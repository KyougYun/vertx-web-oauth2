package io.vertx.ext.web.handler.impl;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.oauth2.OAuth2AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.oauth2.AuthTokenRequestParameters;
import io.vertx.ext.web.handler.oauth2.OAuth2AuthUrlBuilder;
import io.vertx.ext.web.handler.oauth2.OAuth2HandlerOptions;
import io.vertx.ext.web.handler.oauth2.OAuth2Param;
import io.vertx.ext.web.handler.oauth2.OAuth2State;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * User: jez
 */
public class OAuth2AuthHandlerImpl extends AuthHandlerImpl implements OAuth2AuthHandler {

  private static final String OAUTH2_STATE_SALT = "OAUTH2_STATE_SALT";

  private final String loginRedirectURL;
  private final String returnURLParam;
  private final String authResultHandlerUrl;
  private final String clientId;
  private final AuthTokenRequestor authTokenRequestor;
  private final HttpClient httpClient;
  private final BiConsumer<RoutingContext, String> tokenHandler;

  public OAuth2AuthHandlerImpl(AuthProvider authProvider, OAuth2HandlerOptions handlerOptions, Router router, Vertx vertx) {
    super(authProvider);
    if (!(authProvider instanceof OAuth2AuthProvider)) {
      throw new RuntimeException("Auth provider for an OAuth2 handler must implement OAuth2AuthProvider");
    }
    OAuth2AuthProvider oAuth2AuthProvider = (OAuth2AuthProvider) authProvider;
    this.tokenHandler = oAuth2AuthProvider.tokenHandler();
    this.loginRedirectURL = handlerOptions.authProviderRedirectUrl();
    this.returnURLParam = handlerOptions.returnUrlParam();
    this.clientId = handlerOptions.clientId();
    this.authResultHandlerUrl = handlerOptions.authResultHandlerUrl();

    httpClient = vertx.createHttpClient();

    final AuthTokenRequestParameters authTokenRequestParams = new AuthTokenRequestParameters(handlerOptions.authTokenUrl(),
      handlerOptions.clientId(), handlerOptions.clientSecret(), handlerOptions.authResultHandlerUrl());
    authTokenRequestor = new AuthTokenRequestor(handlerOptions.authTokenRequestFactoryProvider(), authTokenRequestParams);

    try {
      final String authResultPath = authResultPath(this.authResultHandlerUrl);
      router.route(HttpMethod.GET, authResultPath).handler(authResultHandler());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Authentication result handler url " + this.authResultHandlerUrl +
        " is not correctly formatted");
    }
  }

  @Override
  public void handle(RoutingContext routingContext) {

    Session session = routingContext.session();
    if (session != null) {
      User user = routingContext.user();
      if (user != null) {
        // Already logged in, just authorise
        authorise(user, routingContext);
      } else {
        // Now check our authprovider to see if we already have a token
        authProvider.authenticate(new JsonObject().put("sessionId", session.id()), res -> {
          if (res.succeeded()) {
            authorise(user, routingContext);
          } else {
            // Now redirect to the login url - we'll get redirected back here after successful login
            session.put(returnURLParam, routingContext.request().absoluteURI());
            String salt = UUID.randomUUID().toString();
            session.put(OAUTH2_STATE_SALT, salt);

            OAuth2AuthUrlBuilder urlBuilder = new OAuth2AuthUrlBuilder()
              .setAuthenticationUrl(loginRedirectURL)
              .setClientId(clientId)
              .setState(new OAuth2State(routingContext.session().get(returnURLParam), salt, routingContext.session().id()))
              .setRedirectUri(authResultHandlerUrl);
            String actualRedirect = urlBuilder.build();
            routingContext.response().putHeader("location", actualRedirect).setStatusCode(302).end();
          }
        });
      }
    } else {
      routingContext.fail(new NullPointerException("No session - did you forget to include a SessionHandler?"));
    }

  }

  private String authResultPath(final String resultHandlerUrl) throws MalformedURLException {
    URL url = new URL(resultHandlerUrl);
    return url.getPath();
  }

  private Handler<RoutingContext> authResultHandler() {
    return rc -> {
      // validate state was the one we stored first, otherwise someone's playing silly sods
      // then attempt to get the auth token for the code
      // if successful we authorize and redirect to the original url as the user is now allowed to see it
      // if not we return a 401
      final String state = rc.request().getParam("state");
      final String stateSalt = rc.session().get(OAUTH2_STATE_SALT);
      final String originalUrl = rc.session().get(returnURLParam);
      final String sessionId = rc.session().id();

      if (state == null ||
        stateSalt == null ||
        originalUrl == null ||
        sessionId == null) {
        rc.fail(401);
      } else {
        OAuth2State expectedState = new OAuth2State(originalUrl, stateSalt, sessionId);
        if (expectedState.toString().equals(state)) {
          final Optional<String> code = Optional.ofNullable(rc.request().getParam(OAuth2Param.CODE.paramName()));
          if (code.isPresent()) {
            authTokenRequestor.invoke(httpClient, code.get(), authTokenResultHandler(tokenHandler, rc));
          } else {
            // TODO: LOG FAILURE CONDITIONS
            rc.fail(401);
          }
        } else {
          rc.fail(401);
        }
      }
    };

  }

  private Handler<HttpClientResponse> authTokenResultHandler(final BiConsumer<RoutingContext, String> tokenHandler, RoutingContext routingContext) {
    return resp -> {
      resp.bodyHandler(body -> {
        JsonObject json = new JsonObject(body.toString());
        Optional<String> token = Optional.ofNullable(json.getString("access_token"));
        if (token.isPresent()) {
          tokenHandler.accept(routingContext, token.get());
          Session session = routingContext.session();
          authProvider.authenticate(new JsonObject().put("sessionId", session.id()), res -> {
            if(res.succeeded()) {
              User user = res.result();
              routingContext.setUser(user);
              String returnURL = session.remove(this.returnURLParam);
              if(returnURL == null) {
                routingContext.fail(new IllegalStateException("Logged in OK, but no return URL"));
              } else {
                routingContext.response().putHeader("location", returnURL).setStatusCode(302).end();
              }
            } else {
              routingContext.fail(403);
            }
          });
        } else {
          // let's do some detailed failure handling here
          System.out.println("Handle failure better");
        }
      });
    };
  }

//  private Handler<String>
}
