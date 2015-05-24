package io.vertx.ext.auth.impl.oauth2;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.impl.oauth2.impl.SimpleOAuth2AuthProviderImpl;

import java.util.Set;

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
  public User hasRole(String role, Handler<AsyncResult<Boolean>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(true));
    return this;
  }

  @Override
  public User hasPermission(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(true));
    return this;
  }

  @Override
  public User hasRoles(Set<String> roles, Handler<AsyncResult<Boolean>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(true));
    return this;
  }

  @Override
  public User hasPermissions(Set<String> permissions, Handler<AsyncResult<Boolean>> resultHandler) {
    resultHandler.handle(Future.succeededFuture(true));
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
