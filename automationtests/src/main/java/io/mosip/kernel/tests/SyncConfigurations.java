package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

import com.google.common.base.Verify;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class SyncConfigurations extends BaseTestCase implements ITest {

	public SyncConfigurations() {
		
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(SyncConfigurations.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "SyncConfigurations";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String syncConf = props.get("syncConf");
	private KernelAuthentication auth=new KernelAuthentication();
	private String build;

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		if(!lib.isValidToken(regAdminCookie))
			regAdminCookie=auth.getAuthForRegistrationAdmin();
		build=io.mosip.report.Reporter.getAppDepolymentVersion().substring(2, 4);
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "SyncConfigurations")
	public Object[][] readData1(ITestContext context) throws Exception {
				return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * syncConfigurations
	 * Given input Json as per defined folders When GET request is sent to /uingenerator/v1.0/uin send
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	
	@SuppressWarnings({ "unchecked" })
	@Test(dataProvider="SyncConfigurations")
	public void syncConfigurations(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// Getting configurations from the server
		Response expectedobject=applicationLibrary.getConfigProperties("http://104.211.212.28:51000/registration/"+environment+"/0."+build+".0/");
		//Getting the registrationConfiguration
		JSONObject regConfig=(JSONObject) ((JSONObject)((JSONArray)((JSONObject) new JSONParser().parse(expectedobject.asString())).get("propertySources")).get(0)).get("source");
		//Getting the globalConfig
		JSONObject globalConfig=(JSONObject) ((JSONObject)((JSONArray)((JSONObject) new JSONParser().parse(expectedobject.asString())).get("propertySources")).get(1)).get("source");
		for(Iterator<String>  iterator = globalConfig.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value=(String) globalConfig.get(key);
			if(value.contains("${mosip.base.url}")) {
				String unwantedString = value.substring(0, 16);
				String wantedString=value.substring(17);
				unwantedString=unwantedString.replace(unwantedString, ApplnURI);
				value=unwantedString+wantedString;
				globalConfig.put(key, value);
				logger.info("value---"+value);
			}
		}
		//Creating a JSONObject and adding both registrationConfiguration and globalConfig
		JSONObject configDetail= new JSONObject();
		configDetail.put("registrationConfiguration", regConfig);
		configDetail.put("globalConfiguration", globalConfig);
		
		// Calling the get method 
		Response res=applicationLibrary.getWithoutParams(syncConf,regAdminCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		//Getting only configDetails from actual response
		JSONObject actualresponse = (JSONObject) ((JSONObject)((JSONObject) new JSONParser().parse(res.asString())).get("response")).get("configDetail");
		String consent_ara=((JSONObject)actualresponse.get("registrationConfiguration")).get("mosip.registration.consent_ara").toString();
		String consent_fra=((JSONObject)actualresponse.get("registrationConfiguration")).get("mosip.registration.consent_fra").toString();
		//adding the unstable elements from actual response to expected response
		regConfig.put("mosip.registration.consent_ara", consent_ara);
		regConfig.put("mosip.registration.consent_fra", consent_fra);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");

		// Comparing expected and actual response
		status = assertKernel.assertKernelWithJsonObject(actualresponse, configDetail,listOfElementToRemove);
 
      if (!status) {
			logger.debug(res);
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
