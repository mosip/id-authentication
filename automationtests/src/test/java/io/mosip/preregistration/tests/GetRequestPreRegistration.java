package io.mosip.preregistration.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.dbHealthcheck.DBHealthCheck;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.AssertPreReg;
import io.mosip.service.BaseTestCase;
import io.mosip.util.CommonLibrary;
import io.mosip.util.GetHeader;
import io.mosip.util.ReadFolder;
import io.restassured.response.Response;

public class GetRequestPreRegistration extends BaseTestCase {
	private static Logger logger = Logger.getLogger(GetRequestPreRegistration.class);
	boolean status = false;
	String finalStatus = "";
	public static JSONArray arr = new JSONArray();
	ObjectMapper mapper = new ObjectMapper();
	static Response response = null;
	static JSONObject responseObject = null;
	private static AssertPreReg preRegAssertions = new AssertPreReg();
	private static ApplicationLibrary applicationLibrary = new ApplicationLibrary();
	private static final String preReg_URI = "/int-demographic/v0.1/pre-registration/applicationData";
	static String dest="";
	static String configPaths=""; 
	@DataProvider(name = "createPreReg")
	public Object[][] readData(ITestContext context) throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<String> outerKeys=new ArrayList<String>();
		List<String> innerKeys=new ArrayList<String>();
		 String testParam = context.getCurrentXmlTest().getParameter("testType");
		 switch ("smoke") {
		case "smoke":
			return ReadFolder.readFolders("preReg/GetRequest", "Get_PreRegistrationOutput.json","Get_PreRegistrationRequest.json","smoke");
			
		case "regression":	
			return ReadFolder.readFolders("preReg/GetRequest", "Get_PreRegistrationOutput.json","Get_PreRegistrationRequest.json","regression");
		default:
			return ReadFolder.readFolders("preReg/GetRequest", "Get_PreRegistrationOutput.json","Get_PreRegistrationRequest.json","smokeAndRegression");
		}
		
	}

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "createPreReg")
	public void generate_Response(String fileName, Integer i, JSONObject object)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		List<String> outerKeys=new ArrayList<String>();
		List<String> innerKeys=new ArrayList<String>();
		dest=fileName;
		String filepath=System.getProperty("user.dir") + "/src/test/resources/"+fileName+"/Get_PreRegistrationRequest.json";
		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(filepath));
	/*	String keys = "";
		for(Object obj: requestKeys.keySet()) {
			keys += obj.toString()+ ",";
		}
		keys = keys.substring(0, keys.length() - 1);
		if (object.get("testType").toString().equals("smoke"))
			CommonLibrary.configFileWriter(keys, fileName, object.toJSONString(), 1, "positive");
		else
			CommonLibrary.configFileWriter(keys, fileName, object.toJSONString(), 2, "negative");*/
		/**
		 * Data Utility
		 */
		//new Main().ApiRunner();
	configPaths =  "src/test/resources/" + fileName + "/";
		File folder = new File(configPaths);
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				if(listOfFolders[j].getName().equals(object.get("testCaseName").toString())) {
					logger.info("name of test Case------------------------>"+listOfFolders[j].getName());
				File[] listofFiles = listOfFolders[j].listFiles();
				for (int k = 0; k < listofFiles.length; k++) {

					if (listofFiles[k].getName().toLowerCase().contains("request")) {
						JSONObject objectData = (JSONObject) new JSONParser()
								.parse(new FileReader(listofFiles[k].getPath()));

						logger.info(objectData.toJSONString());
						GetHeader.getHeader(objectData);
						System.out.println(GetHeader.getHeader(objectData));
						response = applicationLibrary.getRequest(preReg_URI, GetHeader.getHeader(objectData));

					} else if (listofFiles[k].getName().toLowerCase().contains("response")) {
						responseObject = (JSONObject) new JSONParser().parse(new FileReader(listofFiles[k].getPath()));

					}

				}
				outerKeys.add("resTime");
				status = preRegAssertions.assertPreRegistration(response, responseObject,outerKeys,innerKeys);
				if (status) {
					
					
							int statusCode=response.statusCode();
							logger.info("Status Code is : " +statusCode);
							
							if (statusCode==200)
							{
								String preId=(response.jsonPath().get("response[0].prId")).toString();
								logger.info("Hey    ------  Pre Id is : " +preId);
								
								DBHealthCheck.prereg_preIDCheckDB(preId);
							}
							finalStatus ="Pass";
						}
					
					
				 else {
					finalStatus ="Fail";
					break;
				}
			}
			} else {
				continue;
			}
		}
		object.put("status", finalStatus);
		arr.add(object);
	}

	@AfterClass
	public void updateOutput() throws IOException {
		String configPath =  "src/test/resources/preReg/GetRequest/Get_PreRegistrationOutput.json";
		try (FileWriter file = new FileWriter(configPath)) {
			file.write(arr.toString());
			logger.info("Successfully updated Results to Create_PreRegistrationOutput.json file.......................!!");
		}
		//CommonLibrary.backUpFiles(configPaths,dest);
	}
}
