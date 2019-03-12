package io.mosip.authentication.fw.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;    
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;
import com.ibm.icu.text.Transliterator;

import io.mosip.authentication.fw.client.RestClient;
import io.mosip.authentication.fw.dbUtil.DbConnection;
import io.mosip.authentication.fw.dto.OutputValidationDto;
import io.mosip.authentication.fw.precon.JsonPrecondtion;
import io.mosip.authentication.testdata.TestDataDto;
import io.restassured.response.Response;
 
/**
 * Class to hold dependency method for ida tests automation
 * 
 * @author Vignesh
 *
 */
public class IdaScriptsUtil {
	
	private static EncrypDecrptUtils objEncrypDecrptUtils = new EncrypDecrptUtils();
	private static JsonPrecondtion objJsonPrecondtion = new JsonPrecondtion();
	public static RestClient objRestClient = new RestClient();
	private static OutputValidationUtil objOutputValidationUtil = new OutputValidationUtil();
	private ReportUtil objReportUtil = new ReportUtil();
	private static Logger logger = Logger.getLogger(IdaScriptsUtil.class);
	private static DbConnection objDbConnection = new DbConnection();
	private static String testCaseName;
	private static int testCaseId;
	private static File testFolder;
	

	public static File getTestFolder() {
		return testFolder;
	}

	public static void setTestFolder(File testFolder) {
		IdaScriptsUtil.testFolder = testFolder;
	}

	public static String getTestCaseName() {
		return testCaseName;
	}

	public static void setTestCaseName(String testCaseName) {
		IdaScriptsUtil.testCaseName = testCaseName;
	}

	public static int getTestCaseId() {
		return testCaseId;
	}

	public static void setTestCaseId(int testCaseId) {
		IdaScriptsUtil.testCaseId = testCaseId;
	}

	protected boolean postAndGenOutFile(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath);
					else
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath, code);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ objReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	protected String postAndGenOutFileAndReturnResponse(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath);
					else
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath, code);
					Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ objReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
					return responseJson.toString();
				}
			}
			return "NoResponse";
		} catch (Exception e) {
			logger.error("Exception " + e);
			return e.getMessage();
		}
	}
	
	protected boolean postAndGenOutFileForUinGen(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath);
					else
						responseJson = postRequest(listOfFiles[j].getAbsolutePath(), urlPath, code);
					if (responseJson.contains("Invalid UIN")) {
						fos.flush();
						fos.close();
						return false;
					} else {
						Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
								+ objReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
						fos.write(responseJson.getBytes());
						fos.flush();
						fos.close();
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	protected boolean postAndGenOutFileForUinUpdate(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					FileOutputStream fos = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = patchRequest(listOfFiles[j].getAbsolutePath(), urlPath);
					else
						responseJson = patchRequest(listOfFiles[j].getAbsolutePath(), urlPath, code);
					Reporter.log("<b><u>Actual Patch Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ objReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					fos.write(responseJson.getBytes());
					fos.flush();
					fos.close();
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("Exception " + e);
			return false;
		}
	}
	
	protected String postRequest(String filename, String url) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return objRestClient.postRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	protected String patchRequest(String filename, String url) {
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			return objRestClient.patchRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	protected String patchRequest(String filename, String url, int expCode) {
		Response response=null;
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			response = objRestClient.patchRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<String, List<OutputValidationDto>>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			OutputValidationDto objOpDto = new OutputValidationDto();
			if (response.statusCode() == expCode) {
				objOpDto.setFiedlHierarchy("STATUS CODE");
				objOpDto.setFieldName("STATUS CODE");
				objOpDto.setActualValue(String.valueOf(response.statusCode()));
				objOpDto.setExpValue(String.valueOf(expCode));
				objOpDto.setStatus("PASS");
			} else {
				objOpDto.setFiedlHierarchy("STATUS CODE");
				objOpDto.setFieldName("STATUS CODE");
				objOpDto.setActualValue(String.valueOf(response.statusCode()));
				objOpDto.setExpValue(String.valueOf(expCode));
				objOpDto.setStatus("FAIL");
			}
			objList.add(objOpDto);
			objMap.put("Status Code", objList);
			Reporter.log(objReportUtil.getOutputValiReport(objMap));
			Verify.verify(objOutputValidationUtil.publishOutputResult(objMap));
			return response.asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return response.asString();
		}
	}
	
	protected String postRequest(String filename, String url, int expCode) {
		Response response=null;
		try {
			JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(filename));
			response = objRestClient.postRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<String, List<OutputValidationDto>>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			OutputValidationDto objOpDto = new OutputValidationDto();
			if (response.statusCode() == expCode) {
				objOpDto.setFiedlHierarchy("STATUS CODE");
				objOpDto.setFieldName("STATUS CODE");
				objOpDto.setActualValue(String.valueOf(response.statusCode()));
				objOpDto.setExpValue(String.valueOf(expCode));
				objOpDto.setStatus("PASS");
			} else {
				objOpDto.setFiedlHierarchy("STATUS CODE");
				objOpDto.setFieldName("STATUS CODE");
				objOpDto.setActualValue(String.valueOf(response.statusCode()));
				objOpDto.setExpValue(String.valueOf(expCode));
				objOpDto.setStatus("FAIL");
			}
			objList.add(objOpDto);
			objMap.put("Status Code", objList);
			Reporter.log(objReportUtil.getOutputValiReport(objMap));
			Verify.verify(objOutputValidationUtil.publishOutputResult(objMap));
			return response.asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return response.asString();
		}
	}
	
	protected String getResponse(String url,String type) {
		try {
			return objRestClient.getRequest(url, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON,type).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	protected String getResponse(String url) {
		try {
			return objRestClient.getRequest(url, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			logger.error("Exception: " + e);
			return e.toString();
		}
	}
	
	protected String getEnodedData(File[] listOfFiles,String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return objEncrypDecrptUtils.getEncode(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}
	
	protected String getDecodedData(File[] listOfFiles,String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return objEncrypDecrptUtils.getEncode(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}
	
	protected String getDecodedData(String content) {
		return objEncrypDecrptUtils.getDecodeFromStr(content);
	}
	
	protected void displayContentInFile(File[] listOfFiles, String keywordToFind) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					String responseJson = getContentFromFile(listOfFiles[j]);
					Reporter.log("<pre>" + objReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
				}
			}
		} catch (Exception e) {
			logger.info("Exception : " + e);
		}
	}
	
	protected String getContentFromFile(File[] listOfFiles, String keywordToFind) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					return getContentFromFile(listOfFiles[j]);
				}
			}
		} catch (Exception e) {
			logger.info("Exception : " + e);
			return e.getMessage();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public String getContentFromFile(File file) throws IOException {
		return FileUtils.readFileToString(file.getAbsoluteFile());
	}
	
	protected boolean modifyRequest(File[] listOfFiles, Map<String, String> fieldvalue, String propFileName,
			String keywordinFile) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordinFile))
					objJsonPrecondtion.parseAndwriteJsonFile(listOfFiles[j].getAbsolutePath(), fieldvalue,
							listOfFiles[j].getAbsolutePath(), propFileName);
			}
			return true;
		} catch (Exception e) {
			logger.error("Exception occured:" + e.getMessage());
			return false;
		}
	}
	
	protected Map<String, String> getEncryptKeyvalue(File[] listOfFiles,String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return objEncrypDecrptUtils.getEncryptSessionKeyValue(listOfFiles[j].getAbsolutePath());
			}
		}
	return null;
	}
	
	protected void displayLog(File testCaseName, int testCaseNumber) {
		logger.info(
				"**************************************************************************************************************************************");
		logger.info("*          Test Case Id: " + testCaseNumber);
		logger.info("*          Test Case Name: " + testCaseName.getName());
		logger.info(
				"**************************************************************************************************************************************");
	}
	
	protected String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}
	
	private Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/ida/Testdata/RunConfig/envRunConfig.properties");
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	
	public String getOtpValue(String inputFilePath, String mappingFileName, String otpMappingFieldName) {
		String value = objJsonPrecondtion.getValueFromJson(inputFilePath, mappingFileName, otpMappingFieldName);
		if (value.contains(":")) {
			String[] otpKeyword = value.split(":");
			String otpQuery = otpKeyword[0];
			String waitTime = otpKeyword[1];
			wait(Integer.parseInt(waitTime) * 1000);
			return objDbConnection.getDataForQuery(otpQuery, "KERNEL").get("otp");
		}
		else
			return value;
	}
	
	public String getValueFromJson(File[] listOfFiles, String mappingFileName, String mappingFieldName,
			String keywordinFile) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordinFile)) {
				return objJsonPrecondtion.getValueFromJson(listOfFiles[j].getAbsolutePath(), mappingFileName,
						mappingFieldName);
			}
		}
		return "No Value Found From Json, Check mapping field or file name and input json file";
	}
    
	public void wait(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			logger.info("Exception :" + e);
		}
	}
	
	public String languageConverter(String inputString,String langSourceCode, String langDestCode) {
		final String language_translation_code = langSourceCode+"-"+langDestCode;
        Transliterator input = Transliterator.getInstance(language_translation_code);
        String transliteratedString = input.transliterate(inputString);
        return transliteratedString;      
	} 
	
	/**
	 * Create generatd uin number and its test case name in property file
	 * 
	 * @param filePath
	 */
	public void generateMappingDic(String filePath,Map<String,String> map) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			for (Entry<String, String> entry : map.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, null);
			//prop.
		} catch (Exception e) {
			logger.error("Excpetion in storing the data in propertyFile" + e.getMessage());
		}
	}
	
	public void updateMappingDic(String filePath, Map<String, String> map) {
		try {
			FileInputStream in = new FileInputStream(filePath);
			Properties props = new Properties();
			props.load(in);
			in.close();
			FileOutputStream out = new FileOutputStream(filePath);
			for (Entry<String, String> entry : map.entrySet()) {
				props.setProperty(entry.getKey(), entry.getValue());
			}
			props.store(out, null);
			out.close();
		} catch (Exception e) {
			logger.error("Exception in updating the property file" + e.getMessage());
		}
	}
	
	public String getValueFromPropertyFile(String filepath, String key) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filepath);
			prop.load(input);
			return prop.getProperty(key).toString();
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return e.getMessage();
		}
	}
	
	public Properties getProperty(String filepath) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filepath);
			prop.load(input);
			return prop;
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return prop;
		}
	}
	
	public Map<String,String> getPropertyAsMap(String filepath) {
		Properties prop = new Properties();
		InputStream input = null;
		Map<String,String> map = new HashMap<String,String>();
		try {
			input = new FileInputStream(filepath);
			prop.load(input);
			for(String key : prop.stringPropertyNames())
			{
				map.put(key, prop.getProperty(key));
			}
			return map;
		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage());
			return map;
		}
	}
	
	/**
	 * After the entire test suite clean up rest assured
	 */
	@AfterSuite(alwaysRun = true)
	public void testTearDown(ITestContext ctx) {
		/*
		 * Saving TestNG reports to be published
		 */
		String currentModule = ctx.getCurrentXmlTest().getClasses().get(0).getName().split("\\.")[2];
		objReportUtil.moveReport(currentModule);
		exitDemoAppRunner();
	}
	
	@BeforeSuite(alwaysRun = true)
	public void wakeDemoApp() {
		createBatFileForDemoApp();
		batDemoAppRunner();
	}
	
	private static File demoAppBatchFilePath;
	public void createBatFileForDemoApp() {
		try {
			String javaHome = System.getenv("JAVA_HOME");
			String jarPath="C:/Users/"+System.getProperty("user.name")+"/.m2/repository/io/mosip/Testing/demoApp/testing-demoApp.jar";
			String demoAppJarPath = jarPath;
			String content = '"' + javaHome + "/bin/java" + '"'
					+ " -Dspring.profiles.active=local -Djava.net.useSystemProxies=true -agentlib:jdwp=transport=dt_socket,server=y,address=4000,suspend=n -jar "
					+ '"' + demoAppJarPath.toString() + '"';
			demoAppBatchFilePath = new File(System.getProperty("user.dir") + "/src/test/resources/demoApp.bat");
			FileOutputStream fos = new FileOutputStream(demoAppBatchFilePath);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeBytes(content);
			dos.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			logger.error("Exception in creating the bat file for demoApp application " + e.getMessage());
		}
	}
	
	
	public void batDemoAppRunner() {
		try {
			Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "cmd.exe", "/K", demoAppBatchFilePath.getAbsolutePath() });
			Thread.sleep(20000);
		} catch (Exception e) {
			logger.error("Execption in launching demoApp application" + e.getMessage());
		}
	}
	
	public void exitDemoAppRunner() {
		try {
			Runtime.getRuntime()
					.exec(new String[] { "cmd", "/c", "start", "cmd.exe", "/K", "taskkill /f /im conhost.exe" });
			Runtime.getRuntime()
					.exec(new String[] { "cmd", "/c", "start", "cmd.exe", "/K", "taskkill /f /im java.exe" });
		} catch (Exception e) {
			logger.error("Execption in quitting demoApp application" + e.getMessage());
		}
	}

} 
