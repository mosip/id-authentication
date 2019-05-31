
package io.mosip.service;

import java.io.File;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;


import io.mosip.util.RestAssuredMethods;
import io.restassured.response.Response;

public class ApplicationLibrary extends BaseTestCase{
	
	private static RestAssuredMethods restMethods = new RestAssuredMethods();
	
	

		
	public Response postRequest(Object body, String Resource_URI) {
		return restMethods.post_Request(ApplnURI + Resource_URI, body,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
	}
	
    
    public Response getRequest(String Resource_URI, HashMap<String, String> valueMap) {
          return restMethods.get_Request_queryParam(ApplnURI + Resource_URI , valueMap);
    }
    
    public Response putRequest(Object body, String Resource_URI) {
		return restMethods.put_Request(ApplnURI + Resource_URI, body,
				MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
	}
  
    public Response getRequestParam2(String Resource_URI,String id,String keyId, String timestamp, String Keytimestamp) {
        return restMethods.get_request_pathParam(ApplnURI + Resource_URI, id,keyId,  timestamp,  Keytimestamp);
    }
    public Response putRequest(String Resource_URI, HashMap<String, String> valueMap) {
    	return restMethods.put_Request(ApplnURI+Resource_URI,MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,valueMap);
    }
    
    public Response deleteRequest(String Resource_URI,HashMap<String, String> valueMap) {
    	return restMethods.delete_Request(ApplnURI+Resource_URI,valueMap);
    }
    //public Response PutRequest(String Resource_URI, String )
    public Response putMultipartFile(File file, String Url) {
    	return restMethods.Post_DataPacket(file,ApplnURI+Url);
    }
    public Response putFileAndJson(String Resource_Uri,Object body,File file) {
    	return restMethods.Post_JSONwithFile(body, file, ApplnURI+Resource_Uri,MediaType.MULTIPART_FORM_DATA);
    }
    
    public Response getRequestPathPara(String Resource_URI, HashMap<String, String> valueMap) {
        return restMethods.get_Request_pathParameters(ApplnURI + Resource_URI , valueMap);
      
  } 
    /** Author Arjun
     * @param Resource_URI
     * @param valueMap
     * @return
     */
    public Response getRequestAsQueryParam(String Resource_URI, HashMap<String, String> valueMap) {
      return restMethods.get_Request_queryParam(ApplnURI + Resource_URI , valueMap);
      
}

    public Response GetRequestNoParameter(String Resource_URI) {
        return restMethods.GET_REQUEST_withoutParameters(ApplnURI + Resource_URI);
        
  }
    
    public Response deleteRequestPathParam(String Resource_URI,HashMap<String, String> valueMap) {
    	return restMethods.delete_RequestPathParameters(ApplnURI+Resource_URI,valueMap);

    }
    
    
    public Response postModifiedGETRequest(String Resource_URI, HashMap<String, String> valueMap) {
		return restMethods.post_Request_WithQueryParams(ApplnURI + Resource_URI, new JSONObject(),MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,valueMap);
	}
    
    
    public Response putRequest_WithBody(String Resource_URI, JSONObject object) {
    	return restMethods.put_RequestWithBody(ApplnURI+Resource_URI,MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,object);
    }
    
}

