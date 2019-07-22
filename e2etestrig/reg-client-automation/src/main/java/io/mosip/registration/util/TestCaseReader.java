package io.mosip.registration.util;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class TestCaseReader {
	/**
	 * this method return test case name
	 * 
	 * @param apiName
	 * @param fieldFile
	 * @param testType
	 * @return return the folder(test case name)
	 */
	public Object[][] readTestCases(String apiName, String testType) {
		String path = "src" + File.separator + "main" + File.separator + "resources" + File.separator + apiName;
		File file = new File(path);
		File[] listOfFolders = file.listFiles();

		ArrayList<String> testCaseName = new ArrayList<>();
		ArrayList<String> validTestCase = new ArrayList<>();
		ArrayList<String> invalidTestCase = new ArrayList<>();
		
		String type=System.getProperty("os.name");
		String seperator="";
		if(type.toLowerCase().contains("windows")){
			seperator="\\\\";
		}else if(type.toLowerCase().contains("linux")||type.toLowerCase().contains("unix"))
		{
			seperator="/";
		}
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				String[] arr = listOfFolders[j].toString().split(seperator);
				if (arr[arr.length - 1].toString().contains("smoke"))
					validTestCase.add(arr[arr.length - 1].toString());
				else
					invalidTestCase.add(arr[arr.length - 1].toString());
			}

		}
		if (testType.equalsIgnoreCase("smoke"))
			testCaseName = validTestCase;
		else
			testCaseName = (ArrayList<String>) Stream.of(validTestCase, invalidTestCase).flatMap(x -> x.stream())
					.collect(Collectors.toList());
		Object[][] tcName = new Object[testCaseName.size()][];
		int k = 0;
		for (String input : testCaseName) {
			tcName[k] = new Object[] { input, new JSONObject() };
			k++;
		}
		return tcName;

	}
}
