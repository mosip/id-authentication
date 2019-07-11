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

	
/*	*//**
	 * @param path
	 * @return this method is for reading the jsonData object from the given path.
	 *//*
	public JSONObject readJsonData(String path) {

		
		JSONObject jsonData = null;
		try {
			InputStream is = TestCaseReader.class.getResourceAsStream("/" + path);
			jsonData = (JSONObject) new JSONParser().parse(new InputStreamReader(is, "UTF-8"));
		} catch (IOException | ParseException e) {
			logger.info(e.getMessage());
		}
		return jsonData;
	}*/

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
	
/*	*//**
	 * @param folderRelativePath
	 * @param isfolder(it should be true if u want to get list of folders and false for list of files)
	 * @return this method is for returning the list of relative path of each folder or files in a given path
	 *//*
	public List<String> getFoldersFilesNameList(String folderRelativePath, boolean isfolder){
		String configPath = folderRelativePath;
		List<String> listFoldersFiles = new ArrayList<>();

		final File jarFile = new File(
				TestNgApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) { // Run with JAR file
			JarFile jar = null;
			try {
				jar = new JarFile(jarFile);
			} catch (IOException e) {
				logger.info(e.getMessage());
			}
			
			final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			while (entries.hasMoreElements()) {
				JarEntry je = entries.nextElement();
				if (je.isDirectory()==isfolder) {
					final String name = je.getName();
					if (name.startsWith(configPath + "/")) { // filter according to the path
						listFoldersFiles.add(name);
					}
				}
			}
			try {
				jar.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else { // Run with IDE
			final URL url = TestCaseReader.class.getResource("/" + configPath);
			if (url != null) {
				try {
					final File file = new File(url.toURI());
					for (File f : file.listFiles()) {
						if (f.isDirectory()==isfolder)
						listFoldersFiles.add(configPath + "/" + f.getName());
					}
				} catch (URISyntaxException | NullPointerException e) {
					logger.info(e.getMessage());
				}
			}
		//}
		return listFoldersFiles;
	}*/
	
}
