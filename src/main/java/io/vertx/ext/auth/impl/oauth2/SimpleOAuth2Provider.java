package io.vertx.ext.auth.impl.oauth2;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.impl.oauth2.impl.SimpleOAuth2AuthProviderImpl;
import io.vertx.ext.web.sstore.SessionStore;

/**
 * User: jez
 */
public interface SimpleOAuth2Provider extends AuthProvider {

  static SimpleOAuth2Provider create(final String tokenParamName, final SessionStore sessionStore) {
    return new SimpleOAuth2AuthProviderImpl(tokenParamName, sessionStore);
  }

}
