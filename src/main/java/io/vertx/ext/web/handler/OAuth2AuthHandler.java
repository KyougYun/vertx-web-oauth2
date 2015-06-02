package io.vertx.ext.web.handler;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.impl.OAuth2AuthHandlerImpl;
import io.vertx.ext.web.handler.oauth2.OAuth2HandlerOptions;

/**
 * User: jez
 */
public interface OAuth2AuthHandler extends AuthHandler {


    /**
     * Create a handler
     *
     * @param authProvider  the auth service to use
     * @param options Options for configuring the OAuth2Handler
     * @param router the apex router to which we will add our post-authentication handler
     * @return the handler
     */
    static AuthHandler create(AuthProvider authProvider, OAuth2HandlerOptions options, Router router, Vertx vertx) {
        return new OAuth2AuthHandlerImpl(authProvider, options, router, vertx);
    }

}
