package io.mosip.authentication.fw.client;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;

/**
 * The Rest assured class to put, post, get request and response
 * @author Vignesh
 *
 */
public class RestClient {

	private static Logger logger = Logger.getLogger(RestClient.class);	
	
	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {
		logger.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	/**
	 * REST ASSURED GET request method
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param urls
	 * @return response
	 */
    public Response getRequest(String url, String contentHeader, String acceptHeader, String urls) {
          logger.info("REST-ASSURED: Sending a GET request to " + url);
          Response getResponse= given().relaxedHTTPSValidation()
                      .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
          logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
          logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
          return getResponse;
    }

    /**
	 * REST ASSURED GET request method without type or after ?
	 * 
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param urls
	 * @return response
	 */
    public Response getRequest(String url, String contentHeader, String acceptHeader) {
        logger.info("RESSURED: Sending a GET request to " + url);
        Response getResponse= given().relaxedHTTPSValidation()
                    .log().all().when().get(url).then().log().all().extract().response();
        logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
  }
}

