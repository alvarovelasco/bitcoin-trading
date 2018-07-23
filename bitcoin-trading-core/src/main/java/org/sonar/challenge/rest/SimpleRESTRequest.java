package org.sonar.challenge.rest;

import org.sonar.challenge.exception.RESTResponseNotSuccessException;

public interface SimpleRESTRequest {

	String request() throws RESTResponseNotSuccessException;
	
}
