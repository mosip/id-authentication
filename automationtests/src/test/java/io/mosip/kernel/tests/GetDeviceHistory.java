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
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;
/**
 * @author Arunakumar Rati
 *
 */
public class GetDeviceHistory extends BaseTestCase implements ITest{

	public GetDeviceHistory() {
		super();
	}
	
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetDeviceHistory.class);
	protected static String testCaseName = "";
	public SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String fetchDeviceHistory = props.get("fetchDeviceHistory");
	private String folderPath = "kernel/GetDeviceHistory";
	private String outputFile = "GetDeviceHistoryOutput.json";
	private String requestKeyFile = "GetDeviceHistoryInput.json";
	private AssertKernel assertKernel = new AssertKernel();
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	public KernelAuthentication auth=new KernelAuthentication();
	private String cookie;
	private KernelDataBaseAccess kernelDB=new KernelDataBaseAccess();

	//Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		 cookie = auth.getAuthForRegistrationProcessor();
	} 
	
	//Data Providers to read the input json files from the folders
	@DataProvider(name = "GetDeviceHistory")
	public Object[][] readData1(ITestContext context) throws Exception {	 
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, testLevel);
		}	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getDeviceHistory
	 * Given input Json as per defined folders When GET request is sent to v1/masterdata/deviceshistories/{id}/{langcode}/{effdatetimes}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetDeviceHistory")
	public void getDeviceHistory(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Calling GET method with path parameters
		Response res=applicationLibrary.getRequestPathPara(fetchDeviceHistory, actualRequest,cookie);
		
		//  Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		listOfElementToRemove.add("timestamp");
		
		if(testCaseName.equals("Kernel_GetDeviceHistory_smoke_1")|| testCaseName.equals("Kernel_GetDeviceHistory_response_time")) {
			String effectDateTime = res.jsonPath().get("response.deviceHistoryDetails[0].effectDateTime");
			((JSONObject)((JSONArray)((JSONObject)Expectedresponse.get("response")).get("deviceHistoryDetails")).get(0)).put("effectDateTime", effectDateTime).toString();
		}
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
    	  if(testCaseName.equals("Kernel_GetDeviceHistory_smoke_1"))
    	  {
    		String id = actualRequest.get("id").toString();
	        String queryStr = "SELECT count(*) FROM master.device_master_h h WHERE h.id='"+id+"'";
	        long count = kernelDB.validateDBCount(queryStr,"masterdata");   
	        if(count==1) {
	        	finalStatus = "Pass";
	        }else {
	        	finalStatus="Fail";
	        	logger.info("Device History is not equal to 1");
	        }
    	  }else
				finalStatus = "Pass";
      }
		else {
			finalStatus="Fail";
			logger.error(res);
		}
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

				f.set(baseTestMethod, GetDeviceHistory.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/GetDeviceHistory/GetDeviceHistoryOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to GetDeviceHistoryOutput.json file.......................!!");
			}
		}

}
