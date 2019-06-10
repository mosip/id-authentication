package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import org.testng.annotations.AfterClass;
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
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
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
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	private boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String syncConf = props.get("syncConf");
	private String folderPath = "kernel/SyncConfigurations";
	private String outputFile = "SyncConfigurationsOutput.json";
	private String requestKeyFile = "SyncConfigurationsInput.json";
	private String finalStatus = "";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		cookie=auth.getAuthForRegistrationAdmin();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "SyncConfigurations")
	public Object[][] readData1(ITestContext context) throws Exception {
				return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
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
	public void syncConfigurations(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		// Getting configurations from the server
		Response expectedobject=applicationLibrary.getConfigProperties("http://104.211.212.28:51000/registration/"+environment+"/0.12.0/");
		//Getting the registrationConfiguration
		JSONObject regConfig=(JSONObject) ((JSONObject)((JSONArray)((JSONObject) new JSONParser().parse(expectedobject.asString())).get("propertySources")).get(0)).get("source");
		//Getting the globalConfig
		JSONObject globalConfig=(JSONObject) ((JSONObject)((JSONArray)((JSONObject) new JSONParser().parse(expectedobject.asString())).get("propertySources")).get(1)).get("source");
		//Creating a JSONObject and adding both registrationConfiguration and globalConfig
		JSONObject configDetail= new JSONObject();
		configDetail.put("registrationConfiguration", regConfig);
		configDetail.put("globalConfiguration", globalConfig);
		
		// Calling the get method 
		Response res=applicationLibrary.getWithoutParams(syncConf,cookie);
		
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
      if (status) {
	            
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
			logger.error(res);
		}
		
		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus=false;
		if(finalStatus.equals("Fail"))
			setFinalStatus=false;
		else if(finalStatus.equals("Pass"))
			setFinalStatus=true;
		Verify.verify(setFinalStatus);
		softAssert.assertAll();

}
		@SuppressWarnings("static-access")
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
				f.set(baseTestMethod, SyncConfigurations.testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/SyncConfigurations/SyncConfigurationsOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to SyncConfigurationsOutput.json file.......................!!");
			}
		}
}
