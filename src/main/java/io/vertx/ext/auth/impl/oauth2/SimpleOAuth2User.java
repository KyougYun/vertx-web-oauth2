package io.vertx.ext.auth.impl.oauth2;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.oauth2.impl.SimpleOAuth2AuthProviderImpl;

/**
 * Trivial User implementation for OAuth2 authentication only. All role requests for now return true - we may need
 * to look at building in more complex authorization/permission processing later, but this is a simple implementation
 * just to get an external authentication working
 */
public class SimpleOAuth2User implements User {

  public static final String FIELD_TOKEN = "token";
  private SimpleOAuth2AuthProviderImpl authProvider;
  private String token;

  public SimpleOAuth2User() {
  }

  public SimpleOAuth2User(final String token, final SimpleOAuth2AuthProviderImpl authProvider) {
    this.authProvider = authProvider;
    this.token = token;
  }

  @Override
  public User isAuthorised(String s, Handler<AsyncResult<Boolean>> handler) {
    // Simple OAuth2 authentication doesn't currently concern itself with permissions for a user
    handler.handle(Future.succeededFuture());
    return this;
  }

  @Override
  public User clearCache() {
    return this;
  }

  @Override
  public JsonObject principal() {
    return new JsonObject().put(FIELD_TOKEN, token);
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {
    if (authProvider instanceof SimpleOAuth2AuthProviderImpl) {
      this.authProvider = (SimpleOAuth2AuthProviderImpl)authProvider;
    } else {
      throw new IllegalArgumentException("Not a JDBCAuthImpl");
    }
  }
}
