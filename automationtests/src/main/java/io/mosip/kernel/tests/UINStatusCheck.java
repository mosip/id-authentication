package io.mosip.kernel.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
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
import io.mosip.kernel.util.CommonLibrary;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.mosip.kernel.util.TestCaseReader;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

/**
 * @author M9010714
 *
 */
public class UINStatusCheck extends BaseTestCase implements ITest{

	public UINStatusCheck() {
		
		super();
	}
	// Declaration of all variables
	private static Logger logger = Logger.getLogger(UINStatusCheck.class);
	protected String testCaseName = "";
	private final String moduleName = "kernel";
	private final String apiName = "UINStatusCheck";
	private SoftAssert softAssert=new SoftAssert();
	public static JSONArray arr = new JSONArray();
	private ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	public CommonLibrary lib=new CommonLibrary();
	private final Map<String, String> props = lib.readProperty("Kernel");
	private final String uingenerator =props.get("uingenerator");
	private KernelAuthentication auth=new KernelAuthentication();
	boolean setFinalStatus=false;
	public KernelDataBaseAccess dbConnection=new KernelDataBaseAccess();
		
	// Getting test case names and also auth cookie based on roles
	@BeforeMethod(alwaysRun=true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();
		if(!lib.isValidToken(regProcCookie))
			regProcCookie=auth.getAuthForRegistrationProcessor();
	} 
	
	// Data Providers to read the input json files from the folders
	@DataProvider(name = "UINStatusCheck")
	public Object[][] readData1(ITestContext context) throws Exception {
			return new TestCaseReader().readTestCases(moduleName + "/" + apiName, testLevel);
	}
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * checkUINStatusCheck
	 * Given input Json as per defined folders When GET request is sent to /uingenerator/v1.0/uin send
	 * Then Response is expected as 200 and other responses as per inputs passed in the request
	 */
	
	@Test(dataProvider="UINStatusCheck",invocationCount=1)
	public void checkUINStatusCheck(String testcaseName) throws FileNotFoundException, IOException, ParseException
    {
		//Getting all UIN from Database whose status is UNUSED
		String query="select u.uin from kernel.uin u where u.uin_status='UNUSED'";
		List<String>list=dbConnection.getDbData( query,"kernel");

		// Calling the GET method with no parameters 
		Response res=applicationLibrary.getWithoutParams(uingenerator,regProcCookie);
		
		//This method is for checking the authentication is pass or fail in rest services
		new CommonLibrary().responseAuthValidation(res);
		
		//Getting the UIN from response
		String uin_number = res.jsonPath().getMap("response").get("uin").toString();
		
		//Getting the status of the UIN 
		String status_query="select uin_status from kernel.uin where uin='"+uin_number+"'";
		List<String> status_list = dbConnection.getDbData( status_query,"kernel");
		String status=status_list.get(0);
		
		//Checking the UIN's status is Unused before calling the get method and and Issued after calling the Get method
		for(String uin:list)
		{
			if(uin.equals(uin_number))
			{
				if(status.equals("ISSUED"))
				{
					setFinalStatus=true;
				}
				else {
					setFinalStatus=false;
					logger.info("UIN status is not in Issued status");
				}
				break;
			}else {
				setFinalStatus=false;
			}
		}
		
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
			f.set(baseTestMethod, testCaseName);	
			} catch (Exception e) {
				Reporter.log("Exception : " + e.getMessage());
			}
		}  
}
