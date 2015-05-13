package io.vertx.ext.apex.handler.oauth2;

import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.handler.AuthHandler;
import io.vertx.ext.apex.handler.oauth2.impl.OAuth2AuthHandlerImpl;
import io.vertx.ext.auth.AuthProvider;

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
    static AuthHandler create(AuthProvider authProvider, OAuth2HandlerOptions options, Router router) {
        return new OAuth2AuthHandlerImpl(authProvider, options, router);
    }

}
