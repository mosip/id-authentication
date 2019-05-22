/*package io.mosip.kernel.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
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

import io.mosip.Runner;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.qrcode.generator.zxing.QrcodeGeneratorImpl;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.util.TestCaseReader;

@SpringBootTest(classes = Runner.class)
public class QRCodeGenerator extends AbstractTestNGSpringContextTests implements ITest {

	public QRCodeGenerator() {
		super();
	}

	private static Logger logger = Logger.getLogger(QRCodeGenerator.class);
	private static final String jiraID = " ";
	private static final String moduleName = "kernel";
	private static final String apiName = "QRCodeGenerator";
	private static final String requestJsonName = "QRCodeGeneratorRequest";
	private static final String outputJsonName = "QRCodeGeneratorOutput";
	protected static String testCaseName = "";
	static SoftAssert softAssert = new SoftAssert();
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();

	@Autowired
	private QrcodeGeneratorImpl qrgenerator;

	*//**
	 * method to set the test case name to the report
	 * 
	 * @param method
	 * @param testdata
	 * @param ctx
	 *//*
	@BeforeMethod
	public static void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) throws Exception {
		String object = (String) testdata[0];
		testCaseName = object.toString();

	}

	*//**
	 * This data provider will return a test case name
	 * 
	 * @param context
	 * @return test case name as object
	 *//*
	@DataProvider(name = "fetchData")
	public Object[][] readData(ITestContext context)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		switch (testLevel) {
		case "smoke":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smoke");

		case "regression":
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "regression");
		default:
			return TestCaseReader.readTestCases(moduleName + "/" + apiName, "smokeAndRegression");
		}

	}

	*//**
	 * This fetch the value of the data provider and run for each test case
	 * 
	 * @param fileName
	 * @param object
	 * 
	 *//*
	@SuppressWarnings("unchecked")
	@Test(dataProvider = "fetchData", alwaysRun = true)
	public void generateQRCode(String testcaseName, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {

		logger.info("Test Case Name:" + testcaseName);
		object.put("Test case Name", testcaseName);
		object.put("Jira ID", jiraID);
		String fieldNameArray[] = testcaseName.split("_");
		String fieldName = fieldNameArray[1];

		JSONObject requestJson = new TestCaseReader().readRequestJson(moduleName, apiName, requestJsonName);

		for (Object key : requestJson.keySet()) {
			if (fieldName.equals(key.toString()))
				object.put(key.toString(), "invalid");
			else
				object.put(key.toString(), "valid");
		}

		String configPath = "src/test/resources/" + moduleName + "/" + apiName + "/" + testcaseName;

		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();
		JSONObject objectData = null;
		String data = null;
		QrVersion qrVersion = null;
		for (int k = 0; k < listofFiles.length; k++) {

			if (listofFiles[k].getName().toLowerCase().contains("photo")) {
				byte[] image = null;
				try {
					image = FileUtils.readFileToByteArray(new File("src/test/resources/" + moduleName + "/" + apiName
							+ "/" + testcaseName + "/" + "photo.jpg"));
				} catch (io.mosip.kernel.core.exception.IOException e) {
					logger.info("Exception occured in loading photo \n" + e.getMessage());
				}
				String imgStr = CryptoUtil.encodeBase64(image);
				data = "photo: " + imgStr;
			}
		else if (listofFiles[k].getName().toLowerCase().contains("request")) {
				objectData = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
				
				String tempData = (objectData.get("data") != null) ? objectData.get("data").toString() : null;
				qrVersion = (objectData.get("QrVersion") != null)
						? QrVersion.valueOf(objectData.get("QrVersion").toString())
						: null;
						data =(data!=null) ? data+tempData: tempData;
			} else if (listofFiles[k].getName().toLowerCase().contains("response")) {
				objectData = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));
			}
		}

		logger.info("request data :\n 'data': " + data + "\n 'qrVersion': " + qrVersion);

		try {
			byte[] result = qrgenerator.generateQrCode(data, qrVersion);
			finalStatus = (result.length > 0) ? "Pass" : "Fail";

			String filePath = "src/test/resources/" + moduleName + "/" + apiName + "/" + testcaseName + "/"
					+ "QRCode.png";
			FileUtils.writeByteArrayToFile(new File(filePath), result);
		} catch (Exception e) {
			finalStatus = (e.getMessage().contains(objectData.get("errorCode").toString())) ? "Pass" : "Fail";
		}

		object.put("status", finalStatus);
		arr.add(object);
		boolean setFinalStatus = false;
		if (finalStatus.equals("Fail")) {
			setFinalStatus = false;
		} else if (finalStatus.equals("Pass"))
			setFinalStatus = true;

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

	*//**
	 * this method write the output to corressponding json
	 *//*
	@AfterClass
	public void updateOutput() throws IOException {
		String configPath = "src/test/resources/" + moduleName + "/" + apiName + "/" + outputJsonName + ".json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to " + outputJsonName + ".json file.......................!!");
		}
	}
}
*/