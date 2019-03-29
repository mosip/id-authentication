package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mosip.service.BaseTestCase;

/**
 * @author Arjun chandramohan
 *
 */
public class TestCaseReader extends BaseTestCase {
	/**
	 * this method return test case name
	 * 
	 * @param folderName
	 * @param jsonFileName
	 * @param fieldFile
	 * @param testType
	 * @return return the folder(test case name)
	 */
	public static Object[][] readTestCases(String folderName, String testType) throws IOException, ParseException {

		String configPath1 = "src/test/resources/" + folderName + "/";
		File folder1 = new File(configPath1);
		File[] listOfFolders = folder1.listFiles();

		ArrayList<String> testCaseName = new ArrayList<>();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				String[] arr = listOfFolders[j].toString().split(BaseTestCase.SEPRATOR);
				switch (testType) {
				case "smoke":
					if (arr[arr.length - 1].toString().contains("smoke"))
						testCaseName.add(arr[arr.length - 1].toString());
					break;
				case "regression":
					if (arr[arr.length - 1].toString().contains("invalid"))
						testCaseName.add(arr[arr.length - 1].toString());
					break;

				default:
					testCaseName.add(arr[arr.length - 1].toString());
					break;
				}

			}
		}

		Object[][] testParam = new Object[testCaseName.size()][];
		int k = 0;
		for (String input : testCaseName) {
			testParam[k] = new Object[] { input, new JSONObject() };
			k++;
		}
		return testParam;

	}

	/**
	 * this method accepts the request json name and return the json as JSONObject
	 * 
	 * @param modulename
	 * @param apiname
	 * @param requestjsonname
	 * @return
	 */
	public JSONObject readRequestJson(String modulename, String apiname, String requestjsonname) {

		String configPath = "src/test/resources/" + modulename + "/" + apiname + "/" + requestjsonname + ".json";
		File requestJson = new File(configPath);
		JSONObject inputRequest = null;
		try {
			inputRequest = (JSONObject) new JSONParser().parse(new FileReader(requestJson));
		} catch (IOException | ParseException e) {
		}
		return inputRequest;
	}

	/**
	 * this method return the testcase condition for the test data generation
	 * 
	 * @param modulename
	 * @param apiname
	 * @param testcaseName
	 * @param requestjsonname
	 * @return
	 */
	public JSONObject readRequestJsonCondition(String modulename, String apiname, String testcaseName,
			String requestjsonname) {
		String configPath = "src/test/resources/" + modulename + "/" + apiname + "/" + testcaseName + "/"
				+ requestjsonname + ".json";

		File requestJson = new File(configPath);
		JSONObject inputRequest = null;
		try {
			inputRequest = (JSONObject) new JSONParser().parse(new FileReader(requestJson));
		} catch (IOException | ParseException e) {
		}
		return inputRequest;
	}
}
