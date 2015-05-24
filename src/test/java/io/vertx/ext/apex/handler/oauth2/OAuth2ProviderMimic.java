package io.vertx.ext.apex.handler.oauth2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Http server implementation to mimic an OAuth2 provider's possible authentication outcomes. By default this will
 * be based on the endpoint used for authentication, and we will construct different handlers in the tests to
 * redirect to different endpoints to mimic the possible outcomes.
 */
public class OAuth2ProviderMimic extends AbstractVerticle {

  public static final int OAUTH2PROVIDER_PORT = 9292;
  public static final String OAUTH2_PROVIDER_SUCCESS_ENDPOINT = "/authSuccess";
  public static final String OAUTH2_PROVIDER_TOKEN_ENDPOINT = "/authToken";
  public static final String OAUTH2PROVIDER_UNKNOWN_CLIENT_ENDPOINT = "/unknownClient";
  public static final String OAUTH2PROVIDER_NOT_AUTHENTICATED_ENDPOINT = "/authFailure";
  public static final String OAUTH2PROVIDER_SERVER_ERROR_ENDPOINT = "/serverError";

  private static final String LOCATION_HEADER = "location";

  private Set<String> pendingCodes = new HashSet<>();

  @Override
  public void start() throws Exception {
    vertx.createHttpServer().requestHandler(router()::accept).listen(OAUTH2PROVIDER_PORT);
  }

  private Router router() {
    Router router = Router.router(vertx);
    router.route(HttpMethod.GET, OAUTH2_PROVIDER_SUCCESS_ENDPOINT).handler(authSuccessHandler());
    return router;
  }

  private Handler<RoutingContext> authSuccessHandler() {
    // Create a full URL for redirection

    return rc -> {
      final MultiMap requestParams = rc.request().params();
      final String redirectUrl = requestParams.get("redirect_uri");
      final String responseType = requestParams.get("response_type");
      final String clientId = requestParams.get("client_id");
      final String state = requestParams.get("state");
      // We're not going to be sent scope as we default that
      final StringBuilder sb = new StringBuilder(redirectUrl);
      sb.append("?");
      sb.append("state").append("=").append(state);
      sb.append("&").append("code").append("=").append(newCode());
      System.out.println("*" + sb.toString());
      rc.response().putHeader("location", sb.toString()).setStatusCode(302).end();
    };
  }

  private String newCode() {
    final String newCode = UUID.randomUUID().toString();
    if (pendingCodes.contains(newCode)) {
      return newCode();
    }
    pendingCodes.add(newCode);
    return newCode;
  }
}
