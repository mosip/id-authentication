package io.mosip.kernel.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mosip.service.BaseTestCase;

/**
 *
 * @author Ravikant
 *
 */
public class TestCaseReader extends BaseTestCase {
	
	/**
	 * @param folderName
	 * @param testType
	 * @param requestjsonName
	 * @return this method works as data provider and reads the test case folders and returns the respective output object.
	 * @throws IOException
	 * @throws ParseException
	 */
	public Object[][] readTestCases(String folderName, String testType){

		String configPath = "src/test/resources/" + folderName + "/";
		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		ArrayList<String> testCaseNames = new ArrayList<>();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				String[] arr = listOfFolders[j].toString().split(BaseTestCase.SEPRATOR);
				switch (testType) {
				case "smoke":
					if (arr[arr.length - 1].toString().contains("smoke"))
						testCaseNames.add(arr[arr.length - 1].toString());
					break;
				case "regression":
					if (arr[arr.length - 1].toString().contains("invalid"))
						testCaseNames.add(arr[arr.length - 1].toString());
					break;

				default:
					testCaseNames.add(arr[arr.length - 1].toString());
					break;
				}

			}
		}

		Object[][] testParam = new Object[testCaseNames.size()][];
		int k = 0;
		for (String testcaseName : testCaseNames) {
			
			testParam[k] = new Object[] {testcaseName};
			k++;
		}
		return testParam;

	}

	
	/**
	 * @param path
	 * @return this method is for reading the jsonData object from the given path.
	 */
	public JSONObject readJsonData(String path) {

		File requestfile = new File(path);
		JSONObject jsonData = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(requestfile);
			jsonData = (JSONObject) new JSONParser().parse(fileReader);
		} catch (IOException | ParseException e) {
		  
		}
		finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
		}
		return jsonData;
	}

	/**
	 * @param modulename
	 * @param apiname
	 * @param testcaseName
	 * @return this method is for reading request and response object form the given testcase folder and returns in array.
	 */
	public JSONObject[] readRequestResponseJson(String modulename, String apiname, String testcaseName){
		String configPath = "src/test/resources/" + modulename + "/" + apiname + "/" + testcaseName;

		File folder = new File(configPath);
		File[] listofFiles = folder.listFiles();
		JSONObject[] objectData = new JSONObject[2];
		for (int k = 0; k < listofFiles.length; k++) {

				if (listofFiles[k].getName().toLowerCase().contains("request")) 
					objectData[0] = readJsonData(configPath+"/"+listofFiles[k].getName());
					
				 else if (listofFiles[k].getName().toLowerCase().contains("response")) 
					objectData[1] = readJsonData(configPath+"/"+listofFiles[k].getName());
		 
			
		}
		return objectData;
	}
	
}
