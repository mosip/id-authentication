package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.mosip.util.UIN_Assertions;
import io.restassured.response.Response;
/**
 * @author Arunakumar Rati
 *
 */
public class UINGeneration extends BaseTestCase implements ITest{
	
	public UINGeneration()
	{
		super();
	}
	/**
	 *  Declaration of all variables
	 */
	private static Logger logger = Logger.getLogger(UINGeneration.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static AssertKernel assertKernel = new AssertKernel();
	private static final String uingenerator = "/v1/uingenerator/uin";
	static String dest = "";
	static String folderPath = "kernel/UINGeneration";
	static String outputFile = "UINGenerationOutput.json";
	static String requestKeyFile = "UINGenerationInput.json";
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";
	String alphanumeric_regEx="^[a-zA-Z0-9]*$";
	
	/*
	 * Data Providers to read the input json files from the folders
	 */
	@BeforeMethod(alwaysRun=true)
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
		
		testCaseName = object.get("testCaseName").toString();
	} 
	
	/**
	 * @return input jsons folders
	 * @throws Exception
	 */
	@DataProvider(name = "UINValidator")
	public static Object[][] readData1(ITestContext context) throws Exception {
		//CommonLibrary.configFileWriter(folderPath,requestKeyFile,"DemographicCreate","smokePreReg");
		 testParam = context.getCurrentXmlTest().getParameter("testType");
		switch ("smoke") {
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
	 * getRegCenterByID_Timestamp
	 * Given input Json as per defined folders When GET request is sent to /uingenerator/v1.0/uin send
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	
	@Test(dataProvider="UINValidator",invocationCount=1)
	public void getUIN(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		@SuppressWarnings("unchecked")
		/*
		 * Calling the GET method with no parameters
		 */
		Response res=applicationLibrary.getRequestNoParameter(uingenerator);
		
		 String uin = res.jsonPath().getMap("response").get("uin").toString();
		 
		 int uin_length=uin.length();
		 String first_half=uin.substring(0, uin_length/2);
		 String second_half=uin.substring(uin_length/2);
		 String rev_half="";
		 for(int j=second_half.length()-1;j>=0;j--){
			 rev_half=rev_half+second_half.charAt(j);
		 }
		boolean isAscending = UIN_Assertions.ascendingMethod(uin);
     	boolean isDescending = UIN_Assertions.ascendingMethod(uin);
     	boolean alpanumeric = UIN_Assertions.asserUinWithPattern(uin, alphanumeric_regEx);
     	
		if(uin_length==10){
        	   if(first_half.equals(second_half)){ 
        		   finalStatus="fail";
        	   }else {
        		   if(first_half.equals(rev_half)&&isAscending&&isDescending&&alpanumeric){
        			   finalStatus="fail";
        		   }else{
        			   String first2=uin.substring(0,1);int count =1;
        				for(int k=2;k<uin.length();k++){
        					if(first2.equals(uin.substring(k, i+k))){
        						count++;
        					}
        				}if(count==5)
        					finalStatus="fail";
        				else
        					finalStatus="pass";
        		   }
           }   
        	
        	  
           }
		else
			finalStatus="fail";
        		   

		softAssert.assertAll();
		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus=false;
		if(finalStatus.equals("Fail"))
			setFinalStatus=false;
		else if(finalStatus.equals("Pass"))
			setFinalStatus=true;
		
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

				f.set(baseTestMethod, UINGeneration.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/UINGeneration/UINGenerationOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to UINGenerationOutput.json file.......................!!");
			}
		}

		
		
	
}
