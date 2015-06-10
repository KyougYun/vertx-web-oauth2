package io.vertx.ext.web.handler.oauth2;

/**
 * Enumeration for parameter names used in OAuth2 requests. Made public to permit reuse in external OAuth2 code
 */
public enum OAuth2Param {

  CLIENT_ID("client_id"),
  GRANT_TYPE("grant_type"),
  CLIENT_SECRET("client_secret"),
  REDIRECT_URI("redirect_uri"),
  CODE("code"),
  STATE("state"),
  RESPONSE_TYPE("response_type");

  private final String paramName;

  OAuth2Param(String paramName) {
    this.paramName = paramName;
  }

  public String paramName() {
    return paramName;
  }

}
