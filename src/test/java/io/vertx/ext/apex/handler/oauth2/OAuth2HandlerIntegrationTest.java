package io.vertx.ext.apex.handler.oauth2;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.impl.oauth2.SimpleOAuth2Provider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.oauth2.OAuth2HandlerOptions;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.test.core.VertxTestBase;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: jez
 */
public class OAuth2HandlerIntegrationTest extends VertxTestBase {

  private static final String TEST_CLIENT_ID = "testClient";
  private static final String TEST_CLIENT_SECRET = "testClientSecret";
  private static final String TEST_OAUTH2_SUCCESS_URL = "http://localhost:9292/authSuccess";
  private static final String TEST_OAUTH2_TOKEN_URL = "http://localhost:9292/authToken";
  public static final String APPLICATION_SERVER = "http://localhost:8080";
  private static final String AUTH_RESULT_HANDLER_URL = APPLICATION_SERVER + "/authResult";
  private static final String SESSION_PARAM_TOKEN = "testOAuth2Token";

  // This will be our session cookie header for use by requests
  protected AtomicReference<String> sessionCookie = new AtomicReference<>();

  @Before
  public void startOAuth2ProviderMimic() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    vertx.deployVerticle(OAuth2ProviderMimic.class.getName(), result -> latch.countDown());
    latch.await(2, TimeUnit.SECONDS);
  }

  @Test
  public void testSuccessfulOAuth2Login() throws Exception {
    startWebServer();
    HttpClient client = vertx.createHttpClient();
    // Attempt to get a private url
    final HttpClientRequest successfulRequest = client.get(8080, "localhost", "/private/index.html");
    successfulRequest.handler(resp -> {
      // First we expect a redirect to our handler
      assertEquals(302, resp.statusCode());
      final String setCookie = resp.headers().get("set-cookie");
      assertNotNull(setCookie);
      sessionCookie.set(setCookie); // We're going to want to use this subsequently
      final String redirectToUrl = resp.getHeader("location");
      redirectToUrl(redirectToUrl, client, redirectResponse -> {
        assertEquals(302, redirectResponse.statusCode());
        String postAuthRedirectionUrl = redirectResponse.getHeader("location");
        redirectToUrl(postAuthRedirectionUrl, client, postAuthRedirectResponse -> {
          assertEquals(302, postAuthRedirectResponse.statusCode()); // should redirect us back to our original url, but this time we should get there ok
          postAuthRedirectResponse.bodyHandler(body -> System.out.println(body.toString()));
          System.out.println("Post-auth: " + postAuthRedirectionUrl);
          testComplete();
        });
      });
    });
    successfulRequest.end();

    await(1, TimeUnit.SECONDS);
  }

  private void redirectToUrl(final String redirectUrl, final HttpClient client, final Handler<HttpClientResponse> resultHandler) {
    URL url;
    try {
      url = new URL(redirectUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not decode url");
    }
    final HttpClientRequest request = client.getAbs(redirectUrl.toString());
    getSessionCookie(redirectUrl).ifPresent(cookie -> request.putHeader("cookie", cookie));
    request.handler(resultHandler);
    request.end();
  }

  private Optional<String> getSessionCookie(final String url) {
    return url.startsWith(APPLICATION_SERVER) ? Optional.ofNullable(sessionCookie.get()) : Optional.empty();
  }

  private void startWebServer() {
    HttpServer server = vertx.createHttpServer();
    SessionStore sessionStore = sessionStore();

    Router router = Router.router(vertx);

    router.route().handler(CookieHandler.create());
    router.route().handler(sessionHandler(sessionStore));

    AuthProvider authProvider = SimpleOAuth2Provider.create(SESSION_PARAM_TOKEN, sessionStore);
    router.route("/private/*").handler(OAuth2AuthHandler.create(authProvider,
      new OAuth2HandlerOptions(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_OAUTH2_SUCCESS_URL, AUTH_RESULT_HANDLER_URL, TEST_OAUTH2_TOKEN_URL
      ), router, vertx));


    router.route().handler(routingContext -> {

      // This handler will be called for every request
      HttpServerResponse response = routingContext.response();
      response.putHeader("content-type", "text/plain");

      // Write to the response and end it
      response.end("Hello World from Apex!");
    });

    server.requestHandler(router::accept).listen(8080);
  }

  private SessionHandler sessionHandler(SessionStore sessionStore) {
    return SessionHandler.create(sessionStore).setSessionCookieName("oAuth2Consumer.session");
  }

  private LocalSessionStore sessionStore() {
    return LocalSessionStore.create(vertx);
  }

}
