package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import io.restassured.response.Response;

public class RestAssuredMethods {
	private static Logger logger = Logger.getLogger(RestAssuredMethods.class);
	public Response post_Request(String url, Object body, String contentHeader, String acceptHeader) {

		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	} // end POST_REQUEST
	
	public Response put_Request(String url, Object body, String contentHeader, String acceptHeader) {

		Response putResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().put(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	} 
    /**
    * REST ASSURED GET request method
    *
    * @param url
    *            destination of the request
    * @return Response object that has the REST response
    */
    public Response get_Request_queryParam(String url,HashMap<String, String> valueMap) {
          logger.info("REST-ASSURED: Sending a GET request to " + url);
          Response getResponse = given().relaxedHTTPSValidation().queryParams(valueMap)
                      .log().all().when().get(url).then().log().all().extract().response();
          // log then response
          logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
          logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
          return getResponse;
    } // end GET_REQUEST
    
    

    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response get_Request_pathParameters(String url,HashMap<String, String> valueMap) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Response getResponse = given().relaxedHTTPSValidation().pathParams(valueMap)
                       .log().all().when().get(url).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } 
     public Response GET_REQUEST_withoutParameters(String url) {
         logger.info("REST-ASSURED: Sending a GET request to " + url);
         Response getResponse = given().relaxedHTTPSValidation()
                     .log().all().when().get(url).then().log().all().extract().response();
     
         logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
         logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
         return getResponse;
   }
    
    public Response put_Request(String url,String contentHeader,String acceptHeader,HashMap<String, String> valueMap) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Response getResponse= given().relaxedHTTPSValidation().queryParams(valueMap).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
    public Response delete_Request(String url,HashMap<String, String> valueMap) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Response getResponse=given().relaxedHTTPSValidation().queryParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response delete_RequestPathParameters(String url,HashMap<String, String> valueMap) {
    	logger.info("REST-ASSURED: Sending a DELETE request to   "+ url);
    	Response getResponse=given().relaxedHTTPSValidation().pathParams(valueMap).log().all().when().delete(url).then().log().all().extract().response();
    	logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response Post_DataPacket(File file,String url) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	
    	Response getResponse=given().relaxedHTTPSValidation().multiPart("file",file).expect().when().post(url);
    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    public Response Post_JSONwithFile(Object body,File file,String url,String contentHeader) {
    	logger.info("REST:ASSURED:Sending a data packet to"+url);
    	logger.info("Request DTO for document upload is"+ body);
    	logger.info("Name of the file is"+file.getName());
    	/*
    	 * Fetch to get the param name to be passed in the request
    	 */
    	String Document_request=fetch_IDRepo("req.Documentrequest");
    
    	Response getResponse=given().relaxedHTTPSValidation().multiPart("file",file).formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
    	logger.info("REST:ASSURED: The response from request is:"+getResponse.asString());
    	logger.info("REST-ASSURED: the response time is: "+ getResponse.time());
    	return getResponse;
    }
    
    /**
     * REST ASSURED GET request method
     *
     * @param url
     *            destination of the request
     * @return Response object that has the REST response
     */
     public Response get_Request_queryParam(String url, String contentHeader, String acceptHeader, String urls) {
           logger.info("REST-ASSURED: Sending a GET request to " + url);
           Response getResponse = given().relaxedHTTPSValidation()
                       .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
           // log then response
           logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
           logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
           return getResponse;
     } // end GET_REQUEST
    
    public static void backUpFiles(String source, String destination) {
    	//String time = java.time.LocalDate.now().toString()+"--"+java.time.LocalTime.now().toString();
    	 Calendar cal = Calendar.getInstance();
         cal.setTime(Date.from(Instant.now()));
  
    	String result = String.format(
                  "%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
    //System.out.println(System.getProperty("APPDATA"));
		String filePath="src/test/resources/APPDATA/MosipUtil/UtilFiles/"+destination+"/"+result;
		File sourceFolder = new File(source);
		File dest = new File(filePath);
		try {
		FileUtils.copyDirectory(sourceFolder,dest);
		logger.info("Please Check Your %APPDATA% in C drive to get access to the generted files");
		}catch(IOException e) {
			logger.info("Check %APPDATA%");
		}
    }
    
    public Response get_request_pathParam(String url,String id,String keyId, java.lang.String timestamp, java.lang.String keytimestamp) {
        logger.info("REST-ASSURED: Sending a GET request to " + url);
        HashMap params= new HashMap();
        params.put(keyId, id);
        params.put(keytimestamp, timestamp);
        Response getResponse = given().relaxedHTTPSValidation().pathParams(params)
                    .log().all().when().get(url).then().log().all().extract().response();
        // log then response
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  } // end GET_REQUEST

    public Response put_RequestWithBody(String url,String contentHeader,String acceptHeader,JSONObject valueMap) {
    	  logger.info("REST-ASSURED: Sending a PUT request to " + url);
    	  Response getResponse= given().relaxedHTTPSValidation().contentType(MediaType.APPLICATION_JSON).body(valueMap.toJSONString()).log().all().when().put(url).then().log().all().extract().response();
    	  logger.info("REST-ASSURED: The response from the request is: "+getResponse.asString());
    	  logger.info("REST-ASSURED: the response Time is: "+  getResponse.time());
    	  return getResponse;
   }
    
   
      
    public Response post_Request_WithQueryParams(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> valueMap) {

  		Response postResponse = given().relaxedHTTPSValidation().body(body).queryParams(valueMap).contentType(contentHeader)
  				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
  		// log then response
  		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
  		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
  		return postResponse;
  	} // end POST_REQUEST

    
    
 // GLOBAL CLASS VARIABLES
 	private Properties prop;
 	
 	
 	private String fetch_IDRepo(String element)
 	{
 		String IDRepo_Element = null;
 		try {
 			logger.info("Fetching ID Repo related properties to validate in the response");
 			prop = new Properties();
 			InputStream inputStream = new FileInputStream(

 					"src/config/IDRepo.properties");

 			prop.load(inputStream);
 			switch(element)
 			{
 			case "req.Documentrequest":
 				
 				IDRepo_Element=prop.getProperty("req.Documentrequest"); 	
 			
 			logger.info("Configs from properties file is fetched for req.Documentreques.  " +IDRepo_Element);
 			}

 		} catch (IOException e) {
 			logger.error("Could not find the properties file.\n" + e);
 		}
 		
 		return IDRepo_Element;
 	
 	}
}
