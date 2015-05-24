package io.vertx.ext.apex.handler.oauth2;

import io.vertx.ext.web.handler.oauth2.OAuth2State;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test for the key desired behaviours of the OAuth2 state object - specifically that the same parameters will
 * generate the same state, and that varying one or more parameters leads to a different state hash.
 */
public class OAuth2StateTest {

  private static final String TEST_URL1 = "http://testUrl1";
  private static final String TEST_URL2 = "http://testUrl12";
  private static final String TEST_SALT1 = "testSalt1";
  private static final String SESSION_ID1 = "sessionId1";
  private static final String TEST_SALT2 = "testSalt2";
  private static final String SESSION_ID2 = "sessionId2";

  @Test
  public void checkSameParametersGiveIdenticalState() throws Exception {
    OAuth2State state1 = new OAuth2State(TEST_URL1, TEST_SALT1, SESSION_ID1);
    OAuth2State state2 = new OAuth2State(TEST_URL1, TEST_SALT1, SESSION_ID1);
    assertEquals(state1.toString(), state2.toString());
  }

  @Test
  public void checkDifferentRequestedUrlsLeadToDifferentStateValues() throws Exception {
    OAuth2State state1 = new OAuth2State(TEST_URL1, TEST_SALT1, SESSION_ID1);
    OAuth2State state2 = new OAuth2State(TEST_URL2, TEST_SALT1, SESSION_ID1);
    assertNotEquals(state1.toString(), state2.toString());
  }

  @Test
  public void checkDifferentSaltsLeadToDifferentStateValues() throws Exception {
    OAuth2State state1 = new OAuth2State(TEST_URL1, TEST_SALT1, SESSION_ID1);
    OAuth2State state2 = new OAuth2State(TEST_URL1, TEST_SALT2, SESSION_ID1);
    assertNotEquals(state1.toString(), state2.toString());
  }

  @Test
  public void checkDifferentSessionIdsLeadToDifferentStateValues() throws Exception {
    OAuth2State state1 = new OAuth2State(TEST_URL1, TEST_SALT1, SESSION_ID1);
    OAuth2State state2 = new OAuth2State(TEST_URL1, TEST_SALT2, SESSION_ID2);
    assertNotEquals(state1.toString(), state2.toString());
  }

}