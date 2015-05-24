package io.vertx.ext.web.handler.oauth2;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Test for the OAuth2HandlerOptions class.
 */
public class OAuth2HandlerOptionsTest {

  private static final String TEST_CLIENT_ID = "TEST_CLIENT";
  private static final String TEST_AUTH_RESULT_HANDLER_URL = "http://test.com:9191/test_auth";
  private static final String TEST_AUTH_RESULT_HANDLER_PATH = "/test_auth";
  private static final String TEST_LOGIN_REDIRECT_URL = "https://test.com/auth";
  private static final String TEST_RETURN_URL_PARAM = "testReturnUrl";
  private static final String TEST_OAUTH2_TOKEN_PARAM = "testOAuth2Token";

  @Test
  public void setOnlyRequired() {
    OAuth2HandlerOptions options = new OAuth2HandlerOptions(TEST_CLIENT_ID, TEST_LOGIN_REDIRECT_URL, TEST_AUTH_RESULT_HANDLER_URL);
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(OAuth2HandlerOptions.DEFAULT_RETURN_URL_PARAM, options.returnUrlParam());
    assertEquals(OAuth2HandlerOptions.DEFAULT_OAUTH2_TOKEN_PARAM, options.tokenParam());
  }

  @Test
  public void testReturnUrlParam() throws Exception {
    OAuth2HandlerOptions options = new OAuth2HandlerOptions(TEST_CLIENT_ID, TEST_LOGIN_REDIRECT_URL, TEST_AUTH_RESULT_HANDLER_URL)
      .setReturnUrlParam(TEST_RETURN_URL_PARAM);
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(TEST_RETURN_URL_PARAM, options.returnUrlParam());
    assertEquals(OAuth2HandlerOptions.DEFAULT_OAUTH2_TOKEN_PARAM, options.tokenParam());
  }

  @Test
  public void testOAuth2TokenParam() throws Exception {
    OAuth2HandlerOptions options = new OAuth2HandlerOptions(TEST_CLIENT_ID, TEST_LOGIN_REDIRECT_URL, TEST_AUTH_RESULT_HANDLER_URL)
      .setTokenParam(TEST_OAUTH2_TOKEN_PARAM);
    assertEquals(TEST_CLIENT_ID, options.clientId());
    assertEquals(TEST_LOGIN_REDIRECT_URL, options.authProviderRedirectUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_URL, options.authResultHandlerUrl());
    assertEquals(TEST_AUTH_RESULT_HANDLER_PATH, options.authResultHandlerPath());
    assertEquals(OAuth2HandlerOptions.DEFAULT_RETURN_URL_PARAM, options.returnUrlParam());
    assertEquals(TEST_OAUTH2_TOKEN_PARAM, options.tokenParam());
  }

}