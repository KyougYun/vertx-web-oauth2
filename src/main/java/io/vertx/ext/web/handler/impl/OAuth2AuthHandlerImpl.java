package io.vertx.ext.web.handler.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.handler.oauth2.OAuth2AuthUrlBuilder;
import io.vertx.ext.web.handler.oauth2.OAuth2HandlerOptions;
import io.vertx.ext.web.handler.oauth2.OAuth2State;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.OAuth2AuthHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * User: jez
 */
public class OAuth2AuthHandlerImpl extends AuthHandlerImpl implements OAuth2AuthHandler {

  private static final String OAUTH2_STATE_SALT = "OAUTH2_STATE_SALT";

  private final String loginRedirectURL;
  private final String returnURLParam;
  private final String authResultHandlerUrl;
  private final String clientId;
  private final String tokenParam;

  public OAuth2AuthHandlerImpl(AuthProvider authProvider, OAuth2HandlerOptions handlerOptions, Router router) {
    super (authProvider);
    this.loginRedirectURL = handlerOptions.authProviderRedirectUrl();
    this.returnURLParam = handlerOptions.returnUrlParam();
    this.clientId = handlerOptions.clientId();
    this.authResultHandlerUrl = handlerOptions.authResultHandlerUrl();
    this.tokenParam = handlerOptions.tokenParam();

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
      User user = UserHolder.getUser(authProvider, routingContext);
      if (user != null) {
        // Already logged in, just authorise
        authorise(user, routingContext);
      } else {
        // Now redirect to the login url - we'll get redirected back here after successful login
        session.put(returnURLParam, routingContext.request().path());
        String salt = UUID.randomUUID().toString();
        session.put(OAUTH2_STATE_SALT, salt);

        OAuth2AuthUrlBuilder urlBuilder = new OAuth2AuthUrlBuilder()
          .setAuthenticationUrl(loginRedirectURL)
          .setClientId(clientId)
          .setState(new OAuth2State(returnURLParam, salt, routingContext.session().id()))
          .setRedirectUri(authResultHandlerUrl);
        String actualRedirect = urlBuilder.build();
        routingContext.response().putHeader("location", actualRedirect).setStatusCode(302).end();
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
        rc.fail(401); // unauthorised - should we accept a file to write out for this as an                           // alternative?
      } else {
        OAuth2State expectedState = new OAuth2State(originalUrl, stateSalt, sessionId);
        if (expectedState.toString().equals(state)) {
        } else {
          rc.fail(401);
        }
      }
    };
  }

}