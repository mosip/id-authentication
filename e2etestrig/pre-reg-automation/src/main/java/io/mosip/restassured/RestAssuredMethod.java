package io.mosip.restassured;
import java.io.File;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;
import io.restassured.response.Response;

public class RestAssuredMethod {
	public RestAssuredLibrary restLib=new RestAssuredLibrary();
	
	public Response postRequestWithToken(Object body, String Resource_URI,String authToken) {
		return restLib.postRequestWithToken(Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,authToken);
	}
	public Response putFileAndJsonWithParm(String Resource_Uri, Object body, File file,HashMap<String, String> parm,String token) {
		return restLib.Post_JSONwithFileWithParm(body, file,   Resource_Uri, MediaType.MULTIPART_FORM_DATA,parm,token);
	}
	
	public Response postRequestWithOutToken(Object body, String Resource_URI) {
		return restLib.postRequestWithoutToken(Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}
	public Response getRequestWithoutParm(String Resource_URI,String authToken) {
		return restLib.getRequestWithoutParm(Resource_URI,authToken);
	}
	public Response postRequestWithParm(Object body, String Resource_URI, HashMap<String, String> pathValue,String authToken) {
		return restLib.postRequestWithParm(Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, pathValue,authToken);
	}
	public Response getRequestDataSync(String Resource_URI, HashMap<String, String> valueMap,String authToken) {
		return restLib.get_Request_queryParamDataSync(Resource_URI, valueMap,authToken);
	}
	
}
