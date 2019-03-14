package io.mosip.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.lingala.zip4j.exception.ZipException;

public class TweakRegProcPackets {
	
	EncrypterDecrypter encryptDecrypt=new EncrypterDecrypter();
	public  void tweakFile(String testCaseName,String parameterToBeChanged) throws IOException, ZipException {
		File decryptedFile=null;
		JSONObject metaInfo=null;
		String filesToBeDestroyed=null;
		String configPath=System.getProperty("user.dir")+"/"+"src/test/resources/regProc/Packets/ValidPackets/";
		String invalidPacketsPath=System.getProperty("user.dir")+"/"+"src/test/resources/regProc/Packets/InvalidPackets/"+testCaseName;
		File file=new File(configPath);
		File[] listOfFiles=file.listFiles();
		for(File f: listOfFiles) {
			if(f.getName().contains(".zip")){
			decryptedFile=encryptDecrypt.decryptFile(f,f.getPath());
			filesToBeDestroyed=configPath+"/"+decryptedFile.getName();
			File[] packetFiles=decryptedFile.listFiles();
			for(File info:packetFiles) {
				
				if(info.getName().toLowerCase().equals("packet_meta_info.json")) {
					try {
						metaInfo=(JSONObject) new JSONParser().parse(new FileReader(info.getPath()));
						System.out.println("Inside Info Is  :: "+ metaInfo.toJSONString());
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}	
					System.out.println("Initial Info is :: "+ metaInfo);
					JSONObject identity= (JSONObject) metaInfo.get("identity");
					JSONArray metaData=(JSONArray) identity.get("metaData");
					JSONArray updatedData=tweakParameter(metaData,parameterToBeChanged);
					metaInfo.put("identity", identity);
					try (FileWriter updatedFile = new FileWriter(info.getAbsolutePath())) 
		            {
		                try {
							updatedFile.write(metaInfo.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
		                System.out.println("Successfully updated json object to file...!!");
		               
		            } catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}
			
			}
		}
		}
		encryptDecrypt.encryptFile(decryptedFile,configPath,invalidPacketsPath);
		encryptDecrypt.destroyFiles(filesToBeDestroyed);
	}
	
	public JSONArray tweakParameter(JSONArray metaData, String parameter) {
		switch (parameter) {
		case "registrationId":
				return tweakRegID(metaData);
		case "centerId":
			return tweakCentreID(metaData);
	
		default:
			break;
		}
		return metaData;
		
	}
	public JSONArray tweakRegID(JSONArray metaData) {
		
		for(int i=0;i<metaData.size();i++) {
			JSONObject labels=(JSONObject) metaData.get(i);
			if(labels.get("label").equals("registrationId")) {
				labels.put("value", "12345");
			}
		}
		return metaData;
	}
	public  JSONArray tweakCentreID(JSONArray metaData) {
		for(int i=0;i<metaData.size();i++) {
			JSONObject labels=(JSONObject) metaData.get(i);
			if(labels.get("label").equals("centerId")) {
				labels.put("value", "ABCD");
			}
		}
		return metaData;
	}
	
	public static void main(String[] args) throws IOException, ZipException {
		TweakRegProcPackets e=new TweakRegProcPackets();
		e.tweakFile("InvalidCentreID","centerId");
	}
}
