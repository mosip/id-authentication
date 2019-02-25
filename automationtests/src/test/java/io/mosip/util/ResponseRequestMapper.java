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
	
	public static JSONArray mapArrayRequest(String testSuite, JSONObject object)
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
							JSONArray objectData = (JSONArray) new JSONParser().parse(new FileReader(f.getPath()));
							return objectData;
						}
				 	}
				}
			}
		}
		return null; 
	}
	
	public static JSONArray mapArrayResponse(String testSuite,JSONObject object) throws FileNotFoundException, IOException, ParseException {

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
							JSONArray objectData = (JSONArray) new JSONParser().parse(new FileReader(f.getPath()));
							return objectData;
						}
					}
				}
			}
		}
		return null;
	}
}
