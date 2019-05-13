package io.mosip.registrationProcessor.tests;

import static io.restassured.RestAssured.given;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.registrationProcessor.util.StageValidationMethods;
import io.mosip.service.BaseTestCase;
import io.mosip.util.TokenGeneration;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class TokenTest extends BaseTestCase {
	TokenGeneration generateToken = new TokenGeneration();
	TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
	StageValidationMethods apiRequest=new StageValidationMethods();
	@Test
	public void request() {
		String tokenGenerationProperties = generateToken.readPropertyFile();
		tokenEntity = generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token = generateToken.getToken(tokenEntity);
		Cookie.Builder builder = new Cookie.Builder("Authorization",token);
		String request="{\r\n" + 
				"  \"id\" : \"\",\r\n" + 
				"  \"version\" : \"1.0\",\r\n" + 
				"  \"requesttime\": \"2019-02-14T12:40:59.768Z\",\r\n" + 
				"  \"request\" : [\r\n" + 
				"	  {\r\n" + 
				"		\"langCode\": \"eng\",\r\n" + 
				"		\"parentRegistrationId\": null,\r\n" + 
				"		\"registrationId\": \"10011100115245020190404123845\",\r\n" + 
				"		\"statusComment\": \"string\",\r\n" + 
				"		\"syncStatus\": \"PRE_SYNC\",\r\n" + 
				"		\"syncType\": \"NEW\"\r\n" + 
				"	  }\r\n" + 
				"	]\r\n" + 
				"}";
	
		JSONParser parser = new JSONParser(); 
		JSONObject body=null;
		try {
			body = (JSONObject) parser.parse(request);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url="https://qa.mosip.io/registrationstatus/registration-processor/sync/v1.0";
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body).contentType( MediaType.APPLICATION_JSON)
				.accept( MediaType.APPLICATION_JSON).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		//logger.info("REST-ASSURED: The response from the request is: " + postResponse.asString());
		//logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		test=extent.createTest("request");
		System.out.println("Response is :: "+ postResponse.asString());
	}
}
