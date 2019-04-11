package io.mosip.preregistration.tests;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.restassured.response.Response;

public class ApplicationLibary_Sample extends BaseTestCase {

	private static CommonLibrary commonLibrary = new CommonLibrary();

	public Response getRequestParm(String Resource_URI, HashMap<String, String> valueMap) {
		return commonLibrary.get_Request_pathParameters(ApplnURI + Resource_URI, valueMap);
	}


}
