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

import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.kernel.service.AssertKernel;
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author Arunakumar Rati
 *
 */
public class GetAllTemplateByTemplateTypeCode extends BaseTestCase implements ITest{

	public GetAllTemplateByTemplateTypeCode() {
		
		super();
	}

	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetAllTemplateByTemplateTypeCode.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "GetAllTemplateByTemplateTypeCode";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String fetchAllTemplate = props.get("fetchAllTemplate");
	private AssertKernel assertKernel = new AssertKernel();
	private JSONObject Expectedresponse = null;
	private KernelAuthentication auth=new KernelAuthentication();
	private KernelDataBaseAccess kernelDB=new KernelDataBaseAccess();
	
	//Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = moduleName+"_"+apiName+"_"+object.toString();
		if(!lib.isValidToken(individualCookie))
			individualCookie = auth.getAuthForIndividual();
	}
	
	 //Data Providers to read the input json files from the folders
	@DataProvider(name = "GetAllTemplateByTemplateTypeCode")
	public Object[][] readData1(ITestContext context) throws Exception { 
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
		}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * getAllTemplateByTemplateTypeCode
	 * Given input Json as per defined folders When GET request is sent to /masterdata/v1.0/templates/templatetypecodes/{code}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetAllTemplateByTemplateTypeCode")
	public void getAllTemplateByTemplateTypeCode(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		// getting request and expected response jsondata from json files.
		JSONObject objectDataArray[] = new TestCaseReader().readRequestResponseJson(moduleName, apiName, testcaseName);
		JSONObject actualRequest = objectDataArray[0];
		Expectedresponse = objectDataArray[1];
	    // Calling the get method 
		Response res=applicationLibrary.getWithPathParam(fetchAllTemplate, actualRequest,individualCookie);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
    	  if(testCaseName.equalsIgnoreCase("Kernel_GetAllTemplateByTemplateTypeCode_smoke"))
    	  {
    		String id = actualRequest.get("code").toString();
	        String queryStr = "SELECT count(*) FROM master.template WHERE template_typ_code='"+id+"'";

		     long count = kernelDB.validateDBCount(queryStr,"masterdata");     
			if(count==3) {
						status =true;
					}
					else {
						status =false;
						logger.debug(res.asString());
					}
    	  }else
				status = true;
			
      }
		else {
			status =false;
			logger.debug(res.asString());
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