package io.vertx.ext.auth.impl.oauth2.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.oauth2.SimpleOAuth2Provider;
import io.vertx.ext.auth.impl.oauth2.SimpleOAuth2User;
import io.vertx.ext.web.sstore.SessionStore;

import java.util.Objects;
import java.util.Optional;

/**
 * A very simple AuthProvider implementation for OAuth2. It expects a session id in the authInfo and will
 * interrogate the specified session to determine whether or not it contains an OAuth2 token - if so it will
 * assume that authentication has been successful, otherwise it will return a failed result.
 */
public class SimpleOAuth2AuthProviderImpl implements SimpleOAuth2Provider {



  /**
   * The name of the session parameter/field to interrogate for an OAuth2 token
   */
  private final String tokenParamName;

  /**
   * The session store in which to look for the session to interrogate
   */
  private final SessionStore sessionStore;

  /**
   * Create an instance of this authprovider - required parameters are the name of the session parameter to interrogate
   * for the token and the session store to interrogate
   * @param tokenParamName - name of the session parameter to interrogate for the token
   * @param sessionStore - session store to interrogate
   */
  public SimpleOAuth2AuthProviderImpl(final String tokenParamName, final SessionStore sessionStore) {
    Objects.requireNonNull(tokenParamName, "Token parameter name must not be null");
    Objects.requireNonNull(sessionStore, "Session store must not be null");
    this.tokenParamName = tokenParamName;
    this.sessionStore = sessionStore;
  }

  @Override
  public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
    sessionStore.get(authInfo.getString("sessionId"), sessionResult -> {
      if (sessionResult.succeeded()) {
        Optional<String> tokenOption = Optional.ofNullable(sessionResult.result())
          .flatMap(session -> Optional.ofNullable(session.get(tokenParamName)));
        if (tokenOption.isPresent()) {
          resultHandler.handle(Future.succeededFuture(new SimpleOAuth2User(tokenOption.get(), this)));
        } else {
          resultHandler.handle(Future.failedFuture("Token not present"));
        }
      } else {
        resultHandler.handle(Future.failedFuture("Session could not be found"));
      }
    });
  }

}
