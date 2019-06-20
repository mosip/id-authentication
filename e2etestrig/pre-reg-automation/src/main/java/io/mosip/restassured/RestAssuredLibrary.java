package io.mosip.restassured;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.HashMap;
import org.apache.log4j.Logger;
import io.mosip.preregistration.util.PreRegistartionUtil;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class RestAssuredLibrary {
	protected static  Logger logger = Logger.getLogger(PreRegistartionUtil.class);

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
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	public Response postRequestWithoutToken(String url, Object body, String contentHeader, String acceptHeader) {
		Response postResponse = given().relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	public Response Post_JSONwithFileWithParm(Object body, File file, String url, String contentHeader,
			HashMap<String, String> parm,String token) {
		logger.info("REST:ASSURED:Sending a data packet to" + url);
		logger.info("Request DTO for document upload is" + body);
		logger.info("Name of the file is" + file.getName());
		Response getResponse = null;
		/*
		 * Fetch to get the param name to be passed in the request  
		 */
		PreRegistartionUtil util=new PreRegistartionUtil();
		String Document_request = util.getProperty().get("req.Documentrequest");

		Cookie.Builder builder = new Cookie.Builder("Authorization", token);
		getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(parm).multiPart("file", file)
				.formParam(Document_request, body).contentType(contentHeader).expect().when().post(url);
		logger.info("REST:ASSURED: The response from request is:" + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	public Response getRequestWithoutParm(String url,String authToken) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().log().all().when().get(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	public Response postRequestWithParm(String url, Object body, String contentHeader, String acceptHeader,HashMap<String, String> pathValue,String authToken) {

		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);

		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().pathParams(pathValue).post(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}
	public Response get_Request_queryParamDataSync(String url, HashMap<String, String> valueMap,String authToken) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", authToken);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap).log()
				.all().when().get(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

}
