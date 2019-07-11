package io.mosip.registrationProcessor.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import io.mosip.service.BaseTestCase;
import io.mosip.testrunner.MosipTestRunner;
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
				.all().when().post(ApplnURI+url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	public Response regProcGetIdRepo(String url, String regProcAuthToken) {
		logger.info("REST-ASSURED: Sending a GET request to " + ApplnURI+url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log()
				.all().when().get(ApplnURI+url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader,String token) {
		logger.info("REST:ASSURED:Sending a data packet to" + ApplnURI+url);
		logger.info("REST ASSURRED :: Request To Encrypt Is "+ body);
		Cookie.Builder builder = new Cookie.Builder("Authorization",token);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(ApplnURI+url).then().log().all().extract().response();

		return postResponse;
	}
	
	public Response regProcPostRequest(String url, HashMap<String, String> valueMap, String contentHeader,String token) {
		logger.info("REST:ASSURED:Sending a post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(valueMap)
				.contentType(contentHeader).log().all().when().post(ApplnURI+url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {

		logger.info("URL IS  :: "+ ApplnURI+url);

		Response postResponse = given().relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(ApplnURI+url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		logger.info("REST-ASSURED:454545445 The response Time is: " + postResponse.asString());
		return postResponse;
	}
	
	public boolean validateToken(String token) {
		String url="/v1/authmanager/authorize/validateToken";
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);
		Response response=given().cookie(builder.build()).relaxedHTTPSValidation()
				.log().all().when().post(ApplnURI+url).then().log().all().extract().response();
		System.out.println(response.asString());
		List<String> errors=response.jsonPath().get("errors");
		if(errors==null) {
			return true;
		} else
			return false;
	} 
	
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 *//*
	public String getResourcePath() {
		return MosipTestRunner.getGlobalResourcePath()+"/";
	} */
	public Response regProcPacketGenerator(Object body,String url,String contentHeader,String token ) {
		logger.info("REST:ASSURED:Sending a post request to"+url);
		Cookie.Builder builder = new Cookie.Builder("Authorization",token);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.log().all().when().post(ApplnURI+url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	public boolean getUinStatusFromIDRepo(JSONObject actualRequest,String idRepoToken,String expectedUinResponse) {
		boolean status=false;
		JSONObject generatorRequest = (JSONObject) actualRequest.get("request");
			String uin=generatorRequest.get("uin").toString();
			String idRepoUrl="/idrepository/v1/identity/uin/";
			Cookie.Builder builder = new Cookie.Builder("Authorization",idRepoToken);
			
	Response idRepoResponse = given().cookie(builder.build()).relaxedHTTPSValidation().when().get(ApplnURI+idRepoUrl+uin).then().extract().response();
	String uinResponse=idRepoResponse.jsonPath().get("response.status").toString();
	System.out.println(uinResponse);
	if(uinResponse.equals(expectedUinResponse)) {
		status=true;
	}
		return status;
		
	}
	
	/**
	 * The method to return class loader resource path
	 * 
	 * @return String
	 */
	public String getResourcePath() {
		return MosipTestRunner.getGlobalResourcePath()+"/";
	}
	
	/**
	 * @param url
	 * @param cookie
	 * @return this method is for get request with authentication(cookie) and
	 *         without any param.
	 */
	public Response getWithoutParams(String url, String cookie) {
		logger.info("REST-ASSURED: Sending a Get request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url);
		// log then response
		//responseLogger(getResponse);
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		logger.info("REST-ASSURED: the response from request is: " + getResponse.asString());
		return getResponse;
	}

}
