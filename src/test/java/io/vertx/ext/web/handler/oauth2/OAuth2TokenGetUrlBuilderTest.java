package io.vertx.ext.web.handler.oauth2;

import io.vertx.ext.apex.handler.oauth2.UrlBuildingTest;
import org.junit.Test;

import java.net.URL;
import java.util.Map;

import static io.vertx.ext.web.handler.oauth2.OAuth2Param.*;
import static org.junit.Assert.*;

public class OAuth2TokenGetUrlBuilderTest extends UrlBuildingTest {

  private static final String TEST_CLIENT_ID = "testClientId";
  private static final String TEST_URL = "http://test.com";
  private static final String TEST_CLIENT_SECRET = "testClientSecret";
  private static final String TEST_CODE = "testCode";
  public static final String TEST_HOST = "test.com";

  @Test(expected = IllegalArgumentException.class)
  public void nullBaseUrlTriggersException() {
    new OAuth2TokenGetUrlBuilder(null, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullClientIdTriggersException() {
    new OAuth2TokenGetUrlBuilder(TEST_URL, null, TEST_CLIENT_SECRET, TEST_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullClientSecretTriggersException() {
    new OAuth2TokenGetUrlBuilder(TEST_URL, TEST_CLIENT_ID, null, TEST_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullRedirectUriTriggersException() {
    new OAuth2TokenGetUrlBuilder(TEST_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, null);
  }

  @Test
  public void successfulConstruction() {
    new OAuth2TokenGetUrlBuilder(TEST_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildWithNullCodeTriggersException  () {
    new OAuth2TokenGetUrlBuilder(TEST_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_URL).build(null);
  }

  @Test
  public void buildWitValidCode() throws Exception {
    final OAuth2TokenGetUrlBuilder builder = new OAuth2TokenGetUrlBuilder(TEST_URL, TEST_CLIENT_ID, TEST_CLIENT_SECRET, TEST_URL);
    URL url = new URL(builder.build(TEST_CODE));
    assertEquals(url.getHost(), TEST_HOST);
    Map<String, String> queryParams = extractQueryParams(url);
    assertEquals(TEST_CLIENT_ID, queryParams.get(CLIENT_ID.paramName()));
    assertEquals(TEST_URL, queryParams.get(REDIRECT_URI.paramName()));
    assertEquals(TEST_CLIENT_SECRET, queryParams.get(CLIENT_SECRET.paramName()));
    assertEquals(TEST_CODE, queryParams.get(CODE.paramName()));
  }
}