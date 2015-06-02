package io.vertx.ext.web.handler.oauth2;

import static io.vertx.ext.web.handler.oauth2.OAuth2Param.*;

/**
 * Builder class for URLs for oAuth2.0 authentication token retrieval via code submission
 *
 * Note that the following elements of the URL are considered mandatory for this implementation:
 * The base url for the OAuth2 provider for the exchange of code for auth token
 * The client id - the client id with which our application is registered with the OAuth2 provider
 * The client secret - known only to the client application so we can combine this with the id to authenticate ourself
 * with the OAuth2 provider
 * The redirect url - the url to which the auth provider redirected the browser following the authentication attempt
 * which supplied the code
 * The code supplied as a result of the authentication attempt
 *
 */
class OAuth2TokenGetUrlBuilder extends GetUrlBuilder {

  private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
  private final String coreUrl;

  OAuth2TokenGetUrlBuilder(final String baseUri, final String clientId, final String clientSecret, final String redirectUri) {
    // Validate that none of these parameters are null
    validateParamNotNullOrEmpty(baseUri, "Base URI must not be null or empty in OAuth2TokenGetUrlBuilder");
    validateParamNotNullOrEmpty(clientId, "Client id must not be null or empty in OAuth2TokenGetUrlBuilder");
    validateParamNotNullOrEmpty(clientSecret, "Client Secret must not be null or empty in OAuth2TokenGetUrlBuilder");
    validateParamNotNullOrEmpty(redirectUri, "Redirect URI must not be null or empty in OAuth2TokenGetUrlBuilder");

    final StringBuilder builder = new StringBuilder(baseUri);

    appendFirstParam(builder, CLIENT_ID, clientId);
    appendSubsequentParam(builder, GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
    appendSubsequentParam(builder, CLIENT_SECRET, clientSecret);
    appendSubsequentParam(builder, REDIRECT_URI, redirectUri);
    coreUrl = builder.toString();
  }

  public String build(final String code) {
    validateParamNotNullOrEmpty(code, "Code must not be null or empty in OAuth2TokenGetUrlBuilder");
    final StringBuilder builder = new StringBuilder(coreUrl);
    appendSubsequentParam(builder, CODE, code);
    return builder.toString();
  }

}
