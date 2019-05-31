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

import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class GetAllTemplateByTemplateTypeCode extends BaseTestCase implements ITest{

	public GetAllTemplateByTemplateTypeCode() {
		
		super();
	}
	
	
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(GetAllTemplateByTemplateTypeCode.class);
	protected static String testCaseName = "";
	private SoftAssert softAssert=new SoftAssert();
	public JSONArray arr = new JSONArray();
	boolean status = false;
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private final Map<String, String> props = new CommonLibrary().kernenReadProperty();
	private final String fetchAllTemplate = props.get("fetchAllTemplate");
	private String folderPath = "kernel/GetAllTemplateByTemplateTypeCode";
	private String outputFile = "GetAllTemplateByTemplateTypeCodeOutput.json";
	private String requestKeyFile = "GetAllTemplateByTemplateTypeCodeInput.json";
	private AssertKernel assertKernel = new AssertKernel();
	private JSONObject Expectedresponse = null;
	private String finalStatus = "";
	private String testParam="";
	private KernelAuthentication auth=new KernelAuthentication();
	private String cookie;
	private KernelDataBaseAccess kernelDB=new KernelDataBaseAccess();
	
	//Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		testCaseName = object.get("testCaseName").toString();
		 cookie = auth.getAuthForIndividual();
	}
	
	 //Data Providers to read the input json files from the folders
	@DataProvider(name = "GetAllTemplateByTemplateTypeCode")
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
	 * getAllTemplateByTemplateTypeCode
	 * Given input Json as per defined folders When GET request is sent to /masterdata/v1.0/templates/templatetypecodes/{code}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@SuppressWarnings("unchecked")
	@Test(dataProvider="GetAllTemplateByTemplateTypeCode")
	public void getAllTemplateByTemplateTypeCode(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
	    // Calling the get method 
		Response res=applicationLibrary.getRequestPathPara(fetchAllTemplate, actualRequest,cookie);
		
		// Removing of unstable attributes from response
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		
		// Comparing expected and actual response
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
    	  if(testCaseName.equalsIgnoreCase("smoke"))
    	  {

    		/*String id = actualRequest.get("id").toString();
	        String queryStr = "SELECT master.device_master_h.* FROM master.device_master_h WHERE id='"+id+"'";

		    boolean valid = kernelDB.validateDataInDb(queryStr,"masterdata");     
			if(valid)

		        */
			if(status)
					{
						finalStatus ="Pass";
					}
					else
					{
		 				finalStatus ="Fail";
					}
    	  }else
				finalStatus = "Pass";
			
      }
		else {
			finalStatus="Fail";
			logger.error(res);
			softAssert.assertTrue(false);
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
				f.set(baseTestMethod, GetAllTemplateByTemplateTypeCode.testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/GetAllTemplateByTemplateTypeCode/GetAllTemplateByTemplateTypeCodeOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to GetAllTemplateByTemplateTypeCodeOutput.json file.......................!!");
			}
		}


}
