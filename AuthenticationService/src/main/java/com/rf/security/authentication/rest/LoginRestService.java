package com.rf.security.authentication.rest;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.rf.security.authentication.authenticate.AuthenticateUser;
import com.rf.security.authentication.domain.UserLoginBO;

@Path("/login")
public class LoginRestService {

	private AuthenticateUser authenticateUser;

	// This method is called if TEXT_PLAIN is request
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserLoginBO isUserValid(UserLoginBO userObj)
			throws NoSuchAlgorithmException, SQLException {

		System.out.println("LoginRestService::isUserValid::username = "
				+ userObj.getUserID());
		System.out.println("LoginRestService::isUserValid::username = "
				+ userObj.getPassword());

		boolean valid = authenticateUser.authenticate(userObj.getUserID(),
				userObj.getPassword());

		userObj.setValid(valid);
		return userObj;
	}

	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public UserLoginBO createUser(UserLoginBO userObj) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{

		System.out.println("LoginRestService::isUserValid::username = "
				+ userObj.getUserID());
		System.out.println("LoginRestService::isUserValid::username = "
				+ userObj.getPassword());

		boolean valid = authenticateUser.createUser(userObj.getUserID(), userObj.getPassword());

		userObj.setValid(valid);
		return userObj;
	}

	public AuthenticateUser getAuthenticateUser() {
		return authenticateUser;
	}

	public void setAuthenticateUser(AuthenticateUser authenticateUser) {
		this.authenticateUser = authenticateUser;
	}

}
