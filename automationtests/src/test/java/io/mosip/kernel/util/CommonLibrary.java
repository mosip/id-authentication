package io.mosip.kernel.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.mosip.service.BaseTestCase;
import io.restassured.http.Cookie;
import io.restassured.response.Response;

public class CommonLibrary extends BaseTestCase {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static void configFileWriter(String folderPath, String requestKeyFile, String generationType,
			String baseFileName) throws Exception {
		String splitRegex = Pattern.quote(System.getProperty("file.separator"));
		folderPath.split(splitRegex);
		String requestFilePath = "src/test/resources/" + folderPath + "/" + requestKeyFile;
		String configFilePath = "src/test/resources/" + folderPath + "/" + "FieldConfig.properties";

		JSONObject requestKeys = (JSONObject) new JSONParser().parse(new FileReader(requestFilePath));
		Properties properties = new Properties();
		Properties cloneProperties = new Properties();
		properties.load(new FileReader(new File(configFilePath)));
		cloneProperties.load(new FileReader(new File(configFilePath)));
		try {
			for (Object key : requestKeys.keySet()) {

				if (properties.getProperty(key.toString()) != null) {
					properties.setProperty(key.toString(), "invalid");
					properties.setProperty("filename", "invalid_" + key.toString());
					File file = new File(configFilePath);
					FileOutputStream fileOut = new FileOutputStream(file);
					properties.store(fileOut, "FieldConfig.properties");
					/*
					 * try { new Main().TestRequestReponseGenerator(api, testSuite,generationType);
					 * }catch(org.json.JSONException exp) { exp.printStackTrace(); }
					 */
					properties.remove(key.toString());
					properties.setProperty(key.toString(), "valid");
					properties.remove("filename");
					properties.setProperty("filename", baseFileName);
					properties.store(fileOut, "FieldConfig.properties");
					fileOut.close();
				}

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		cloneProperties.remove("prereg_id_custom");
		cloneProperties.setProperty("prereg_id_custom", "");
		properties.clear();
		File file = new File(configFilePath);
		FileOutputStream fileOut = new FileOutputStream(file);
		properties.store(fileOut, null);
		cloneProperties.store(fileOut, null);
	}

	/**
	 * @param propertyFileName
	 * @return this method is for reading property file from src/config folder by
	 *         passing the property file name
	 */
	public Map<String, String> readProperty(String propertyFileName) {
		Properties prop = new Properties();
		try {
			prop.load(CommonLibrary.class.getResourceAsStream("/mosip-api-resources/config/"+ propertyFileName + ".properties"));
			
		} catch (IOException e) {

			logger.info(e.getMessage());
		}

		Map<String, String> mapProp = prop.entrySet().stream()
				.collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));

		return mapProp;
	}

	/**
	 * @param response
	 *            This method is for checking the authentication is pass or fail in
	 *            rest services
	 */
	public void responseAuthValidation(Response response) {
		JSONArray errors = null;
		String errorCode = null;
		String errorMessage = null;
		int statusCode = response.getStatusCode();
		// fetching json array of objects from response
		try {
			if (statusCode > 500)
				Assert.assertTrue(false, "Service is Unavailable and the statusCode=" + statusCode);

			errors = (JSONArray) ((JSONObject) new JSONParser().parse(response.asString())).get("errors");
		} catch (ParseException pe) {
			Assert.assertTrue(false, "Response from the service is not able to parse and exception is" + pe.getClass());
		} catch (NullPointerException npe) {
			Assert.assertTrue(false, "Errors in the response is not null and exception is " + npe.getClass());
		}
		if (errors != null) {
			try {
				errorCode = ((JSONObject) errors.get(0)).get("errorCode").toString();
				errorMessage = ((JSONObject) errors.get(0)).get("message").toString();
			} catch (IndexOutOfBoundsException aibe) {
				Assert.assertTrue(false,
						"Not able to find the errorCode or errorMessage from errors array and exception is "
								+ aibe.getClass());
			}

			if (errorCode.contains("ATH")) {
				Assert.assertTrue(false,
						"Failed due to Authentication failure. Error message is='" + errorMessage + "'");
			}
		}
	}

	/**
	 * @param cookie
	 * @return this method is for checking cookie(token) is expired or not.
	 */
	public boolean isValidToken(String cookie) {
		// we will have to read configCookieTime, token and secret from property file
		String token_base = "Mosip-Token";
		String secret = "authjwtsecret";
		long configCookieTime = 20;
		Integer cookieGenerationTimeMili = null;

		try {
			cookieGenerationTimeMili = (Integer) Jwts.parser().setSigningKey(secret)
					.parseClaimsJws(cookie.substring(token_base.length())).getBody().get("iat");
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
				| IllegalArgumentException | NullPointerException e) {
			logger.info(e.getMessage());
			return false;
		}

		Date date = new Date(Long.parseLong(Integer.toString(cookieGenerationTimeMili)) * 1000);
		Date currentDate = new Date();
		long intervalMin = (currentDate.getTime() - date.getTime()) / (60 * 1000) % 60;

		if (intervalMin <= configCookieTime)
			return true;
		else
			return false;

	}

	/**
	 * This method is for generating the random alphanumeric string of required
	 * length
	 */
	public String randomAlphaNumeric(int lengthOfString) {
		StringBuilder builder = new StringBuilder();
		while (lengthOfString-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	/**
	 * @param source
	 * @param destination
	 *            this method is for copying a file from given source to given
	 *            destination. used by preregistration
	 */
	public static void backUpFiles(String source, String destination) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(Date.from(Instant.now()));

		String result = String.format("%1$tY-%1$tm-%1$td-%1$tk-%1$tS-%1$tp", cal);
		String filePath = "src/test/resources/APPDATA/MosipUtil/UtilFiles/" + destination + "/" + result;
		File sourceFolder = new File(source);
		File dest = new File(filePath);
		try {
			FileUtils.copyDirectory(sourceFolder, dest);
			logger.info("Please Check Your %APPDATA% in C drive to get access to the generted files");
		} catch (IOException e) {
			logger.info("Check %APPDATA%");
		}
	}

	/**
	 * @param response
	 *            this method is for logging the response in case of error only.
	 *            this is used in get request response logging
	 */
	public void responseLogger(Response response) {
		int statusCode = response.statusCode();
		if (statusCode < 200 || statusCode > 299) {
			logger.info(response.asString());
		} else
			logger.info("status code: " + statusCode + "(success)");

	}

	// rest services methods.

	// Post Requests:
	/**
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for post request with authentication(cookie) and only
	 *         with jsonData in request body.
	 */
	public Response postWithoutJson(String url, String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @return this method is for post request without authentication(cookie) and
	 *         only with jsonData in request body.
	 */
	public Response postWithJson(String url, Object body, String contentHeader, String acceptHeader) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Response postResponse = given().relaxedHTTPSValidation().body(body).contentType(contentHeader)
				.accept(acceptHeader).log().all().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for post request with authentication(cookie) and only
	 *         with jsonData in request body.
	 */
	public Response postWithJson(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param pathParams
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for post request with authentication(cookie) with
	 *         pathParams and jsonData in request body.
	 */
	public Response postWithPathParams(String url, Object body, HashMap<String, String> pathParams,
			String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.body(body).contentType(contentHeader).accept(acceptHeader).log().all().when().post(url).then().log()
				.all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param file
	 * @param url
	 * @param cookie
	 * @return this method is for post request with authentication(cookie) and only
	 *         with File in request body.
	 */
	public Response postWithOnlyFile(String url, File file, String fileKeyName, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
				.expect().when().post(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param file
	 * @param url
	 * @param cookie
	 * @return this method is for post request with authentication(cookie), jsonData
	 *         and with File in request body.
	 */
	public Response postWithFile(String url, Object body, File file, String fileKeyName, String contentHeader,
			String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
				.body(body).contentType(contentHeader).expect().when().post(url).then().log().all().extract()
				.response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param formParams
	 * @param file
	 * @param url
	 * @param contentHeader
	 * @param cookie
	 * @return this method is for post request with authentication(cookie)
	 *         containing file and Map of form params(name, value).
	 */
	public Response postWithFileFormParams(String url, HashMap<String, String> formParams, File file,
			String fileKeyName, String contentHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		logger.info("Name of the file is" + file.getName());
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().multiPart(fileKeyName, file)
				.formParams(formParams).contentType(contentHeader).expect().when().post(url);
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param formParams
	 * @param file
	 * @param url
	 * @param contentHeader
	 * @param pathParams
	 * @param cookie
	 * @return this method is for post request with authentication(cookie)
	 *         containing file, Map of pathParams(name, value) and Map of
	 *         formParams(name, value).
	 */
	public Response postWithFilePathParamsFormParams(String url, HashMap<String, String> pathParams,
			HashMap<String, String> formParams, File file, String fileKeyName, String contentHeader, String cookie) {
		logger.info("REST:ASSURED:Sending post request to" + url);
		logger.info("Name of the file is" + file.getName());

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.multiPart(fileKeyName, file).formParams(formParams).contentType(contentHeader).expect().when()
				.post(url);
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @param queryparams
	 * @param cookie
	 * @return this method is for post request with authentication(cookie)
	 *         containing json body with Map of queryPaarams(name, value).
	 */
	public Response postWithQueryParams(String url, HashMap<String, String> queryparams, Object body,
			String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a POST request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.queryParams(queryparams).contentType(contentHeader).accept(acceptHeader).log().all().when().post(url)
				.then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param headers
	 * @param contentHeader
	 * @param regProcAuthToken
	 * @return this method is for post request with authentication(cookie)
	 *         containing json body and Map of headers(name, value). Used by
	 *         RegProc.
	 */
	public Response postWithMultiHeaders(String endpoint, Object body, HashMap<String, String> headers,
			String contentHeader, String cookie) {
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = given().cookie(builder.build()).headers(headers).relaxedHTTPSValidation()
				.body("\"" + body + "\"").contentType(contentHeader).log().all().when().post(endpoint).then().log()
				.all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from request is: " + postResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	/**
	 * @param jsonString
	 * @param serviceUri
	 * @param cookie
	 * @return this method is specifically for email notification with
	 *         authorization(cookie) and multiPart data.
	 */
	public Response postRequestEmailNotification(String serviceUri, JSONObject jsonString, String cookie) {
		logger.info("REST-ASSURED: Sending a POST request to " + serviceUri);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response postResponse = null;
		if (jsonString.get("attachments").toString().isEmpty()) {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		} else {
			postResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType("multipart/form-data")
					.multiPart("attachments", new File((String) jsonString.get("attachments")))
					.multiPart("mailContent", (String) jsonString.get("mailContent"))
					.multiPart("mailTo", (String) jsonString.get("mailTo"))
					.multiPart("mailSubject", (String) jsonString.get("mailSubject"))
					.multiPart("mailCc", (String) jsonString.get("mailCc")).post(serviceUri).then().log().all()
					.extract().response();
		}

		// log then response
		logger.info("REST-ASSURED: The response Time is: " + postResponse.time());
		return postResponse;
	}

	// Patch requests:
	/**
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for Patch Request with authentication(cookie) and Json
	 *         body.
	 */
	public Response patchRequest(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a Patch request to " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().patch(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	// Get Requests:
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
		responseLogger(getResponse);
		logger.info("REST-ASSURED: the response Time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param patharams
	 * @param cookie
	 * @return this method is for get request with authentication(cookie) and with
	 *         pathParams Map(name, Value).
	 */
	public Response getWithPathParam(String url, HashMap<String, String> patharams, String cookie) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(patharams).log()
				.all().when().get(url);
		// log then response
		responseLogger(getResponse);
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param queryParams
	 * @param cookie
	 * @return this method is for get request with authentication(cookie) and with
	 *         queryParams Map(name, Value).
	 */
	public Response getWithQueryParam(String url, HashMap<String, String> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log()
				.all().when().get(url);
		// log then response
		responseLogger(getResponse);
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param queryParams
	 * @param cookie
	 * @return this method is for get request with authentication(cookie) and with
	 *         List of queryParams(Map(name, List<Value>)) with same name.
	 */
	public Response getWithQueryParamList(String url, HashMap<String, List<String>> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log()
				.all().when().get(url);
		// log then response
		responseLogger(getResponse);
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param queryParams
	 * @param cookie
	 * @return this method is for get request with authentication(cookie),
	 *         pathParams Map(name, Value) and with queryParams Map(name, Value).
	 */
	public Response getWithPathQueryParam(String url, HashMap<String, String> pathParams,
			HashMap<String, String> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.queryParams(queryParams).log().all().when().get(url);
		// log then response
		responseLogger(getResponse);
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param queryParams
	 * @param cookie
	 * @return this method is for get request with authentication(cookie),
	 *         pathParams Map(name, Value) and with queryParams(Map(name,
	 *         List<Value>)) with same name.
	 */
	public Response getWithPathParamQueryParamList(String url, HashMap<String, String> pathParams,
			HashMap<String, List<String>> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);

		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.queryParams(queryParams).log().all().when().get(url);
		// log then response
		responseLogger(getResponse);
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
	// Put Requests:

	/**
	 * @param url
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for put request with Authentication(cookie) and
	 *         without any data.
	 */
	public Response putWithoutData(String url, String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a PUT request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().contentType(contentHeader)
				.accept(acceptHeader).log().all().when().put(url).then().log().all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * @param url
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for Put request with authentication(cookie) and only
	 *         with jsonData in request body
	 */
	public Response putWithJson(String url, Object body, String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a PUT request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().body(body)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for Put request with authentication(cookie) and only
	 *         with pathParams.
	 */
	public Response putWithPathParams(String url, HashMap<String, String> pathParams, String contentHeader,
			String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a PUT request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for Put request with authentication(cookie) and only
	 *         with pathParams.
	 */
	public Response putWithQueryParams(String url, HashMap<String, String> queryParams, String contentHeader,
			String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a PUT request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams)
				.contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log().all()
				.extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param body
	 * @param contentHeader
	 * @param acceptHeader
	 * @param cookie
	 * @return this method is for Put request with authentication(cookie) with
	 *         pathParams and jsonData in request body
	 */
	public Response putWithPathParamsBody(String url, HashMap<String, String> pathParams, Object body,
			String contentHeader, String acceptHeader, String cookie) {
		logger.info("REST-ASSURED: Sending a PUT request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response putResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.body(body).contentType(contentHeader).accept(acceptHeader).log().all().when().put(url).then().log()
				.all().extract().response();
		// log then response
		logger.info("REST-ASSURED: The response from the request is: " + putResponse.asString());
		logger.info("REST-ASSURED: The response Time is: " + putResponse.time());
		return putResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param cookie
	 * @return this method is for delete request with authorization(cookie) and
	 *         pathParam Map(name, value).
	 */
	public Response deleteWithPathParams(String url, HashMap<String, String> pathParams, String cookie) {
		logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams).log()
				.all().when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param cookie
	 * @return this method is for delete request with authorization(cookie) and
	 *         queryParam Map(name, value).
	 */
	public Response deleteWithQueryParams(String url, HashMap<String, String> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().queryParams(queryParams).log()
				.all().when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	/**
	 * @param url
	 * @param pathParams
	 * @param queryParams
	 * @param cookie
	 * @return this method is for delete request with authorization(cookie),
	 *         queryParam Map(name, value) and pathParam Map(name, value).
	 */
	public Response deleteWithPathQueryParams(String url, HashMap<String, String> pathParams,
			HashMap<String, String> queryParams, String cookie) {
		logger.info("REST-ASSURED: Sending a DELETE request to   " + url);
		Cookie.Builder builder = new Cookie.Builder("Authorization", cookie);
		Response getResponse = given().cookie(builder.build()).relaxedHTTPSValidation().pathParams(pathParams)
				.queryParams(queryParams).log().all().when().delete(url).then().log().all().extract().response();
		logger.info("REST-ASSURED: The response from the request is: " + getResponse.asString());
		logger.info("REST-ASSURED: the response time is: " + getResponse.time());
		return getResponse;
	}

	// config property reader

	/**
	 * @param url
	 *            (complete url)
	 * @return reads the config property
	 */
	public Response getConfigProperties(String url) {
		logger.info("REST-ASSURED: Sending a GET request to " + url);
		Response getResponse = given().relaxedHTTPSValidation().log().all().when().get(url).then().log().all().extract()
				.response();
		// log then response
		logger.info("REST-ASSURED: The response Time is: " + getResponse.time());
		return getResponse;
	}
}
