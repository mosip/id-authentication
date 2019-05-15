
package io.mosip.kernel.service;

import java.io.File;
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
	
	public Response dataSyncPostRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.dataSyncPostRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}
	public Response authPostRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.authPostRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
	}
	
	public Response getRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestQueryParam(ApplnURI + Resource_URI, valueMap,cookie);
	}
	public Response getRequestDataSync(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestQueryParamDataSync(ApplnURI + Resource_URI, valueMap, cookie);
	}

	public Response putRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.putRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}

	public Response getRequestParam2(String Resource_URI, String id, String keyId, String timestamp,
			String Keytimestamp,String cookie) {
		return commonLibrary.getRequestPathParam(ApplnURI + Resource_URI, id, keyId, timestamp, Keytimestamp,cookie);
	}

	public Response putRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.putRequest(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, valueMap,cookie);
	}

	public Response deleteRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.deleteRequest(ApplnURI + Resource_URI, valueMap,cookie);
	}

	// public Response PutRequest(String Resource_URI, String )
	public Response putMultipartFile(File file, String Url,String cookie) {
		return commonLibrary.postDataPacket(file, ApplnURI + Url, cookie);
	}

	public Response putFileAndJson(String Resource_Uri, Object body, File file,String cookie) {
		return commonLibrary.postJSONwithFile(body, file, ApplnURI + Resource_Uri, MediaType.MULTIPART_FORM_DATA,cookie);
	}

	public Response getRequestPathPara(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.getRequestPathParameters(ApplnURI + Resource_URI, valueMap, cookie);

	}

	public Response getRequestWithoutBody(String Resource_URI,String cookie) {
		return commonLibrary.getRequestWithoutBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, cookie);
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

	/**
	 * @author Arjun patch request for id repo
	 * @param body
	 * @param Resource_URI
	 * @return
	 */
	public Response patchRequest(Object body, String Resource_URI,String cookie) {
		return commonLibrary.patchRequest(ApplnURI + Resource_URI, body, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}

	public Response getRequestNoParameter(String Resource_URI,String cookie) {
		return commonLibrary.getRequestWithoutParameters(ApplnURI + Resource_URI, cookie);

	}

	public Response deleteRequestPathParam(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.deleteRequestPathParameters(ApplnURI + Resource_URI, valueMap, cookie);

	}

	public Response postModifiedGETRequest(String Resource_URI, HashMap<String, String> valueMap,String cookie) {
		return commonLibrary.postRequestWithQueryParams(ApplnURI + Resource_URI, new JSONObject(),
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, valueMap,cookie);
	}

	public Response putRequestWithBody(String Resource_URI, JSONObject object,String cookie) {
		return commonLibrary.putRequestWithBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON, object, cookie);
	}

	public Response postRequestFormData(JSONObject jsonString, String serviceUri,String cookie) {
		return commonLibrary.post_RequestWithBodyAsMultipartFormData(jsonString, ApplnURI + serviceUri, cookie);
	}

	public Response putRequestWithoutBody(String Resource_URI,String cookie) {
		return commonLibrary.putRequestWithoutBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,cookie);
	}
	public Response adminputRequest_WithoutBody(String Resource_URI) {
		return commonLibrary.adminPutRequestWithoutBody(ApplnURI + Resource_URI, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
	}
	 public Response getRequestPathQueryPara(String Resource_URI, HashMap<String, String> path_value,HashMap<String, List<String>> query_value,String cookie) {
	        return commonLibrary.getRequestPathQueryParam(ApplnURI + Resource_URI , path_value,query_value, cookie);
	    } 
	 public Response getRequestPathQueryParaString(String Resource_URI, HashMap<String, String> path_value,HashMap<String, String> query_value,String cookie) {
	        return commonLibrary.getRequestPathQueryParamString(ApplnURI + Resource_URI , path_value,query_value,cookie);
	    } 
	  //Notify
	    public Response putFileAndJsonParam(String Resource_Uri,Object body,File file,String langCodeKey,String value,String cookie) {
	    	
	    	return commonLibrary.postJSONwithFileParam(body, file, ApplnURI+Resource_Uri, MediaType.MULTIPART_FORM_DATA,langCodeKey,value,cookie);
	        
	    }


}
