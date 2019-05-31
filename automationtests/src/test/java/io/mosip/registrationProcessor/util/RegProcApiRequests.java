package io.mosip.registrationProcessor.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import io.mosip.service.BaseTestCase;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class RegProcApiRequests extends BaseTestCase {
	private static Logger logger = Logger.getLogger(RegProcApiRequests.class);

	public Response regProcSyncRequest(String url, Object body, String center_machine_refId, String ldt,
			String contentHeader, String regProcAuthToken) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response postResponse = given().cookie(builder.build()).header("Center-Machine-RefId", center_machine_refId)
				.header("timestamp", ldt).relaxedHTTPSValidation().body("\"" + body + "\"").contentType(contentHeader)
				.log().all().when().post(ApplnURI+url).then().log().all().extract().response();
		return postResponse;
	}

	public Response regProcPacketUpload(File file, String url, String regProcAuthToken) {

		logger.info("REST:ASSURED:Sending a data packet to" + ApplnURI+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file).expect().log().all().
				when().post(ApplnURI+url);
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;

	}

	public Response regProcGetRequest(String url, HashMap<String, String> valueMap, String regProcAuthToken) {
		logger.info("REST-ASSURED: Sending a GET request to " + ApplnURI+url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log()
				.all().when().get(ApplnURI+url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader,String token) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("REST ASSURRED :: Request To Encrypt Is "+ body);
		Cookie.Builder builder = new Cookie.Builder("Authorization",token);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();

		return postResponse;
	}

}
