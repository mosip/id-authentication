package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mosip.registrationProcessor.util.RegProcApiRequests;

public class ReadFolder {
	private static Logger logger = Logger.getLogger(ReadFolder.class);

	public static Object[][] readFolders(String folderName, String jsonFileName,String fieldFile,String testType) throws IOException, ParseException {
		logger.info(folderName);
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
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		String configPath=apiRequests.getResourcePath() + folderName+"/"+jsonFileName;

		File f = new File(configPath);
		
		FileReader fr = new FileReader(f); 
		JSONArray objec = (JSONArray) new JSONParser().parse(fr);
		Object[][] reutr = new Object[objec.size()][];
		int i = 0;
		for (Object input : reutr) {
			
			reutr[i] = new Object[] { folderName, i, objec.get(i) };
			i++;
		}
		fr.close();
		return reutr;
	}
}
