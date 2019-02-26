package io.mosip.preregistration.tests;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;

import io.mosip.dbaccess.PreRegDbread;

import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertResponses;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.PreRegistrationLibrary;
import io.mosip.util.ReadFolder;
import io.mosip.util.ResponseRequestMapper;
import io.restassured.response.Response;

/**
 * @author Lavanya R
 *
 */

public class DocumentUploadbck extends BaseTestCase implements ITest {
//implement,IInvokedMethodListener
	public DocumentUploadbck() {

	}
	static 	String preId="";
	static SoftAssert softAssert=new SoftAssert();
	protected static String testCaseName = "";
	private static Logger logger = Logger.getLogger(DocumentUploadbck.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response Actualresponse = null;
	static JSONObject Expectedresponse = null;
	
	static String dest = "";
	static String folderPath = "preReg/DocumentUpload";
	static String outputFile = "DocumentUploadOutput.json";
	static String requestKeyFile = "DocumentUploadRequest.json";
	
	

	
	PreRegistrationLibrary preRegLib=new PreRegistrationLibrary();

	@DataProvider(name = "documentUpload")
	public static Object[][] readData1(ITestContext context) throws Exception {
		
		
		String testParam = context.getCurrentXmlTest().getParameter("testType");
		switch (testParam) {
		case "smoke":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smoke");
		case "regression":
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "regression");
		default:
			return ReadFolder.readFolders(folderPath, outputFile, requestKeyFile, "smokeAndRegression");
		}
	}

	@Test(dataProvider = "documentUpload")
	public void documentUpload(String testSuite, Integer i, JSONObject object) throws Exception {
	
		List<String> outerKeys = new ArrayList<String>();
		List<String> innerKeys = new ArrayList<String>();
		
		
		Expectedresponse = ResponseRequestMapper.mapResponse(testSuite, object);
	
		//Creating the Pre-Registration Application
		Response createApplicationResponse = preRegLib.CreatePreReg();
		
		//Fetch PreId for DB CLeanUp step
		preId=(createApplicationResponse.jsonPath().get("response[0].preRegistrationId")).toString();
		
		//Document Upload for created application
		Response docUploadResponse = preRegLib.documentUpload(createApplicationResponse);
		
		
		outerKeys.add("resTime");
		innerKeys.add("preRegistrationId");
		innerKeys.add("documnetId");
		innerKeys.add("documentName");
		
		
		status = AssertResponses.assertResponses(docUploadResponse, Expectedresponse, outerKeys, innerKeys);
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

	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		JSONObject object = (JSONObject) testdata[2];
	
		testCaseName = object.get("testCaseName").toString();
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
			f.set(baseTestMethod, DocumentUploadbck.testCaseName);
		} catch (Exception e) {
			Reporter.log("Exception : " + e.getMessage());
		}
	}

	@AfterClass
	public void statusUpdate() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException {
		String configPath = "src/test/resources/" + folderPath + "/"
				+ outputFile;
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputFile);
		}
		String source = "src/test/resources/" + folderPath + "/";
		//CommonLibrary.backUpFiles(source, folderPath);
		//Add generated PreRegistrationId to list to be Deleted from DB AfterSuite 
				preIds.add(preId);
	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	/*@AfterSuite(alwaysRun=true)
    public void preRegCleanUp()
    {
             
             Calling up PreReg DB clean Up step
             
             boolean status=false;
            status=prereg_dbread.prereg_db_CleanUp(preId);
             if(status)
            	 logger.info("PreId is deleted from the DB");
             else
                    logger.info("PreId is NOT deleted from the DB");
                    
    }*/

}
