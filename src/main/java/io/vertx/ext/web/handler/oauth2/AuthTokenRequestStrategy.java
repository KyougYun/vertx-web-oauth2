package io.vertx.ext.web.handler.oauth2;

import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;

import java.util.function.BiFunction;

/**
 * Interface to represent a strategy for retrieving an OAuth2 authentication token from an OAuth2 authentication
 * provider given the code.
 *
 * This will always occur via an Http request, so the strategy exposes a single method which serves as a factory
 * function for a vertx HttpClientRequest, the intent being that all "static" elements of the handling (e.g. the
 * url for retrieving the authentication token) which do not vary from request to request are provided to the strategy
 * and are used in the creation of the factory function (which will be reused for each token retrieval call to that
 * provider)
 *
 * Those static parameters will be provided as an AuthTokenRequestParameters instance
 */
public interface AuthTokenRequestStrategy {

  /**
   * Create a factory function based on the static parameters for a given authentication provider, as provided
   * in the params instance
   * @param params - the static parameters which do not vary from token retrieval to token retrieval for a given
   *                 OAuth2 provider
   * @return - the factory function
   */
  public BiFunction<HttpClient, String, HttpClientRequest> factory(AuthTokenRequestParameters params);

}
