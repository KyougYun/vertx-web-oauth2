package io.vertx.ext.apex.handler.oauth2.impl;

import io.vertx.ext.apex.Router;
import io.vertx.ext.apex.RoutingContext;
import io.vertx.ext.apex.Session;
import io.vertx.ext.apex.handler.impl.AuthHandlerImpl;
import io.vertx.ext.apex.handler.oauth2.OAuth2AuthHandler;
import io.vertx.ext.apex.handler.oauth2.OAuth2AuthUrlBuilder;
import io.vertx.ext.apex.handler.oauth2.OAuth2HandlerOptions;
import io.vertx.ext.auth.AuthProvider;

/**
 * User: jez
 */
public class OAuth2AuthHandlerImpl extends AuthHandlerImpl implements OAuth2AuthHandler {

    private final String loginRedirectURL;
    private final String returnURLParam;
    private final String clientId;

    public OAuth2AuthHandlerImpl(AuthProvider authProvider, OAuth2HandlerOptions handlerOptions, Router router) {
        super (authProvider);
        this.loginRedirectURL = handlerOptions.authProviderRedirectUrl();
        this.returnURLParam = handlerOptions.returnUrlParam();
        this.clientId = handlerOptions.clientId();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        Session session = routingContext.session();
        if (session != null) {
            if (session.isLoggedIn()) {
                // Already logged in, just authorise
                authorise(routingContext);
            } else {
                // Now redirect to the login url - we'll get redirected back here after successful login
                session.put(returnURLParam, routingContext.request().path());

                OAuth2AuthUrlBuilder urlBuilder = new OAuth2AuthUrlBuilder()
                        .setAuthenticationUrl(loginRedirectURL)
                        .setClientId(clientId)
                        .setRedirectUri(routingContext.request().absoluteURI());
                String actualRedirect = urlBuilder.build();
                routingContext.response().putHeader("location", actualRedirect).setStatusCode(302).end();
            }
        } else {
            routingContext.fail(new NullPointerException("No session - did you forget to include a SessionHandler?"));
        }

    }

}
