package io.mosip.kernel.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import io.mosip.service.BaseTestCase;
import io.mosip.kernel.util.CommonLibrary;
import io.restassured.response.Response;
/**
 * 
 * @author Arunakumar.Rati
 * @author Ravi Kant
 *
 */
public class ApplicationLibrary extends BaseTestCase {

	private static CommonLibrary commonLibrary = new CommonLibrary();

	// post requests
	public Response postWithoutJson(String endpoint, String cookie) {
		return commonLibrary.postWithoutJson(ApplnURI + endpoint, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response postWithJson(String endpoint, Object body) {
		return commonLibrary.postWithJson(ApplnURI + endpoint, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}

	public Response postWithJson(String endpoint, Object body, String cookie) {
		return commonLibrary.postWithJson(ApplnURI + endpoint, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}

	public Response postWithPathParams(String endpoint, Object body, HashMap<String, String> pathParams,
			String cookie) {
		return commonLibrary.postWithPathParams(ApplnURI + endpoint, body, pathParams, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}

	public Response postWithOnlyFile(String endpoint, File file, String fileKeyName, String cookie) {
		return commonLibrary.postWithOnlyFile(ApplnURI + endpoint,  file, fileKeyName, cookie);
	}
	public Response postWithFile(String endpoint, Object body, File file, String fileKeyName, String cookie) {
		return commonLibrary.postWithFile(ApplnURI + endpoint, body, file, fileKeyName, MediaType.APPLICATION_JSON, cookie);
	}

	public Response postWithFileFormParams(String endpoint, HashMap<String, String> formParams, File file, String fileKeyName,
			String cookie) {
		return commonLibrary.postWithFileFormParams(ApplnURI + endpoint, formParams, file, fileKeyName,
				MediaType.MULTIPART_FORM_DATA, cookie);
	}

	public Response postWithFilePathParamsFormParams(String endpoint, HashMap<String, String> pathParams,
			HashMap<String, String> formParams, File file, String fileKeyName, String cookie) {
		return commonLibrary.postWithFilePathParamsFormParams(ApplnURI + endpoint, pathParams, formParams, file, fileKeyName,
				MediaType.MULTIPART_FORM_DATA, cookie);
	}

	public Response postWithQueryParams(String endpoint, HashMap<String, String> queryparams, Object body,
			String cookie) {
		return commonLibrary.postWithQueryParams(ApplnURI + endpoint, queryparams, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response postWithMultiHeaders(String endpoint, Object body, HashMap<String, String> headers,
			String contentHeader, String cookie) {
		return commonLibrary.postWithMultiHeaders(ApplnURI + endpoint, body, headers, MediaType.APPLICATION_JSON, cookie);
	}
	public Response postRequestEmailNotification(String endpoint, JSONObject jsonString, String cookie) {
		return commonLibrary.postRequestEmailNotification(ApplnURI+endpoint, jsonString, cookie);
	}
	// patch request
	public Response patchRequest(String endpoint, Object body,String cookie) {
		return commonLibrary.patchRequest(ApplnURI + endpoint, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	// get requests
	public Response getWithoutParams(String endpoint, String cookie) {
		return commonLibrary.getWithoutParams(ApplnURI + endpoint, cookie);
	}
	public Response getWithPathParam(String endpoint, HashMap<String, String> patharams, String cookie) {
		return commonLibrary.getWithPathParam(ApplnURI + endpoint, patharams, cookie);
	}
	public Response getWithQueryParam(String endpoint, HashMap<String, String> queryParams, String cookie) {
		return commonLibrary.getWithQueryParam(ApplnURI + endpoint, queryParams, cookie);
	}
	public Response getWithQueryParamList(String endpoint, HashMap<String, List<String>> queryParams, String cookie) {
		return commonLibrary.getWithQueryParamList(ApplnURI + endpoint, queryParams, cookie);
	}
	public Response getWithPathQueryParam(String endpoint, HashMap<String, String> pathParams,
			HashMap<String, String> queryParams, String cookie) {
		return commonLibrary.getWithPathQueryParam(ApplnURI + endpoint,pathParams, queryParams, cookie);
	}
	public Response getWithPathParamQueryParamList(String endpoint, HashMap<String, String> pathParams,
			HashMap<String, List<String>> queryParams, String cookie) {
		return commonLibrary.getWithPathParamQueryParamList(ApplnURI + endpoint, pathParams, queryParams, cookie);
	}
	// put requests
	public Response putWithoutData(String endpoint, String cookie) {
		return commonLibrary.putWithoutData(ApplnURI + endpoint, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response putWithJson(String endpoint, Object body, String cookie) {
		return commonLibrary.putWithJson(ApplnURI + endpoint, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response putWithPathParams(String endpoint, HashMap<String, String> pathParams, String cookie) {
		return commonLibrary.putWithPathParams(ApplnURI + endpoint, pathParams, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response putWithQueryParams(String endpoint, HashMap<String, String> queryParams, String cookie) {
		return commonLibrary.putWithQueryParams(ApplnURI + endpoint, queryParams, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	public Response putWithPathParamsBody(String endpoint, HashMap<String, String> pathParams, Object body, String cookie) {
		return commonLibrary.putWithPathParamsBody(ApplnURI + endpoint, pathParams, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	// delete request
	public Response deleteWithPathParams(String endpoint, HashMap<String, String> pathParams, String cookie) {
		return commonLibrary.deleteWithPathParams(ApplnURI + endpoint, pathParams, cookie);
	}
	public Response deleteWithQueryParams(String endpoint, HashMap<String, String> queryParams, String cookie) {
		return commonLibrary.deleteWithQueryParams(ApplnURI + endpoint, queryParams, cookie);
	}
	public Response deleteWithPathQueryParams(String endpoint, HashMap<String, String> pathParams,
			HashMap<String, String> queryParams, String cookie) {
		return commonLibrary.deleteWithPathQueryParams(ApplnURI + endpoint, pathParams, queryParams, cookie);
	}
	// get congig properties
	public Response getConfigProperties(String Resource_URI) {
		return commonLibrary.getConfigProperties(Resource_URI);
	}
	
	public Response deleteWithoutParams(String endpoint, String cookie) {
		return commonLibrary.deleteWithoutParams(ApplnURI + endpoint, cookie);
	} 
	
	public Response putFileAndJson(String Resource_Uri,Object body,File file,String cookie) {
		return commonLibrary.Post_JSONwithFile(body, file, ApplnURI+Resource_Uri,MediaType.MULTIPART_FORM_DATA,cookie);
	} 
	
		
}
