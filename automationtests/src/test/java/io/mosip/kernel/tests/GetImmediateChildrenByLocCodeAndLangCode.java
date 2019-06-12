package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.google.common.base.Verify;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Arunakumar.Rati
 *
 */
public class GetImmediateChildrenByLocCodeAndLangCode extends BaseTestCase implements ITest{

	public GetImmediateChildrenByLocCodeAndLangCode(){
		super();
	}

	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetImmediateChildrenByLocCodeAndLangCode.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetImmediateChildrenByLocCodeAndLangCode";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private AssertKernel assertKernel = new AssertKernel();
	private final Map<String, String> props = new CommonLibrary().readProperty("Kernel");
	private final String fetchImmediateChildLocation = props.get("fetchImmediateChildLocation");
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;
	private KernelDataBaseAccess kernelDB=new KernelDataBaseAccess();

	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		 cookie=auth.getAuthForIndividual();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "GetImmediateChildrenByLocCodeAndLangCode")
	public Object[][] readData1(ITestContext context) throws Exception {
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getImmediateChildrenByLocCodeAndLangCode
	 * Given input Json as per defined folders When GET request is sent to /masterdata/v1.0/locations/immediatechildren/{locationcode}/{langcode}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetImmediateChildrenByLocCodeAndLangCode")
	public void getImmediateChildrenByLocCodeAndLangCode(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];

		// Calling the get method 
		Response res=applicationLibrary.getWithPathParam(fetchImmediateChildLocation, actualRequest,cookie);
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
    	  
    	  if(testCaseName.contains("Kernel_GetImmediateChildrenByLocCodeAndLangCode_smoke"))
    	  {
    		  String locationcode= (actualRequest.get("locationcode").toString());
    		  String langCode=actualRequest.get("langcode").toString();
	        
	             String queryStr = "SELECT count(*) FROM master.location l WHERE l.code='"+locationcode+"' and l.lang_code='"+langCode+"'";
					long count = kernelDB.validateDBCount(queryStr,"masterdata");
	       
			if(count==1){
				status =true;
					}
					else{
						status =false;
		 				logger.info("Location count is not equal to 1");
					}
    	  }else
    		  status = true;
			}	
		
		else {
			status=false;
			logger.error(res);
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
