package io.mosip.preregistration.service;

import java.io.File;

import javax.ws.rs.core.MediaType;

import io.mosip.preregistration.util.PreRegistrationCommonLibrary;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class PreRegistrationApplicationLibrary extends BaseTestCase{
	
	PreRegistrationCommonLibrary commonLibrary=new PreRegistrationCommonLibrary();
	
	public Response postRequest(Object body, String Resource_URI) {
		return commonLibrary.postRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}

	
	
	public Response getRequestWithoutParm(String Resource_URI) {
		return commonLibrary.getRequestWithoutParm(ApplnURI + Resource_URI);
	}
	
	
	 public Response postFileAndJsonParam(String Resource_Uri,Object body,File file,String langCodeKey,String value) {
	    	
	    	return commonLibrary.PostJSONwithFileParam(body, file, ApplnURI+Resource_Uri, MediaType.MULTIPART_FORM_DATA,langCodeKey,value);
	        
	    }
	
	/*public Response postRequestWithoutBody(String Resource_URI) {
		return commonLibrary.post_RequestWithoutBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}*/
}
