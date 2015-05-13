package io.vertx.ext.apex.handler.oauth2;

import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * User: jez
 */
public class OAuth2AuthUrlBuilderTest {

    private static final String TEST_HOST = "1.1.1.1";
    private static final String TEST_PORT = "9090";
    private static final String TEST_AUTH_URL = "/auth_test";
    private static final String TEST_CLIENT_ID = "test_client_id";
    private static final String TEST_REDIRECT_URI = "/test_redirect_uri";
    private static final OAuth2State TEST_STATE = new OAuth2State("http://return.url", "salt", "sessionId");
    private static final String EXPECTED_STATE_VAL = TEST_STATE.toString();
    private static final String TEST_PROTOCOL = "https://";
    private static final String RESPONSE_TYPE_CODE = "code";

    @Test
    public void allFieldsPresentGivesCorrectUrl() throws Exception {
        OAuth2AuthUrlBuilder builder = new OAuth2AuthUrlBuilder();
        builder.setAuthenticationUrl(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_AUTH_URL)
                .setClientId(TEST_CLIENT_ID)
                .setRedirectUri(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_REDIRECT_URI)
                .setState(TEST_STATE);
        String urlAsString = builder.build();
        URL url = new URL(urlAsString);
        assertEquals(url.getHost(), TEST_HOST);
        assertEquals(String.valueOf(url.getPort()), TEST_PORT);
        Map queryParams = extractQueryParams(url);
        assertEquals(queryParams.get("client_id"), TEST_CLIENT_ID);
        assertEquals(queryParams.get("redirect_uri"), TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_REDIRECT_URI);
        assertEquals(queryParams.get("state"), EXPECTED_STATE_VAL);
        assertEquals(queryParams.get("response_type"), RESPONSE_TYPE_CODE);
    }

    @Test(expected=IllegalStateException.class)
    public void nullClientIdThrowsException() {
        OAuth2AuthUrlBuilder builder = new OAuth2AuthUrlBuilder();
        builder.setAuthenticationUrl(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_AUTH_URL)
                .setRedirectUri(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_REDIRECT_URI)
                .setState(TEST_STATE);
        builder.build();
    }

    @Test(expected=IllegalStateException.class)
    public void nullBaseUrlThrowsException() {
        OAuth2AuthUrlBuilder builder = new OAuth2AuthUrlBuilder();
        builder.setClientId(TEST_CLIENT_ID)
                .setRedirectUri(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_REDIRECT_URI)
                .setState(TEST_STATE);
        builder.build();
    }

    @Test(expected=IllegalStateException.class)
    public void nullRedirectUrlThrowsException() {
        OAuth2AuthUrlBuilder builder = new OAuth2AuthUrlBuilder();
        builder.setAuthenticationUrl(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_AUTH_URL)
                .setClientId(TEST_CLIENT_ID)
                .setState(TEST_STATE);
        builder.build();
    }

    @Test(expected=IllegalStateException.class)
    public void nullStateThrowsException() {
        OAuth2AuthUrlBuilder builder = new OAuth2AuthUrlBuilder();
        builder.setAuthenticationUrl(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_AUTH_URL)
                .setClientId(TEST_CLIENT_ID)
                .setRedirectUri(TEST_PROTOCOL + TEST_HOST + ":" + TEST_PORT + TEST_REDIRECT_URI);
        builder.build();
    }

    private Map extractQueryParams(URL url) {
        String query = url.getQuery();
        String[] queryPairs = query.split("&");
        return Arrays.stream(queryPairs)
                .map(s -> s.split("="))
                .collect(Collectors.toMap(sa -> sa[0], sa -> sa[1]));
    }


}