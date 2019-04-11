package io.mosip.preregistration.tests;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.ssl.jdbc4.LibPQFactory;

import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.http.Cookie;
import io.restassured.http.Cookie.Builder;
import io.restassured.http.Header;
import io.restassured.response.Response;


public class CommonLibrary_Sample extends BaseTestCase{

	private static Logger logger = Logger.getLogger(CommonLibrary_Sample.class);
	PreRegistrationLibrary lib=new PreRegistrationLibrary();

	 public Response get_Request_pathParameters(String url,HashMap<String, String> valueMap) {
         logger.info("REST-ASSURED: Sending a GET request to " + url);
         Cookie.Builder builder = new Cookie.Builder("Authorization",authToken);
         Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(valueMap)
                     .log().all().when().get(url).then().log().all().extract().response();
         // log then response
         logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
         logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
         return getResponse;
   } 
	
	
	
	

}