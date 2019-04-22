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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Verify;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;
/**
 * @author Arunakumar Rati
 *
 */
public class CentetMachineUserMappingToMasterData extends BaseTestCase implements ITest {

	public CentetMachineUserMappingToMasterData() 
	{
		// TODO Auto-generated constructor stub
		super();
		
	}
	/**
	 *  Declaration of all variables
	 */
	private static Logger logger = Logger.getLogger(CentetMachineUserMappingToMasterData.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String CentetMachineUserMappingToMasterData_uri = "/v1/masterdata/registrationmachineusermappings";
	static String dest = "";
	static String folderPath = "kernel/CentetMachineUserMappingToMasterData";
	static String outputFile = "CentetMachineUserMappingToMasterDataOutput.json";
	static String requestKeyFile = "CentetMachineUserMappingToMasterDataInput.json";
	private static AssertKernel assertKernel = new AssertKernel();
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";

	/*
	 * Data Providers to read the input json files from the folders
	 */
	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		// testName.set(object.get("testCaseName").toString());
		testCaseName = object.get("testCaseName").toString();
	}
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "CentetMachineUserMappingToMasterData")
	public Object[][] readData(ITestContext context) throws JsonParseException, JsonMappingException, IOException, ParseException {
		 String testParam = context.getCurrentXmlTest().getParameter("testType");
		 switch ("smokeAndRegression") {
		case "smoke":
			return ReadFolder.readFolders("kernel/CentetMachineUserMappingToMasterData", "CentetMachineUserMappingToMasterDataOutput.json","CentetMachineUserMappingToMasterDataInput.json","smoke");
			
		case "regression":	
			return ReadFolder.readFolders("kernel/CentetMachineUserMappingToMasterData", "CentetMachineUserMappingToMasterDataOutput.json","CentetMachineUserMappingToMasterDataInput.json","regression");
		default:
			return ReadFolder.readFolders("kernel/CentetMachineUserMappingToMasterData", "createBiometricAuthenticationTypeOutput.json","CentetMachineUserMappingToMasterDataInput.json","smokeAndRegression");
		}
		
	}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * centetMachineUserMappingToMasterData
	 * Given input Json as per defined folders When PUT request is sent to v1/masterdata/registrationmachineusermappings
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	@Test(dataProvider = "CentetMachineUserMappingToMasterData",alwaysRun=true)
	public void centetMachineUserMappingToMasterData(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
	{
		
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		
		/*
		 * Calling the put method
		 */
		  Response response = applicationLibrary.putRequest(actualRequest, CentetMachineUserMappingToMasterData_uri);
		/*
		 *  Removing of unstable attributes from response
		 */
		
		ArrayList<String> listOfElementToRemove=new ArrayList<String>();
		listOfElementToRemove.add("responsetime");
		/*
		 * Comparing expected and actual response
		 */
	
		status = assertKernel.assertKernel(response, Expectedresponse,listOfElementToRemove);
	
      if (status) {
    	  if(testParam.equals("smoke"))
    	  {
    		  
	            
			if(status)
					{
						finalStatus ="Pass";
					}
					else
					{
		 				finalStatus ="Fail";
						//break;
					}
    	  }
				finalStatus = "Pass";
				
      }
		else {
			finalStatus="Fail";
			logger.error(response);
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
		// TODO Auto-generated method stub
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

			f.set(baseTestMethod, CentetMachineUserMappingToMasterData.testCaseName);

			
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}  
	
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/kernel/CentetMachineUserMappingToMasterData/CentetMachineUserMappingToMasterDataOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to CentetMachineUserMappingToMasterDataOutput.json file.......................!!");
		}
	}


}
