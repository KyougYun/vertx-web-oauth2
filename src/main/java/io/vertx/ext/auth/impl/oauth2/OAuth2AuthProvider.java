package io.vertx.ext.auth.impl.oauth2;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiConsumer;

/**
 * User: jez
 */
public interface OAuth2AuthProvider extends AuthProvider {
  /**
   * Extension function to determine how to consume an OAuth2 token once successfully obtained - for example do we store it
   * somehow, or unwrap it for authorizations or any other way we'd like to use the token. Note that because one
   * simple approach to use it might be to store within the session or current user, it is necessary to offer the
   * RoutingContext as part of the function interface
   * @return a handler which can consume the routing context and a token
   */
  BiConsumer<RoutingContext, String> tokenHandler();

}
