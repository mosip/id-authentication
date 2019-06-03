package io.mosip.kernel.service;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import io.mosip.service.BaseTestCase;
import io.mosip.kernel.util.CommonLibrary;
import io.restassured.response.Response;

public class ApplicationLibrary extends BaseTestCase {

	private static CommonLibrary commonLibrary = new CommonLibrary();

	public Response postRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.postRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}
	
	public Response postRequest(Object body, String Resource_URI) {
		return commonLibrary.postRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}
	
	public Response getRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestQueryParam(ApplnURI + Resource_URI, valueMap,cookie);
	}

	public Response putRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.putRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}

	public Response putRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.putRequest(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, valueMap,cookie);
	}

	public Response getRequestPathPara(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestPathParameters(ApplnURI + Resource_URI, valueMap, cookie);

	}
	/**
	 * Author Arjun
	 * 
	 * @param Resource_URI
	 * @param valueMap
	 * @return
	 */
	public Response getRequestAsQueryParam(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestQueryParam(ApplnURI + Resource_URI, valueMap, cookie);

	}

	public Response getRequestNoParameter(String Resource_URI,String cookie) {
		return commonLibrary.getRequestWithoutParameters(ApplnURI + Resource_URI, cookie);

	}
	
	public Response getConfigProperties(String Resource_URI) {
		return commonLibrary.getConfigProperties(Resource_URI);

	}
	public Response putRequestWithBody(String Resource_URI, JSONObject object,String cookie) {
		return commonLibrary.putRequestWithBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, object, cookie);
	}

	public Response postRequestFormData(JSONObject jsonString, String serviceUri,String cookie) {
		return commonLibrary.postRequestWithBodyAsMultipartFormData(jsonString, ApplnURI + serviceUri, cookie);
	}

	public Response putRequestWithoutBody(String Resource_URI,String cookie) {
		return commonLibrary.putRequestWithoutBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}
	
	 public Response getRequestPathQueryPara(String Resource_URI, HashMap<String, String> path_value,HashMap<String, List<String>> query_value,String cookie) {
	        return commonLibrary.getRequestPathQueryParam(ApplnURI + Resource_URI , path_value,query_value, cookie);
	    } 
	 public Response getRequestPathQueryParaString(String Resource_URI, HashMap<String, String> path_value,HashMap<String, String> query_value,String cookie) {
	        return commonLibrary.getRequestPathQueryParamString(ApplnURI + Resource_URI , path_value,query_value,cookie);
	    } 
	  


}