package io.mosip.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mosip.service.ApplicationLibrary;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class EncrypterDecrypter {
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	private final String decrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/decryptPacket";
	private final String encrypterURL="http://104.211.220.190:9096/registrationprocessor-utility/v0.1/registration-processor/util/decryptPacket";
	
	public void decryptFile(File f,String path) throws IOException {
		System.out.println(path);
		Response response=applnMethods.putFile(f,decrypterURL);
		System.out.println(response.contentType());
		System.out.println(response.asByteArray());
		try (FileOutputStream fileOuputStream = new FileOutputStream(path)){
			    fileOuputStream.write(response.asByteArray());
			 }
/*		 try {
			 	System.out.println(path.substring(0, path.lastIndexOf('.')));
		         ZipFile zipFile = new ZipFile(path);
		         zipFile.extractAll(path.substring(0, path.lastIndexOf('.')));
		    } catch (ZipException e) {
		        e.printStackTrace();
		    }*/
		System.exit(0);
	}
	@SuppressWarnings("unchecked")
	public  void tweakFile(String testCaseName,String parameterToBeChanged) throws IOException {
		JSONObject metaInfo=null;
		String configPath=System.getProperty("user.dir")+"/"+"src/test/resources/regProc/Stagevalidation/NegativePackets/"+testCaseName;
		File file=new File(configPath);
		File[] listOfFiles=file.listFiles();
		for(File f: listOfFiles) {
			decryptFile(f,f.getName());
			File[] packetFiles=f.listFiles();
			for(File info:packetFiles) {
				
				if(info.getName().toLowerCase().equals("packet_meta_info.json")) {
					try {
						metaInfo=(JSONObject) new JSONParser().parse(new FileReader(info.getPath()));
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}	
					System.out.println("Initial Info is :: "+ metaInfo);
					JSONObject identity= (JSONObject) metaInfo.get("identity");
					JSONArray metaData=(JSONArray) identity.get("metaData");
					JSONArray updatedData=tweakParameter(metaData,parameterToBeChanged);
					metaInfo.put("identity", identity);
					System.out.println("Updated info is  :: " + metaInfo);
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
					Response response=applnMethods.putFile(info, decrypterURL);
					System.out.println(response.asString());
					
				}
			
			}
			
		}
	
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
				labels.put("value", "02649259190007420190124163340");
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
	
	public static void main(String[] args) throws IOException {
		EncrypterDecrypter e=new EncrypterDecrypter();
		e.tweakFile("InvalidCentreID","centerId");
	}
}
