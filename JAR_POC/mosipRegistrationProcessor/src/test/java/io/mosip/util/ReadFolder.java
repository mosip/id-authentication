package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadFolder {

	public static Object[][] readFolders(String folderName, String jsonFileName,String fieldFile,String testType) throws IOException, ParseException {
		System.out.println(folderName);
//		String cpyFolderName="";
//		 for(int i=0;i<folderName.length();i++){
//		    	if(folderName.charAt(i)=='\\')
//		    		cpyFolderName+='/';
//		    	else
//		    		cpyFolderName+=folderName.charAt(i);
//		    }
//		 folderName=cpyFolderName;
		CommonLibrary.scenarioFileCreator(fieldFile,folderName,testType,jsonFileName);
		File Mainfolder = new File(folderName);
		File[] listOfMainFolders = Mainfolder.listFiles();

		String configPath="src/test/resources/" + folderName+"/"+jsonFileName;

		File f = new File(configPath);
		
		FileReader fr = new FileReader(f); 
		JSONArray objec = (JSONArray) new JSONParser().parse(fr);
		Object[][] reutr = new Object[objec.size()][];
		int i = 0;
		for (Object input : reutr) {
			
			reutr[i] = new Object[] { folderName, i, objec.get(i) };
			i++;
		}
		return reutr;
	}
}
