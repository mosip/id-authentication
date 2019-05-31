package io.mosip.preregistration.tests;

/**
 * @author Ashish
 */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbHealthcheck.DBHealthCheck;
import io.mosip.dbaccess.PreRegDbread;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertPreReg;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

public class FetchAllPreRegistrationIds extends BaseTestCase implements ITest{
	
	public 	String preId="";
	public SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(FetchAllPreRegistrationIds.class);
	boolean status = false;
	public String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	public Response Actualresponse = null;
	public JSONObject Expectedresponse = null;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static  String preReg_URI;
	public String dest = "";
	public String configPaths="";
	public String folderPath = "preReg/Fetch_all_PreRegistration_Ids";
	public String outputFile = "Fetch_all_PreRegistration_IdsOutput.json";
	public String requestKeyFile = "Fetch_all_PreRegistration_IdsRequest.json";
	
	public FetchAllPreRegistrationIds() {
		super();	
	}
	private static CommonLibrary commonLibrary = new CommonLibrary();
	/**
	 * Data Providers to read the input json files from the folders
	 * @param context
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	
	@DataProvider(name = "Fetch_all_PreRegistration_Ids")
	public Object[][] readData(ITestContext context) throws JsonParseException, JsonMappingException, IOException, ParseException {
		 switch (testLevel) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"smoke");
			
		case "regression":	
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile,requestKeyFile,"smokeAndRegression");
		}
		
	}
	
	/**
	 * Script for testing the Fetch_all_PreRegistration_Ids api
	 * @param testSuite
	 * @param i
	 * @param object
	 * @throws Exception
	 */
	@Test(dataProvider = "Fetch_all_PreRegistration_Ids")
	public void fetchAllPreRegistrationIds(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		PreRegistrationLibrary lib=new PreRegistrationLibrary();
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		actualRequest.put("requesttime",lib.getCurrentDate());
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		try {
			Actualresponse = applicationLibrary.dataSyncPostRequest(actualRequest.toJSONString(), preReg_URI);
			
		} catch (Exception e) {
			logger.info(e);
		}
		//String statusCode = Actualresponse.jsonPath().get("status").toString();
		
		/**
		 * Removing dynamic element from actual and expected response
		 */
				outerKeys.add("responsetime");
				innerKeys.add("transactionId");
				innerKeys.add("preRegistrationIds");
				innerKeys.add("countOfPreRegIds");
				status = AssertResponses.assertResponses(Actualresponse, Expectedresponse, outerKeys, innerKeys);
				if (status) {
					finalStatus="Pass";		
				softAssert.assertAll();
				object.put("status", finalStatus);
				arr.add(object);
				}
				else {
					finalStatus="Fail";
				}
				boolean setFinalStatus=false;
	            if(finalStatus.equals("Fail"))
	                  setFinalStatus=false;
	            else if(finalStatus.equals("Pass"))
	                  setFinalStatus=true;
	            Verify.verify(setFinalStatus);
	            softAssert.assertAll();

	}
	/**
	 * Writing response to the specified config path
	 * @throws IOException
	 */
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/preReg/Fetch_all_PreRegistration_Ids/Fetch_all_PreRegistration_IdsOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to Fetch_all_PreRegistration_IdsOutput.json file.......................!!");
		
		}
		//CommonLibrary.backUpFiles(configPaths, dest);
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
                f.set(baseTestMethod, FetchAllPreRegistrationIds.testCaseName);
          } catch (Exception e) {
                Reporter.log("Exception : " + e.getMessage());
          }
    }


    @BeforeMethod(alwaysRun = true)
    public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
          JSONObject object = (JSONObject) testdata[2];
          testCaseName = object.get("testCaseName").toString();
   
          preReg_URI = commonLibrary.fetch_IDRepo().get("preReg_FetchAllPreRegistrationIdsURI");
    }


    @Override
    public String getTestName() {
          return this.testCaseName;
    }
	
}
