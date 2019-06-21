package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ResponseRequestMapper {
	private static Logger logger = Logger.getLogger(ResponseRequestMapper.class);

	public static JSONObject mapRequest(String testSuite, JSONObject object)
			throws FileNotFoundException, IOException, ParseException {

		String configPath = "src/test/resources/" + testSuite + "/";

		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				if (listOfFolders[j].getName().equals(object.get("testCaseName").toString())) {
					logger.info("Testcase name is" + listOfFolders[j].getName());
					File[] listOfFiles = listOfFolders[j].listFiles();
					for (File f : listOfFiles) {
						if (f.getName().toLowerCase().contains("request")) {
							JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
							return objectData;
						}
					}
				}
			}
		}
		return null; 
	}
	public static JSONObject mapResponse(String testSuite,JSONObject object) throws FileNotFoundException, IOException, ParseException {

		String configPath = "src/test/resources/" + testSuite + "/";

		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				if (listOfFolders[j].getName().equals(object.get("testCaseName").toString())) {
					logger.info("Testcase name is" + listOfFolders[j].getName());
					File[] listOfFiles = listOfFolders[j].listFiles();
					for (File f : listOfFiles) {
						if (f.getName().toLowerCase().contains("response")) {
							JSONObject objectData = (JSONObject) new JSONParser().parse(new FileReader(f.getPath()));
							return objectData;
						}
					}
				}
			}
		}
		return null;
	}
	public static File getPacket(String testSuite,JSONObject object) {

		String configPath = "src/test/resources/" + testSuite + "/";

		File folder = new File(configPath);
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory()) {
				if (listOfFolders[j].getName().equals(object.get("testCaseName").toString()) && object.get("testCaseName").toString().contains("smoke")) {
					logger.info("Testcase name is" + listOfFolders[j].getName());
					File[] listOfFiles = listOfFolders[j].listFiles();
					for (File f : listOfFiles) {
						if(f.getName().contains(".zip")) {
							return f;
						}
					}
				}
			}
		}
		return null;
	}
	public static File mapCreateRequest(String testSuite) {
		File requestFile = null;
		// testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		/**
		 * Reading request body from configpath
		 */
		requestFile = getRequest(testSuite);
		//createRequest.put("requesttime", getCurrentDate());
		return requestFile;
	}

	public static File getRequest(String testSuite) {
		JSONObject request = null;
		/**
		 * Reading request body from configpath
		 */
		String configPath = System.getProperty("user.dir") + "/src/test/resources/" + testSuite;
		File folders = new File(configPath);
		File[] listOfFiles = folders.listFiles();
		FileReader fileReader = null;
		for (File f : listOfFiles) {
			if (f.getName().contains(".zip")) {

				/*try {
					fileReader = new FileReader(f.getPath());
					request = (JSONObject) new JSONParser().parse(fileReader);
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					try {
						fileReader.close();
					} catch (IOException e) {
						logger.info(e.getMessage());
					}
				}

			}*/
				return f;
			}
		}
		return null;
	}
}
