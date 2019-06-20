package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;

import io.mosip.service.BaseTest;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class RegProcApiRequests extends BaseTest {
	

	public Response regProcSyncRequest(String url, Object body, String center_machine_refId, String ldt,
			String contentHeader, String regProcAuthToken) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response postResponse = given().cookie(builder.build()).header("Center-Machine-RefId", center_machine_refId)
				.header("timestamp", ldt).relaxedHTTPSValidation().body("\"" + body + "\"").contentType(contentHeader)
				.log().all().when().post(url).then().log().all().extract().response();
		return postResponse;
	}

	public Response regProcPacketUpload(File file, String url, String regProcAuthToken) {

		
		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart("file", file).expect()
				.when().post(url);
	
		return getResponse;

	}

	public Response regProcGetRequest(String url, HashMap<String, String> valueMap, String regProcAuthToken) {
		

		Cookie.Builder builder = new Cookie.Builder("Authorization", regProcAuthToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(valueMap).log()
				.all().when().get(ApplnURI+url).then().log().all().extract().response();
		// log then response

		return getResponse;
	}
	public Response postRequestToDecrypt(String url, Object body, String contentHeader, String acceptHeader,String token) {
		
		Cookie.Builder builder = new Cookie.Builder("Authorization",token);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();

		return postResponse;
	}
	
	public Response regProcPostRequest(String url, HashMap<String, String> valueMap, String contentHeader,String token) {
		
		Cookie.Builder builder = new Cookie.Builder("Authorization", token);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(valueMap)
				.contentType(contentHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
	
		return postResponse;
	}
	
	public Response postRequest(String url, Object body, String contentHeader, String acceptHeader) {

		

		Response postResponse = given().relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(ApplnURI+url).then().log().all()
				.extract().response();
		// log then response
		
		return postResponse;
	}

}
