package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class GetDocTypeDocCatByAppID extends BaseTestCase implements ITest {

	public GetDocTypeDocCatByAppID() {
		
		super();
	}
	
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetDocTypeDocCatByAppID.class);
	protected static String testCaseName = "";
	public SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String getDocType_DocCatByAppID = props.get("getDocType_DocCatByAppID");
	private String folderPath = "kernel/GetDocType_DocCatByAppID";
	private String outputFile = "GetDocType_DocCatByAppIDOutput.json";
	private String requestKeyFile = "GetDocType_DocCatByAppIDInput.json";
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private String testParam="";
	KernelAuthentication auth=new KernelAuthentication();
	String cookie;
	
	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public  void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
		
		 cookie=auth.getAuthForIndividual();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "GetDocType_DocCatByAppID")
	public Object[][] readData1(ITestContext context) throws Exception {	 
		switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getDocType_DocCatByAppID
	 * Given input Json as per defined folders When GET request is sent to v1/masterdata/validdocuments/{applicantId}?{langcodes}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetDocType_DocCatByAppID")
	public void getDocTypeDocCatByAppID(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {	
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		
		// Getting the application id
		String applicantId = actualRequest.get("applicantId").toString();
		
		// Getting the List of languages
		List<String> languages = (JSONArray)actualRequest.get("languages");
		
		//Adding application id to path parameters 
		HashMap<String, String> pathPar=new HashMap<>();
		pathPar.put("applicantId", applicantId);
		
		//Adding list of languages as quary parameters 
		HashMap<String, List<String>> queryPar=new HashMap<>();
		queryPar.put("languages",languages);
				
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		// Calling the get method
		Response res=applicationLibrary.getRequestPathQueryPara(getDocType_DocCatByAppID, pathPar,queryPar,cookie);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		listOfElementToRemove.add("responsetime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
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
				f.set(baseTestMethod, GetDocTypeDocCatByAppID.testCaseName);
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/GetDocType_DocCatByAppID/GetDocType_DocCatByAppIDOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to GetDocType_DocCatByAppIDOutput.json file.......................!!");
			}
		}
}
