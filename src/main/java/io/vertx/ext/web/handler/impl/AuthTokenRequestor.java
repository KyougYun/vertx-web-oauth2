package io.vertx.ext.web.handler.impl;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.web.handler.oauth2.AuthTokenRequestParameters;
import io.vertx.ext.web.handler.oauth2.AuthTokenRequestStrategy;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * User: jez
 */
class AuthTokenRequestor {

  private final BiFunction<HttpClient, String, HttpClientRequest> requestFactory;
  private final BiConsumer<HttpClientRequest, String> bodyWriter;

  public AuthTokenRequestor(final AuthTokenRequestStrategy strategy, final AuthTokenRequestParameters params) {
    requestFactory = strategy.factory(params);
    bodyWriter = strategy.bodyWriter(params);
  }

  public void invoke(final HttpClient client, final String authCode, final Handler<HttpClientResponse> responseHandler) {
    final HttpClientRequest request = requestFactory.apply(client, authCode);
    request.handler(responseHandler);
    bodyWriter.accept(request, authCode);
    request.end();
  }
}
