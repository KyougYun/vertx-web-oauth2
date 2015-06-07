package io.vertx.ext.web.handler.oauth2;

import io.netty.handler.codec.http.QueryStringEncoder;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static io.vertx.core.http.HttpHeaders.*;
import static io.vertx.ext.web.handler.oauth2.OAuth2Param.*;

/**
 * Enum class to represent possible HTTP methods for converting an authorization code to an OAuth2 auth token.
 * Each enum instance exposes a method to get a request, given an HttpClient instance - and this request will be
 * ready to decorate with a response handler (i.e. all parameters and/or body will have been set, so all that remains
 * is to add a handler and submit the request. Each member of this enumeration provides a factory function for an auth
 * token retrieval request, given a set of parameters provided as an AuthTokenRequestParameters object.
 *
 */
public enum AuthTokenRequestProvider implements AuthTokenRequestStrategy {

  GET {
    @Override
    public BiFunction<HttpClient, String, HttpClientRequest> factory(AuthTokenRequestParameters params) {
      final OAuth2TokenGetUrlBuilder builder = new OAuth2TokenGetUrlBuilder(params.authTokenUrl(), params.clientId(),
        params.clientSecret(), params.redirectUri());
      return (httpClient, authCode) -> {
        final HttpClientRequest request = httpClient.getAbs(builder.build(authCode));
        return request;
      };
    }

    @Override
    public BiConsumer<HttpClientRequest, String> bodyWriter(AuthTokenRequestParameters params) {
      return (request, authCode) -> {};
    }
  },
  POST {
    @Override
    public BiFunction<HttpClient, String, HttpClientRequest> factory(AuthTokenRequestParameters params) {
      return (httpClient, authCode) -> httpClient.postAbs(params.authTokenUrl());
    }

    @Override
    public BiConsumer<HttpClientRequest, String> bodyWriter(AuthTokenRequestParameters params) {
      return (request, authCode) -> {
        request.headers().add(CONTENT_TYPE,"application/x-www-form-urlencoded; charset=utf-8");
        request.setChunked(true);
        QueryStringEncoder enc = new QueryStringEncoder("");
        enc.addParam(CLIENT_ID.paramName(), params.clientId());
        enc.addParam(CLIENT_SECRET.paramName(), params.clientSecret());
        enc.addParam(GRANT_TYPE.paramName(), Constants.GRANT_TYPE_AUTHORIZATION_CODE);
        enc.addParam(REDIRECT_URI.paramName(), params.redirectUri());
        enc.addParam(CODE.paramName(), authCode);
        request.write(Buffer.buffer(enc.toString().substring(1)));
    };
    }
  };

  private static class Constants {
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
  }
}
