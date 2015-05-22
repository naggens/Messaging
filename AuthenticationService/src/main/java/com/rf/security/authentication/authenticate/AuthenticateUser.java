package com.rf.security.authentication.authenticate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.rf.security.authentication.dao.AuthenticationDAO;
import com.rf.security.authentication.domain.UserBO;

public class AuthenticateUser {

	private static final Log log = LogFactory.getLog(AuthenticateUser.class);
	private final static int ITERATION_NUMBER = 1000;

	private Connection connection = null;

	private AuthenticationDAO authenticationDAO;

	public AuthenticateUser() {
	}

	/**
	 * Authenticates the user with a given login and password If password and/or
	 * login is null then always returns false. If the user does not exist in
	 * the database returns false.
	 * 
	 * @param con
	 *            Connection An open connection to a databse
	 * @param login
	 *            String The login of the user
	 * @param password
	 *            String The password of the user
	 * @return boolean Returns true if the user is authenticated, false
	 *         otherwise
	 * @throws SQLException
	 *             If the database is inconsistent or unavailable ( (Two users
	 *             with the same login, salt or digested password altered etc.)
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm SHA-1 is not supported by the JVM
	 */
	public boolean authenticate(String login, String password)
			throws SQLException, NoSuchAlgorithmException {
		boolean authenticated = false;

		try {
			boolean userExist = true;
			// INPUT VALIDATION
			if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
				// TIME RESISTANT ATTACK
				// Computation time is equal to the time needed by a legitimate
				// user
				userExist = false;
				login = "";
				password = "";
			}

			UserBO userBO = authenticationDAO.getUser(login);
			String digest, salt;
			if (userBO != null) {
				digest = userBO.getPassword();
				salt = userBO.getSalt();
				// DATABASE VALIDATION
				if (StringUtils.isEmpty(digest) || StringUtils.isEmpty(salt)) {
					throw new SQLException(
							"Database inconsistant Salt or Digested Password altered");
				}

			} else { // TIME RESISTANT ATTACK (Even if the user does not exist
						// the
				// Computation time is equal to the time needed for a legitimate
				// user
				digest = "000000000000000000000000000=";
				salt = "00000000000=";
				userExist = false;
			}

			byte[] bDigest = base64ToByte(digest);
			byte[] bSalt = base64ToByte(salt);

			// Compute the new DIGEST
			byte[] proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);

			return Arrays.equals(proposedDigest, bDigest) && userExist;
		} catch (IOException ex) {
			throw new SQLException(
					"Database inconsistant Salt or Digested Password altered");
		}
	}

	/**
	 * Inserts a new user in the database
	 * 
	 * @param con
	 *            Connection An open connection to a databse
	 * @param login
	 *            String The login of the user
	 * @param password
	 *            String The password of the user
	 * @return boolean Returns true if the login and password are ok (not null
	 *         and length(login)<=100
	 * @throws SQLException
	 *             If the database is unavailable
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm SHA-1 or the SecureRandom is not supported
	 *             by the JVM
	 */
	public boolean createUser(String login, String password)
			throws SQLException, NoSuchAlgorithmException,
			UnsupportedEncodingException {

		if (login != null && password != null && login.length() <= 100) {
			// Uses a secure Random not a simple Random
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// Salt generation 64 bits long
			byte[] bSalt = new byte[8];
			random.nextBytes(bSalt);
			// Digest computation
			byte[] bDigest = getHash(ITERATION_NUMBER, password, bSalt);
			String sDigest = byteToBase64(bDigest);
			String sSalt = byteToBase64(bSalt);
			UserBO userBO = new UserBO();
			userBO.setLogin(login);
			userBO.setPassword(sDigest);
			userBO.setSalt(sSalt);

			authenticationDAO.insert(userBO);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 */
	public byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// MessageDigest digest = MessageDigest.getInstance("SHA-1");

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));

		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}

	public Connection getConnection() {

		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost/RFO?"
							+ "user=root&password=");
		} catch (Exception e) {

		}

		return connection;
	}

	public void close() {

		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {

		}

	}

	/*public static void main(String[] args) {

		AuthenticateUser authenticateUser = new AuthenticateUser();

		try {
			Connection connection = authenticateUser.getConnection();

			if (connection == null) {
				System.out
						.println("AuthenticateUser::main::Connection is NULL");
			} else {

				// System.out.println("AuthenticateUser::main::Inserting test-user1");
				// authenticateUser.createUser(connection, "test-user1",
				// "password123");
				//
				// System.out.println("AuthenticateUser::main::Inserting test-user2");
				// authenticateUser.createUser(connection, "test-user2",
				// "password1234");
				//
				// System.out.println("AuthenticateUser::main::Inserting test-user3");
				// authenticateUser.createUser(connection, "test-user3",
				// "password12345");

				System.out
						.println("AuthenticateUser::main::Authenticating test-user4");
				boolean isUserValid = authenticateUser.authenticate(connection,
						"test-user4", "password123");

				System.out
						.println("AuthenticateUser::main::Authenticating test-user4, isUserValid = "
								+ isUserValid);

			}
		} catch (Exception e) {
			System.out.println("AuthenticateUser::main::Exception e = " + e);
		} finally {
			authenticateUser.close();
		}

		// authenticateUser.createUser(con, login, password);

	}*/

	public AuthenticationDAO getAuthenticationDAO() {
		return authenticationDAO;
	}

	public void setAuthenticationDAO(AuthenticationDAO authenticationDAO) {
		this.authenticationDAO = authenticationDAO;
	}

}
