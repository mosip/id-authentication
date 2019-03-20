package io.mosip.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.lingala.zip4j.exception.ZipException;

public class TweakRegProcPackets {
	private static Logger logger = Logger.getLogger(TweakRegProcPackets.class);
	static String filesToBeDestroyed=null;
	EncrypterDecrypter encryptDecrypt=new EncrypterDecrypter();
	public  void tweakFile(String testCaseName,String parameterToBeChanged,String parameterValue) throws IOException, ZipException {
		File decryptedFile=null;
		JSONObject metaInfo=null;
		
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
						FileReader metaFileReader=new FileReader(info.getPath());
						metaInfo=(JSONObject) new JSONParser().parse(metaFileReader);
						metaFileReader.close();
					} catch (IOException | ParseException e) {
						e.printStackTrace();
					}	
					JSONObject identity= (JSONObject) metaInfo.get("identity");
					JSONArray metaData=(JSONArray) identity.get("metaData");
					JSONArray updatedData=tweakFile(metaData, parameterToBeChanged, parameterValue);
					metaInfo.put("identity", identity);
					//System.out.println("Inside info is :: "+ metaInfo);
					try (FileWriter updatedFile = new FileWriter(info.getAbsolutePath())) 
		            {
		                try {
							updatedFile.write(metaInfo.toString());
							updatedFile.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		               logger.info("Successfully updated json object to file...!!");
		               
		            } catch (IOException e1) {
						
						e1.printStackTrace();
					}
				}
				
			}
		}
		}
		encryptDecrypt.encryptFile(decryptedFile,configPath,invalidPacketsPath);
		encryptDecrypt.revertPacketToValid(filesToBeDestroyed);
	}
	public JSONArray tweakFile(JSONArray metaData, String parameter, String value) {
		for(int i=0;i<metaData.size();i++) {
			JSONObject labels=(JSONObject) metaData.get(i);
			if(labels.get("label").equals(parameter))
				labels.put("value",value);
		}
		logger.info(metaData);
		return metaData;
	}
	public void invalidPacketGenerator(String propertyFiles) throws FileNotFoundException, IOException, ZipException, InterruptedException {
		EncrypterDecrypter encryptDecrypt=new EncrypterDecrypter();
		TweakRegProcPackets e=new TweakRegProcPackets();
		Properties prop=new Properties();
		String propertyFilePath=System.getProperty("user.dir")+"/src/config/"+propertyFiles;
		prop.load(new FileReader(new File(propertyFilePath)));
		for(String property:prop.stringPropertyNames() ) {
			logger.info("invalid"+property);
			logger.info(prop.getProperty(property));
		e.tweakFile("invalid"+property,property,prop.getProperty(property));
		}
		File decryptedPacket=new File(filesToBeDestroyed);
		encryptDecrypt.destroyFiles(decryptedPacket);
	}
	public static void main(String[] args) throws IOException, ZipException, InterruptedException {
		TweakRegProcPackets e=new TweakRegProcPackets();
		e.invalidPacketGenerator("packetProperties.properties");
	}
}