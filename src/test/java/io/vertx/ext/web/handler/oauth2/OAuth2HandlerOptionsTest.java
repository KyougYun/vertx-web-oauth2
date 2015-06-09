package io.vertx.ext.web.handler.oauth2;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for the OAuth2HandlerOptions class.
 */
public class OAuth2HandlerOptionsTest {

  private static final String TEST_CLIENT_ID = "testClient";
  private static final String TEST_CLIENT_SECRET = "testClientSecret";
  private static final String TEST_AUTH_RESULT_HANDLER_URL = "http://test.com:9191/test_auth";
  private static final String TEST_AUTH_RESULT_HANDLER_PATH = "/test_auth";
  private static final String TEST_LOGIN_REDIRECT_URL = "https://test.com/auth";
  private static final String TEST_RETURN_URL_PARAM = "testReturnUrl";
  private static final String TEST_TOKEN_URL = "https://test.com/authToken";

  @Test
  public void setOnlyRequired() {
    OAuth2HandlerOptions options = baseOAuth2HandlerOptions();
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_CLIENT_SECRET, options.clientSecret());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(TEST_TOKEN_URL, options.authTokenUrl());
    assertEquals(AuthTokenRequestProvider.POST, options.authTokenRequestFactoryProvider());
    assertEquals(OAuth2HandlerOptions.DEFAULT_RETURN_URL_PARAM, options.returnUrlParam());
  }

  @Test
  public void testReturnUrlParam() throws Exception {
    OAuth2HandlerOptions options = baseOAuth2HandlerOptions()
            .setReturnUrlParam(TEST_RETURN_URL_PARAM);
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(TEST_RETURN_URL_PARAM, options.returnUrlParam());
  }

  @Test
  public void testOAuth2TokenParam() throws Exception {
    OAuth2HandlerOptions options = baseOAuth2HandlerOptions();
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(OAuth2HandlerOptions.DEFAULT_RETURN_URL_PARAM, options.returnUrlParam());
  }

  private OAuth2HandlerOptions baseOAuth2HandlerOptions() {
    return new OAuth2HandlerOptions(TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_LOGIN_REDIRECT_URL,
            TEST_AUTH_RESULT_HANDLER_URL, TEST_TOKEN_URL);
  }

}