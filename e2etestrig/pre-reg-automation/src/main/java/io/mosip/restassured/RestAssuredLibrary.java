package io.mosip.restassured;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;

import io.mosip.preregistration.util.PreRegistartionUtil;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class RestAssuredLibrary {
	

	/**
	 * Post Request
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return
	 */
	public Response postRequestWithToken(String url, Object body, String contentHeader, String acceptHeader,String authToken) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();
	
		return postResponse;
	}
	public Response postRequestWithoutToken(String url, Object body, String contentHeader, String acceptHeader) {
		Response postResponse = given().relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();

		return postResponse;
	}
	public Response Post_JSONwithFileWithParm(Object body, File file, String url, String contentHeader,
			HashMap<String, String> parm,String token) {

		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request  
		 */
		PreRegistartionUtil util=new PreRegistartionUtil();
		String Document_request = util.getProperty().get("req.Documentrequest");

		Cookie.Builder builder = new Cookie.Builder("Authorization", token);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(parm).multiPart("file", file)
				.formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);

		return getResponse;
	}

	public Response getRequestWithoutParm(String url,String authToken) {
		

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		// log then response

		return getResponse;
	}
	public Response postRequestWithParm(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> pathValue,String authToken) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().pathParams(pathValue).post(url).then().log().all()
				.extract().response();

		return postResponse;
	}
	public Response get_Request_queryParamDataSync(String url, HashMap<String, String> valueMap,String authToken) {
		
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log()
				.all().when().get(url).then().log().all().extract().response();

		return getResponse;
	}

}
