package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.dbdto.LocationDto;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Arunakumar.Rati
 *
 */
public class GetImmediateChildrenByLocCodeAndLangCode extends BaseTestCase implements ITest{

	public GetImmediateChildrenByLocCodeAndLangCode() 
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *  Declaration of all variables
	 */
	private static Logger logger = Logger.getLogger(GetImmediateChildrenByLocCodeAndLangCode.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static AssertKernel assertKernel = new AssertKernel();
	private static final String fetchImmediateChildLocation = "/v1/masterdata/locations/immediatechildren/{locationcode}/{langcode}";
	static String dest = "";
	static String folderPath = "kernel/GetImmediateChildrenByLocCodeAndLangCode";
	static String outputFile = "GetImmediateChildrenByLocCodeAndLCOutput.json";
	static String requestKeyFile = "GetImmediateChildrenByLocCodeAndLCInput.json";
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";
	/*
	 * Data Providers to read the input json files from the folders
	 */
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		
		testCaseName = object.get("testCaseName").toString();
	} 
	
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "GetImmediateChildrenByLocCodeAndLangCode")
	public static Object[][] readData1(ITestContext context) throws Exception {
		//CommonLibrary.configFileWriter(folderPath,requestKeyFile,"DemographicCreate","smokePreReg");
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smokeAndRegression") {
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
	 * getImmediateChildrenByLocCodeAndLangCode
	 * Given input Json as per defined folders When GET request is sent to /masterdata/v1.0/locations/immediatechildren/{locationcode}/{langcode}
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider="GetImmediateChildrenByLocCodeAndLangCode")
	public void getImmediateChildrenByLocCodeAndLangCode(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		@SuppressWarnings("unchecked")
		
		/*
		 * Calling GET method with path parameters
		 */
		Response res=applicationLibrary.getRequestPathPara(fetchImmediateChildLocation, actualRequest);
		
		/*
		   Removing of unstable attributes from response
		*/
		
		outerKeys.add("timestamp");
		innerKeys.add("errorMessage");
		
		/*
		  Comparing expected and actual response
		 */
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("timestamp");
		status = assertKernel.assertKernel(res, Expectedresponse,listOfElementToRemove);
      if (status) {
    	  
    	  if(testCaseName.contains("smoke"))
    	  {
    		  String locationcode= (actualRequest.get("locationcode").toString());
    		  String langCode=actualRequest.get("langcode").toString();
	            
	             String queryStr = "SELECT master.location.* FROM master.location WHERE code='"+locationcode+"' and lang_code='"+langCode+"'";
					boolean valid = KernelMasterDataR.masterDataDBConnection(LocationDto.class,queryStr); 
	            
			if(valid)
					{
						finalStatus ="Pass";
					}
					else
					{
		 				finalStatus ="Fail";
						//break;
					}
    	  }else
				finalStatus = "Pass";
			}	
		
		else {
			finalStatus="Fail";
			logger.error(res);
			//softAssert.assertTrue(false);
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

				f.set(baseTestMethod, GetImmediateChildrenByLocCodeAndLangCode.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/GetImmediateChildrenByLocCodeAndLangCode/GetImmediateChildrenByLocCodeAndLCOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to GetImmediateChildrenByLocCodeAndLCOutput.json file.......................!!");
			}
		}

}
