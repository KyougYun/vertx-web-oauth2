package io.vertx.ext.web.handler.oauth2;

/**
 * Builder class for URLs for oAuth2.0 authentication redirection
 * Note that the following elements of the URL are considered mandatory for this implementation:
 * The authentication redirect url - the uri through which the OAuth2 provider exposes its authentication
 * The client id - the client id with which our application is registered with the OAuth2 provider
 * The redirect url - the url to which the auth provider will redirect the browser following the authentication attempt
 *  - we will expose this url and handle the authentication result
 * The state parameter object (which will be converted to a state parameter value),
 *
 * The response type will always be set as code, at least for now (we will convert it to a token as part of validation)
 * Scope is not required for now at least - we will default it
 */
public class OAuth2AuthUrlBuilder {

  private static final String PARAM_KEY_VAL_SEPARATOR = "=";
  private static final String CLIENT_ID_PARAM = "client_id";
  private static final String RESPONSE_TYPE_PARAM = "response_type";
  private static final String REDIRECT_URI_PARAM = "redirect_uri";
  private static final String RESPONSE_TYPE_CODE = "code";
  private static final String STATE_PARAM = "state";
  private static final String FIRST_PARAM_DELIMITER = "?";
  private static final String SUBSEQUENT_PARAM_DELIMITER = "&";

  /**
   * The url through which the OAuth2 provider exposes its authentication, this is the url to which we redirect
   * authentication.
   */
  private String authenticationUrl = null;

  /**
   * The client id for this application as registered with the OAuth2 provider
   */
  private String clientId = null;

  /**
   * The URL for the post-authentication handler (which will validate the state and returned code and determine
   * whether to permit access)
   */
  private String redirectUri = null;

  /**
   * The OAuth2 state object we will use to generate the state parameter contents. The state parameter contents
   * should be unguessable by potential attackers and should be echoed back with the auth result so that we can
   * validate it.
   */
  private OAuth2State state = null;

  /**
   * Set the client id with which we are registered with the OAuth2 provider
   * @param newClientId - the client id to use
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2AuthUrlBuilder setClientId(final String newClientId) {
    clientId = newClientId;
    return this;
  }

  /**
   * Set the authentication url exposed by the OAuth2 provider
   * @param newBaseUrl - the authentication url to use
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2AuthUrlBuilder setAuthenticationUrl(final String newBaseUrl) {
    authenticationUrl = newBaseUrl;
    return this;
  }

  /**
   * Set the auth result handler - in OAuth2 the redirect uri, which we will use to handle the result of authorization
   * when the OAuth2 provider redirects to it
   * @param newRedirectUri - the redirect uri
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2AuthUrlBuilder setRedirectUri(final String newRedirectUri) {
    redirectUri = newRedirectUri;
    return this;
  }

  /**
   * Set the state - this will be converted into a state parameter in the OAuth2 redirect and will be echoed back to
   * us, ultimately being re-validated to ensure the redirect to our handler is derived from our original redirect
   * to the authenticator
   * @param state - the state object to be used to generate the state parameter
   * @return a reference to this, so the API can be used fluently
   */
  public OAuth2AuthUrlBuilder setState(final OAuth2State state) {
    this.state = state;
    return this;
  }

  /**
   * Generate the full redirect url for authentication, validating that required parameters are present
   * @return String - the redirect url for authentication, including our set of parameters
   */
  public final String build() {
    validateMemberNotNullOrEmpty(authenticationUrl, "Base authorization url must not be null or empty in OAuth2AuthUrlBuilder");
    validateMemberNotNullOrEmpty(clientId, "Client id must not be null or empty in OAuth2AuthUrlBuilder");
    validateMemberNotNullOrEmpty(redirectUri, "Redirect URI must not be null or empty in OAuth2AuthUrlBuilder");
    validateMemberNotNull(state, "State must not be null in OAuth2AuthUrlBuilder");

    final StringBuilder builder = new StringBuilder(authenticationUrl);
    appendFirstParam(builder, CLIENT_ID_PARAM, clientId);
    appendSubsequentParam(builder, REDIRECT_URI_PARAM, redirectUri);
    appendSubsequentParam(builder, STATE_PARAM, state.toString());
    appendSubsequentParam(builder, RESPONSE_TYPE_PARAM, RESPONSE_TYPE_CODE);
    return builder.toString();
  }

  private void appendFirstParam(final StringBuilder builder, final String paramName, final String paramValue) {
    appendParam(builder, FIRST_PARAM_DELIMITER, paramName, paramValue);
  }

  private void appendSubsequentParam(final StringBuilder builder, final String paramName, final String paramValue) {
    appendParam(builder, SUBSEQUENT_PARAM_DELIMITER, paramName, paramValue);
  }

  private void appendParam(final StringBuilder builder, final String delimiter, final String paramName, final String paramValue) {
    builder.append(delimiter).append(paramName).append(PARAM_KEY_VAL_SEPARATOR).append(paramValue);
  }

  private void validateMemberNotNullOrEmpty(final String requiredValue, final String failureMsg) {
    if (requiredValue == null || requiredValue.length() == 0) {
      throw new IllegalStateException(failureMsg);
    }
  }

  private void validateMemberNotNull(final Object requiredValue, final String failureMsg) {
    if (requiredValue == null) {
      throw new IllegalStateException(failureMsg);
    }
  }

}
