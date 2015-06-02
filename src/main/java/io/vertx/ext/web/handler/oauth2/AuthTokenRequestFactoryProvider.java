package io.vertx.ext.web.handler.oauth2;

import io.netty.handler.codec.http.QueryStringEncoder;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enum class to represent possible HTTP methods for converting an authorization code to an OAuth2 auth token.
 * Each enum instance exposes a method to get a request, given an HttpClient instance - and this request will be
 * ready to decorate with a response handler (i.e. all parameters and/or body will have been set, so all that remains
 * is to add a handler and submit the request. Each member of this enumeration provides a factory function for an auth
 * token retrieval request, given a set of parameters provided as an AuthTokenRequestParameters object.
 *
 */
public enum AuthTokenRequestFactoryProvider implements AuthTokenRequestStrategy {

  GET(params -> {
    final OAuth2TokenGetUrlBuilder builder = new OAuth2TokenGetUrlBuilder(params.authTokenUrl(), params.clientId(),
      params.clientSecret(), params.redirectUri());
    return (httpClient, authCode) -> {
      final HttpClientRequest request = httpClient.getAbs(builder.build(authCode));
      request.handler(params.responseHandler());
      return request;
    };
  }),
  POST(params ->
    (httpClient, authCode) -> {
      final HttpClientRequest request = httpClient.postAbs(params.authTokenUrl());
      request.handler(params.responseHandler());
      QueryStringEncoder enc = new QueryStringEncoder("");
      enc.addParam(OAuth2Param.CLIENT_ID.paramName(), params.clientId());
      enc.addParam(OAuth2Param.CLIENT_SECRET.paramName(), params.clientSecret());
      enc.addParam(OAuth2Param.GRANT_TYPE.paramName(), Constants.GRANT_TYPE_AUTHORIZATION_CODE);
      enc.addParam(OAuth2Param.REDIRECT_URI.paramName(), params.redirectUri());
      enc.addParam(OAuth2Param.CODE.paramName(), authCode);
      request.write(Buffer.buffer(enc.toString()));
      return request;
    });

  // Function which when given a set of parameters which remain the same for each authorization code request
  // (client secret, client id etc) generates a factory function to return a ready to end HttpClientRequest from
  // an HttpClient
  private final Function<AuthTokenRequestParameters, BiFunction<HttpClient, String, HttpClientRequest>> provider;

  AuthTokenRequestFactoryProvider(final Function<AuthTokenRequestParameters, BiFunction<HttpClient, String, HttpClientRequest>> provider) {
    this.provider = provider;
  }

  @Override
  public BiFunction<HttpClient, String, HttpClientRequest> factory(AuthTokenRequestParameters params) {
    return provider.apply(params);
  }

  private static class Constants {
    public static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
  }
}
