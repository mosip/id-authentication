package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbaccess.KernelMasterDataR;
import io.mosip.dbentity.UinEntity;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertKernel;
import io.mosip.service.BaseTestCase;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class UINStatusCheck extends BaseTestCase implements ITest{

	public UINStatusCheck() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	/**
	 *  Declaration of all variables
	 */
	
	private static Logger logger = Logger.getLogger(UINStatusCheck.class);
	protected static String testCaseName = "";
	static SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	boolean status = false;
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static AssertKernel assertKernel = new AssertKernel();
	private static final String uingenerator = "/v1/uingenerator/uin";
	static String dest = "";
	static String folderPath = "kernel/UINStatusCheck";
	static String outputFile = "UINStatusCheckOutput.json";
	static String requestKeyFile = "UINStatusCheckInput.json";
	static JSONObject Expectedresponse = null;
	String finalStatus = "";
	static String testParam="";
	
	public KernelMasterDataR dbConnection=new KernelMasterDataR();
		
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
	@DataProvider(name = "UINStatusCheck")
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
	
	@Test(dataProvider="UINStatusCheck",invocationCount=1)
	public void getUIN(String testSuite, Integer i, JSONObject object) throws FileNotFoundException, IOException, ParseException
    {
		
		JSONObject actualRequest = ResponseRequestMapper.mapRequest(testSuite, object);
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
		@SuppressWarnings("unchecked")
		/*
		 * Calling the GET method with no parameters
		 */
		String query="select u.uin from kernel.uin u where u.uin_status='UNUSED'";
	
		List<String>list=dbConnection.getData(query);
		
		int total=list.size();
		
		
		Response res=applicationLibrary.getRequestNoParameter(uingenerator);
		String uin_number = res.jsonPath().getMap("response").get("uin").toString();
				
		String query1="select uin_status from kernel.uin where uin='"+uin_number+"'";
		
		List<String> status_list = dbConnection.getData(query1);
		
		String status=status_list.get(0);
		for(String uin:list)
		{
			if(uin.equals(uin_number))
			{
				finalStatus="pass";
				System.out.println(uin+"----------------------");
			
				if(status.equals("ISSUED"))
				{
					finalStatus="pass";
				}
				else {
					finalStatus="fail";
				}
			}else {
				finalStatus="fail";
			}
		}
		
		
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

				f.set(baseTestMethod, UINStatusCheck.testCaseName);

				
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
		
		@AfterClass
		public void updateOutput() throws IOException {
			String configPath = "src/test/resources/kernel/UINStatusCheck/UINStatusCheckOutput.json";
			try (FileWriter file = new FileWriter(configPath)) {
				file.write(arr.toString());
				logger.info("Successfully updated Results to UINStatusCheckOutput.json file.......................!!");
				
				
			}
		}


}
