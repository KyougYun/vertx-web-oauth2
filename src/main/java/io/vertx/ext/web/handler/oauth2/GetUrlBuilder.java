package io.vertx.ext.web.handler.oauth2;

/**
 * Builder superclass to help with building of URL query portions for HttpClient requests
 */
public class GetUrlBuilder {

  private static final String PARAM_KEY_VAL_SEPARATOR = "=";
  private static final String FIRST_PARAM_DELIMITER = "?";
  private static final String SUBSEQUENT_PARAM_DELIMITER = "&";

  protected void appendFirstParam(final StringBuilder builder, final OAuth2Param param, final String paramValue) {
    appendParam(builder, FIRST_PARAM_DELIMITER, param, paramValue);
  }

  protected void appendSubsequentParam(final StringBuilder builder, final OAuth2Param param, final String paramValue) {
    appendParam(builder, SUBSEQUENT_PARAM_DELIMITER, param, paramValue);
  }

  private void appendParam(final StringBuilder builder, final String delimiter, final OAuth2Param param, final String paramValue) {
    builder.append(delimiter).append(param.paramName()).append(PARAM_KEY_VAL_SEPARATOR).append(paramValue);
  }

  protected void validateParamNotNullOrEmpty(final String requiredValue, final String failureMsg) {
    if (requiredValue == null || requiredValue.length() == 0) {
      throw new IllegalArgumentException(failureMsg);
    }
  }

  protected void validateParamNotNull(final Object requiredValue, final String failureMsg) {
    if (requiredValue == null) {
      throw new IllegalArgumentException(failureMsg);
    }
  }
}
