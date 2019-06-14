package io.mosip.authentication.fw.util;

import static io.restassured.RestAssured.given;  

import java.io.File;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import io.restassured.response.Response;

/**
 * The Rest assured class to put, post, get request and response
 * 
 * @author Vignesh
 *
 */
public class RestClient {

	private static final Logger RESTCLIENT_LOGGER = Logger.getLogger(RestClient.class);	
	
	/**
	 * REST ASSURED POST request method
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
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
    public static Response getRequest(String url, String contentHeader, String acceptHeader, String urls) {
          RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
          Response getResponse= given().relaxedHTTPSValidation()
                      .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
          RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
          RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + getResponse.time());
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
	public static Response getRequest(String url, String contentHeader, String acceptHeader) {
		RESTCLIENT_LOGGER.info("RESSURED: Sending a GET request to " + url);
		Response getResponse = given().relaxedHTTPSValidation().log().all().when().get(url).then().log().all().extract()
				.response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
    /**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param File
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, File file, String contentHeader, String acceptHeader) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().multiPart(file).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	/**
	 * REST ASSURED POST request method
	 * 
	 * @param url
	 * @param string
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response postRequest(String url, String content, String contentHeader, MediaType acceptHeader) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(content).contentType(contentHeader)
				.accept(acceptHeader.toString()).log().all().when().post(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	/**
	 * REST ASSURED PATCH request method
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return response
	 */
	public static Response patchRequest(String url, String body, String contentHeader, String acceptHeader) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().patch(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}	
	
	public static String getCookie(String url, Object body, String contentHeader, String acceptHeader,String cookieName) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse.getCookie(cookieName);
	}
	
	public static Response postRequestWithCookie(String url, Object body, String contentHeader, String acceptHeader,String cookieName,String cookieValue) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a POST request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader).cookie(cookieName, cookieValue)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	public static Response getRequestWithCookie(String url, String contentHeader, String acceptHeader, String urls,String cookieName,String cookieValue) {
        RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
        Response getResponse= given().relaxedHTTPSValidation().cookie(cookieName, cookieValue)
                    .log().all().when().get(url+"?"+urls).then().log().all().extract().response();
        RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
    }
	public static Response patchRequestWithCookie(String url, String body, String contentHeader, String acceptHeader,String cookieName,String cookieValue) {
		RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a PATCH request to " + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader).cookie(cookieName, cookieValue)
				.accept(acceptHeader).log().all().when().patch(url).then().log().all().extract().response();
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	public static Response getRequestWithCookie(String url, String contentHeader, String acceptHeader,String cookieName,String cookieValue) {
        RESTCLIENT_LOGGER.info("REST-ASSURED: Sending a GET request to " + url);
        Response getResponse= given().relaxedHTTPSValidation().cookie(cookieName, cookieValue)
                    .log().all().when().get(url).then().log().all().extract().response();
        RESTCLIENT_LOGGER.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
        RESTCLIENT_LOGGER.info("REST-ASSURED: The response Time is: " + getResponse.time());
        return getResponse;
    }
}

