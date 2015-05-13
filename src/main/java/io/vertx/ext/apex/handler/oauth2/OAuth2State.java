package io.vertx.ext.apex.handler.oauth2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to generate the state parameter for an OAuth2 authentication request, knowing a specific set of parameters.
 * For our apex OAuth2 implementation, these parameters will be held within the user session, so that we can
 * re-generate the state parameter from session data and compare with the state parameter echoed back at us by
 * the OAuth2 authentication provider in our authentication result handling, thus helping protect us from attack
 */
class OAuth2State {

  /**
   * The delimiter we'll use when hashing our parameters to a single state parameter
   */
  private static final String DELIMITER = "|";

  /**
   * The url requested by the user which triggered the authentication check. This would normally be a private URL within
   * our application. It's not always possible to predict which URL a given user will try to hit, so this is a good
   * candidate as part of the state.
   */
  private final String requestedUrl;

  /**
   * A salt we generate and store in the user session prior to redirecting. This will be used to re-generate the state
   * parameter to validate successful authentication results
   */
  private final String salt;

  /**
   * The session id for the current session. I considered just using this rather than adding a salt, and use of this id
   * as part of the hashing process may be superfluous given that we're also salting.
   */
  private final String sessionId;

  /**
   * Constructor with all the required fields
   * @param returnUrl - the url which triggered the authentication request we're processing
   * @param salt - the salt which we generate and store in the user session prior to redirecting to the OAuth2 provider
   * @param sessionId - the current session id
   */
  public OAuth2State(final String returnUrl, final String salt, final String sessionId) {
    this.requestedUrl = returnUrl;
    this.salt = salt;
    this.sessionId = sessionId;
  }

  /**
   * Take the three parameters provided and hash them together. This hash should give us a reproducible state value which
   * we can use to decorate our redirect to the OAuth2 server, and also validate using the session contents as part
   * of our validation of the OAuth2 authentication result (since the state parameter is echoed back to us by the
   * OAuth2 authenticator)
   * @return the state hash to be used to decorate the redirect
   */
  public String toString() {

    // Hash the state object
    StringBuilder buffer = new StringBuilder()
      .append(salt)
      .append(DELIMITER)
      .append(sessionId)
      .append(DELIMITER)
      .append(requestedUrl);
    try {
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] hashedBytes = digest.digest(buffer.toString().getBytes("UTF-8"));
      return convertByteArrayToHexString(hashedBytes);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
      throw new RuntimeException("Could not generate State hash for oAuth2 authentication");
    }


  }

  private static String convertByteArrayToHexString(byte[] arrayBytes) {
    StringBuilder buffer = new StringBuilder();
    for (byte arrayByte : arrayBytes) {
      buffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16)
        .substring(1));
    }
    return buffer.toString();
  }
}
