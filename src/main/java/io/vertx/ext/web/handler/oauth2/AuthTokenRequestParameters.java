package io.vertx.ext.web.handler.oauth2;

/**
 * Immutable class used purely to pass all required parameters into the RetrieveAuthTokenHttpMethod (which is a factory
 * for an HttpClientRequest
 */
public class AuthTokenRequestParameters {

  private final String clientId;
  private final String clientSecret;
  private final String redirectUri;
  private final String authTokenUrl;

  public AuthTokenRequestParameters(final String authTokenUrl, final String clientId, final String clientSecret,
                                    final String redirectUri) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.redirectUri = redirectUri;
    this.authTokenUrl = authTokenUrl;
  }

  public String clientId() {
    return clientId;
  }

  public String clientSecret() {
    return clientSecret;
  }

  public String redirectUri() {
    return redirectUri;
  }

  public String authTokenUrl() {
    return authTokenUrl;
  }
}
