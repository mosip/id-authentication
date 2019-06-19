package io.mosip.kernel.tests;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Ravi Kant
 *
 */
public class OTP extends BaseTestCase implements ITest {
	OTP() {
		super();
	}

	private static Logger logger = Logger.getLogger(OTP.class);
	private final String moduleName = "kernel";
	private final String apiName = "OTP";
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String OTPGeneration = props.get("OTPGeneration").toString();
	private final String OTPValidation = props.get("OTPValidation").toString();

	protected String testCaseName = "";
	SoftAssert softAssert = new SoftAssert();
	boolean status = true;
	String finalStatus = "";
	public JSONArray arr = new JSONArray();
	Response response = null;
	JSONObject responseObject = null;
	private AssertKernel assertions = new AssertKernel();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;

	/**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 */
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
		cookie=auth.getAuthForRegistrationAdmin();
	}

	/**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 */
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
	}

	/**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void otp(String testcaseName){
		logger.info("Test Case Name:" + testcaseName);

		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);

		JSONObject objectData = objectDataArray[0];
		responseObject = objectDataArray[1];
		String otpCase = null;
				if(objectData.containsKey("case"))
				{
					otpCase = objectData.get("case").toString();
					objectData.remove("case");
					if(otpCase.equals("blockUser")||otpCase.equals("blocked"))
						 {
							JSONObject reqJson = (JSONObject) objectData.get("request");
							//generating a random key of 5 digit
						    String key = String.valueOf(10000 + new Random().nextInt(90000));
						    reqJson.put("key", key);
						    objectData.put("reqJson", reqJson);
						 }
				}
				logger.info("Json Request Is : " + objectData.toJSONString());

				response = applicationLibrary.postWithJson(OTPGeneration, objectData.toJSONString(), cookie);

		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(response);
		// add parameters to remove in response before comparison like time stamp
		ArrayList<String> listOfElementToRemove = new ArrayList<String>();
		listOfElementToRemove.add("responsetime");

		int statusCode = response.statusCode();

		if (otpCase!=null && !otpCase.equals("blocked")) {
			if (statusCode == 200) {
				
				String otp = ((HashMap<String, String>)response.jsonPath().get("response")).get("otp").toString();
				logger.info("otp is : " + otp);
				JSONObject reqJson = (JSONObject) objectData.get("request");
				switch(otpCase) {
				
				case "blockUser":
					reqJson.put("otp", "000000");

					// reading validation attempt from property
					int attempt = Integer.parseInt(props.get("attempt").toString());
					for(int i=0;i<attempt;i++)
					{
						response = applicationLibrary.getWithQueryParam(OTPValidation, reqJson,cookie);

					}
					break;
				case "otherKey":
					reqJson.put("otp", otp);
					reqJson.put("key", "otherKey");
					break;
				case "alphanumeric":
					reqJson.put("otp", "12ad23");
					break;
				case "otpEmpty":
					reqJson.put("otp", "");
					break;
				case "keyEmpty":
					reqJson.put("key", "");
					reqJson.put("otp", otp);
					break;
				case "expired":
					try {
						// reading validation timeout from property
						logger.info("waiting for 181 seconds to expire the OTP");
						TimeUnit.SECONDS.sleep(Integer.parseInt(props.get("OTPTimeOut").toString()));
						cookie=auth.getAuthForRegistrationAdmin();
					} catch (InterruptedException e) {
						logger.info(e.getMessage());
					}
					reqJson.put("otp", otp);
					break;
				case "incorrect":
					reqJson.put("otp", "000000");
					break;
				default:
					reqJson.put("otp", otp);
				}
				
				response = applicationLibrary.getWithQueryParam(OTPValidation, reqJson,cookie);
				//This method is for checking the authentication is pass or fail in rest services
				new CommonLibrary().responseAuthValidation(response);
				logger.info("Obtained Response: " + response);
				logger.info("Expected Response:" + responseObject.toJSONString());
				status = assertions.assertKernel( response, responseObject, listOfElementToRemove);

			}
		} else {
			if (otpCase!=null && otpCase.equals("blocked"))
			{
				
				JSONObject reqJson = (JSONObject) objectData.get("request");
				reqJson.put("otp", "000000");

				int attempt = Integer.parseInt(props.get("attempt").toString());
				for(int i=0;i<=attempt;i++)
				{
					response = applicationLibrary.getWithQueryParam(OTPValidation, reqJson,cookie);
				}
				reqJson.remove("otp");
				response = applicationLibrary.postWithJson(OTPGeneration, objectData.toJSONString(),cookie);
			}
			listOfElementToRemove.add("otp");
			logger.info("Obtained Response: " + response);
			logger.info("Expected Response:" + responseObject.toJSONString());
			status = assertions.assertKernel(response, responseObject, listOfElementToRemove);

		}

		if (!status) {
			logger.debug(response);
		}
		Verify.verify(status);
		softAssert.assertAll();
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

}
