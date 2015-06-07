package io.vertx.ext.web.handler.oauth2;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Options class to hold the various configuration settings for an OAuth2 handler for the vertx/apex implementation
 *
 *
 */
public class OAuth2HandlerOptions {

  /**
   * Default name of param used to store return url information in session
   */
  static final String DEFAULT_RETURN_URL_PARAM = "return_url";

  /**
   * Default name of param used to store OAuth2 token in session
   */
  static final String DEFAULT_OAUTH2_TOKEN_PARAM = "oauth2_token";

  /**
   * The url to which a user will be redirected for authentication - i.e. the OAuth2 provider's authentication
   * URL (which may also involve a login)
   */
  private final String authProviderRedirectUrl;

  /**
   * The OAuth2 provider's client id for this application
   */
  private final String clientId;

  /**
   * The OAuth2 provider's client secret for this application
   */
  private final String clientSecret;

  /**
   * The URL to which the OAuth2 provider should redirect the client following an authentication attempt,
   * whether successful or otherwise. This URL should deal with the result of authentication, and if the
   * auth was successful, permit the client to proceed appropriately
   */
  private final String authResultHandlerUrl;

  /**
   * The URL to which the OAuth2 handler will submit a request containing the code supplied by the client on
   * redirect to exchange for an OAuth2 token.
   */
  private final String authTokenUrl;

  /**
   * The name of the session parameter to be used to hold the url originally requested by the client which triggered
   * the auth check. If the client was not already authenticated and the authentication is successful, they will be
   * redirected to the url held in this parameter
   */
  private String returnUrlParam = DEFAULT_RETURN_URL_PARAM;

  /**
   * The name of the session parameter to be used to hold the OAuth2 token, if we successfully
   * obtain one
   */
  private String tokenParam = DEFAULT_OAUTH2_TOKEN_PARAM;

  /**
   * The factory provider to be used to create a factory for http requests to convert an authorization code returned
   * from the OAuth2 provider to the associated authorization token
   */
  private AuthTokenRequestProvider authTokenRequestFactoryProvider = AuthTokenRequestProvider.POST;

  /**
   * Constructor to create options from required fields
   * @param clientId the client id obtained for this application from the OAuth2 provider
   * @param loginRedirectUrl the OAuth2 provider's authentication check URL
   * @param authResultHandlerUrl the URL (typically exposed by our application) for handling the authentication result
   */
  public OAuth2HandlerOptions(final String clientId, final String clientSecret,
                              final String loginRedirectUrl, final String authResultHandlerUrl,
                              final String oAuth2TokenUrl) {
    this.authProviderRedirectUrl = loginRedirectUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.authResultHandlerUrl = authResultHandlerUrl;
    this.authTokenUrl = oAuth2TokenUrl;
  }

  /**
   * Set the name of the parameter used for holding the return url within the session
   * @param newParam - name of the return url parameter within the session
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2HandlerOptions setReturnUrlParam(final String newParam) {
    this.returnUrlParam = newParam;
    return this;
  }

  /**
   * Set the name of the parameter used for holding the OAuth2 token within the session
   * @param tokenParam - name of the token parameter within the session
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2HandlerOptions setTokenParam(final String tokenParam) {
    this.tokenParam = tokenParam;
    return this;
  }

  public OAuth2HandlerOptions setAuthTokenRequestFactoryProvider(final AuthTokenRequestProvider provider) {
    this.authTokenRequestFactoryProvider = provider;
    return this;
  }

  /**
   * Retrieve the authentication url for the OAuth2 provider
   * @return the authentication url
   */
  public String authProviderRedirectUrl() {
    return authProviderRedirectUrl;
  }

  /**
   * Retrieve the auth token url for the OAuth2 provider
   * @return the url to hit to get an auth token from a code supplied during authentication
   */
  public String authTokenUrl() {
    return authTokenUrl;
  }

  /**
   * Retrieve the name of the session parameter into which the url the user attempted to access is stored
   * @return the name of the session parameter
   */
  public String returnUrlParam() {
    return returnUrlParam;
  }

  /**
   *
   */
  public String tokenParam() {
    return tokenParam;
  }

  /**
   * Retrieve the name of the client id by which the OAuth2 provider will recognise our application
   * @return the client id
   */
  public String clientId() {
    return clientId;
  }

  /**
   * Retrieve the client secret for use in private communications between our application and the OAuth2 provider
   * @return the client secret
   */
  public String clientSecret() {
    return clientSecret;
  }

  /**
   * Retrieve the url for handling the auth result. This is given to the auth provider as a parameter, and they will
   * then redirect the user to this url following the authentication attempt. Because this is used as a redirect,
   * it must be a full url, not just the local path
   * @return the auth result handler url
   */
  public String authResultHandlerUrl() {
    return authResultHandlerUrl;
  }

  /**
   * Retrieve the token request factory provider which provides the mechanism to create a function which takes an
   * OAuth2 authentication code and converts it into an authentication token
   * @return the configured factory provider which will be used to create the request factory function
   */
  public AuthTokenRequestProvider authTokenRequestFactoryProvider() {
    return authTokenRequestFactoryProvider;
  }

  /**
   * Retrieve the path part url for handling the auth result. This is given to a local handler configuration within
   * the Apex routing subsystem for our application, so that we can handle the result of the authentication attempt
   * @return the auth result handler url
   */
  public String authResultHandlerPath() {
    try {
      URL url = new URL(authResultHandlerUrl);
      return url.getPath();
    } catch (MalformedURLException e) {
      throw new IllegalStateException("Auth result handler URL" + authResultHandlerUrl + " appears to be invalid, in OAuth2HandlerOptions");
    }
  }
}
