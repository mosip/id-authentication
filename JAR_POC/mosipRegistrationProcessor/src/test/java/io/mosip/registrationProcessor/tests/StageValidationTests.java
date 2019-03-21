package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import io.mosip.util.SetStageStatusCode;
import io.mosip.util.StageValidationMethods;

public class StageValidationTests {
	StageValidationMethods scenario = new StageValidationMethods();
	SetStageStatusCode codeList=new SetStageStatusCode();
	static List<String> userList=new ArrayList<String>();
	static List<String> dbList = new ArrayList<String>();
	@Parameters({"testCaseName","fileName"})
	@BeforeTest
	public void readUserStage(String testCaseName,String fileName) {
		Properties prop=new Properties();
		String configPath=System.getProperty("user.dir")+"//src//test//resources//regProc//StageValidation//"+testCaseName+"//"+fileName;
		try {
			prop.load(new FileReader(new File(configPath)));
			StringBuilder stageString= new StringBuilder();
			stageString.append(prop.getProperty("StageBits").toString());
			userList=codeList.getStatusCodesList(stageString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
@Test
public void virusScanUploadTestSmoke() {
	List<String> statusCodes = scenario.getStatusList("ValidPacketSmoke");
	dbList=scenario.getStatusCodeListFromDb(statusCodes);
}
@Test
public void allStagesFailed() {
	List<String> statusCodes=scenario.getStatusList("AllStagesFailed");
	dbList=scenario.getStatusCodeListFromDb(statusCodes);
}
@AfterTest
	public void compareList() {
	Assert.assertTrue(userList.equals(dbList));
	userList.clear();
	dbList.clear();
}
}