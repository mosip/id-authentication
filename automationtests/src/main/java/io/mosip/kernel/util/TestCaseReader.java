package io.mosip.kernel.util;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
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
	
	CommonLibrary commonLib = new CommonLibrary();
	public Object[][] readTestCases(String folderName, String testType){

		List<String> listOfFolders = commonLib.getFoldersFilesNameList(folderName, true);
		
		ArrayList<String> testCaseNames = new ArrayList<>();
		
		for (int j = 0; j < listOfFolders.size(); j++) {
			String[] arr = listOfFolders.get(j).split("/");
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

		Object[][] testParam = new Object[testCaseNames.size()][];
		int k = 0;
		for (String testcaseName : testCaseNames) {
			
			testParam[k] = new Object[] {testcaseName};
			k++;
		}
		return testParam;

	}


	/**
	 * @param modulename
	 * @param apiname
	 * @param testcaseName
	 * @return this method is for reading request and response object form the given testcase folder and returns in array.
	 */
	public JSONObject[] readRequestResponseJson(String modulename, String apiname, String testcaseName){
		String configPath = modulename + "/" + apiname + "/" + testcaseName;
		List<String> listofFiles =  commonLib.getFoldersFilesNameList(configPath, false);
		JSONObject[] objectData = new JSONObject[2];
		for (int k = 0; k < listofFiles.size(); k++) {
			String[] arr = listofFiles.get(k).split("/");
				if (arr[arr.length - 1].toLowerCase().contains("request")) 
					objectData[0] =  commonLib.readJsonData(configPath+"/"+arr[arr.length - 1]);
					
				 else if (arr[arr.length - 1].toLowerCase().contains("response")) 
					objectData[1] =  commonLib.readJsonData(configPath+"/"+arr[arr.length - 1]);
		 
			
		}
		return objectData;
	}
	
	
}
