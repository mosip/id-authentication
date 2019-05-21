package io.mosip.preregistration.util;

import static io.restassured.RestAssured.given;

import java.io.File;

import io.mosip.service.BaseTestCase;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class PreRegistrationCommonLibrary extends BaseTestCase
{

	PreRegistrationUtil preregUtil=new PreRegistrationUtil();
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();

		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	

	public Response postJSONwithFileParam(Object body, File file, String url, String contentHeader, String langCodeKey,
			String value) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("Request DTO for document upload is" + body);
		logger.info("Name of the file is" + file.getName());
		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request
		 */

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		String Notification_request = preregUtil.fetchPreregProp().get("req.notify");
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("attachment", file)
				.formParam(Notification_request, body).formParam(langCodeKey, value).contentType(contentHeader).expect()
				.when().post(url);

		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	
	/*public Response post_RequestWithoutBody(String url, String contentHeader, String acceptHeader) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}*/
	public Response getRequestWithoutParm(String url) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	
	
	public Response PostJSONwithFileParam(Object body, File file, String url, String contentHeader, String langCodeKey,
			String value) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("Request DTO for document upload is" + body);
		logger.info("Name of the file is" + file.getName());
		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request
		 */

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		String Notification_request = preregUtil.fetchPreregProp().get("req.notify");
		logger.info("REST:ASSURED: The responsNotification_request:" + Notification_request);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("attachment", file)
				.formParam(Notification_request, body).formParam(langCodeKey, value).contentType(contentHeader).expect()
				.when().post(url);

		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}
	
}
