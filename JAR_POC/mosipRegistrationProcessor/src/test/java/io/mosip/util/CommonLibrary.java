
package io.mosip.util;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.restassured.response.Response;

/**
 * 
 * @author M1047227
 *
 */
public class CommonLibrary {

	private static Logger logger = Logger.getLogger(CommonLibrary.class);

	/*public static void configFileWriter(String folderPath,String requestKeyFile,String generationType,String baseFileName)
				throws Exception {
		String splitRegex = Pattern.quote(System.getProperty("file.separator"));
		String string[]=new String[2];
		string=folderPath.split(splitRegex);
		String api=string[0];
		String testSuite=string[1];

		String requestFilePath= "src/test/resources/"+folderPath+"/"+requestKeyFile;
		String configFilePath="src/test/resources/"+folderPath+"/"+"FieldConfig.properties";

		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(requestFilePath));
		Properties properties = new Properties();
		Properties cloneProperties=new Properties();
		properties.load(new FileReader(new File(configFilePath)));
		cloneProperties.load(new FileReader(new File(configFilePath)));
		Set<String> keys=properties.stringPropertyNames();
		
	try { 
			for(Object key: requestKeys.keySet()) { 

				if(properties.getProperty(key.toString())!=null) {
					properties.setProperty(key.toString(), "invalid");
					properties.setProperty("filename", "invalid_"+key.toString());
					File file = new File(configFilePath);
					FileOutputStream fileOut = new FileOutputStream(file);
			 		properties.store(fileOut,"FieldConfig.properties");
			 		try {
						new Main().TestRequestReponseGenerator(api, testSuite,generationType);
						}catch(org.json.JSONException exp) {
							exp.printStackTrace();
						}
					properties.remove(key.toString());
					properties.setProperty(key.toString(), "valid");
					properties.remove("filename");
					properties.setProperty("filename", baseFileName);
					properties.store(fileOut, "FieldConfig.properties");
					fileOut.close();
				}
				
	}
			
	}catch (Exception e) {
		// TODO: handle exception
	}
	cloneProperties.remove("prereg_id_custom");
	cloneProperties.setProperty("prereg_id_custom", "");
	properties.clear();
	File file = new File(configFilePath);
	FileOutputStream fileOut = new FileOutputStream(file);
	properties.store(fileOut, null);
	cloneProperties.store(fileOut, null);
	}*/
	public static void scenarioFileCreator(String fileName,String module,String testType,String ouputFile) throws IOException, ParseException {
		String input = "";
		List<String> scenario = new ArrayList<String>();
		String filepath= "src/test/resources/" + module+"/"+fileName;

		String configPaths = "src/test/resources/" +module;

		File folder = new File(configPaths);
		System.out.println("Config Path is : "+configPaths);
		System.out.println("Folder exists  : "+ folder.exists());
		File[] listOfFolders = folder.listFiles();
		Map<String,String> jiraID= new HashMap<String,String>();
		int id=1000;
		for(int k=0;k<listOfFolders.length;k++) {
			jiraID.put(listOfFolders[k].getName(), "MOS-"+id);
			id++; 
		}
		JSONObject requestKeys= (JSONObject) new JSONParser().parse(new FileReader(filepath));
		if(testType.equals("smoke")) {
		input += "{";
		input += "\"testType\":" + "\"smoke\",";
		for(int k=0;k<listOfFolders.length;k++) {
		if(listOfFolders[k].getName().toLowerCase().contains("smoke")) {
			input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
			input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
			for(Object obj: requestKeys.keySet()) {
				input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
			}
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			input="";
			input += "{";
			input += "\"testType\":" + "\"smoke\",";
		}
		}
	}
		else if(testType.equals("regression")) {
		input = "";
		int[] permutationValidInvalid = new int[requestKeys.size()];
		permutationValidInvalid[0] = 1;
		for (Integer data : permutationValidInvalid) {
			input += data;
		}
		List<String> validInvalid = permutation.pack.Permutation.permutation(input);
		input = "";
		for (String validInv : validInvalid) {
			input += "{";
			input += "\"testType\":" + "\"regression\",";
			int i = 0;
			for(Object obj: requestKeys.keySet()) {
				if (validInv.charAt(i) == '0') {
					input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
				}
				else if (validInv.charAt(i) == '1') {
					input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
					for(int k=0;k<listOfFolders.length;k++) {
						if(listOfFolders[k].getName().toLowerCase().contains(obj.toString().toLowerCase())) {
							input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
							input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
							id++;
							break;
						}
				}
				}
				i++;
			}
			input += "\"status\":" + "\"\"";
			input += "}";
			scenario.add(input);
			input = "";
		}
		}
		else if(testType.toLowerCase().equals("smokeandregression")){
			input += "{";
			input += "\"testType\":" + "\"smoke\",";
			//input += "\"jiraId\":" + "\"MOS-1000\",";
			for(int k=0;k<listOfFolders.length;k++) {
			if(listOfFolders[k].getName().contains("smoke")) {
				input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
				input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
				for(Object obj: requestKeys.keySet()) {
					input += '"' + obj.toString() + '"' + ":" + "\"valid\",";
				}
				input += "\"status\":" + "\"\"";
				input += "}";
				scenario.add(input);
				input="";
				input += "{";
				input += "\"testType\":" + "\"smoke\",";
			}
			}
			input = "";
			int[] permutationValidInvalid = new int[requestKeys.size()];
			permutationValidInvalid[0] = 1;
			for (Integer data : permutationValidInvalid) {
				input += data;
			}
			List<String> validInvalid = permutation.pack.Permutation.permutation(input);
			System.out.println("--------------------------------->"+validInvalid);
			input = "";
			for (String validInv : validInvalid) {
				input += "{";
				input += "\"testType\":" + "\"regression\",";
				int i = 0;
				for(Object obj: requestKeys.keySet()) {
					if (validInv.charAt(i) == '0') {
						input += '"' + obj.toString() + '"' + ":" + "\"valid\"" + ",";
					}
					else if (validInv.charAt(i) == '1') {
						input += '"' + obj.toString() + '"' + ":" + "\"invalid\"" + ",";
						for(int k=0;k<listOfFolders.length;k++) {
							if(listOfFolders[k].getName().toLowerCase().contains(obj.toString().toLowerCase())) {
								input += "\"testCaseName\":" + "\""+listOfFolders[k].getName()+"\""+",";
								input += "\"jiraId\":" + "\""+jiraID.get(listOfFolders[k].getName())+"\""+",";
								id++;
								break;
							}
					}
					}
					i++;
			
				}
				
				input += "\"status\":" + "\"\"";
				input += "}";
				scenario.add(input);
				input = "";
			}
		}
		
		
		
		//System.out.println(scenario);

		String configpath="src/test/resources/" + module+"/"+ouputFile;

		File json = new File(configpath);
		FileWriter fw = new FileWriter(json);
		fw.write(scenario.toString());
		fw.flush();
		fw.close();

	}
	
	
	
    
}


