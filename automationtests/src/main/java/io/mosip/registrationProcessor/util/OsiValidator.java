package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.mosip.service.BaseTestCase;
import io.mosip.util.EncrypterDecrypter;
import net.lingala.zip4j.exception.ZipException;
/**
 * 
 * @author M1047227
 *
 */
public class OsiValidator extends BaseTestCase{
	private static Logger logger = Logger.getLogger(OsiValidator.class);
	EncrypterDecrypter encryptDecrypt=new EncrypterDecrypter();
public void UmcValidation() {
	Properties property=new Properties();
	Properties kernelApis=new Properties();
	File decryptedFile=null;
	JSONObject metaInfo = null;
	RegProcApiRequests apiRequests = new RegProcApiRequests();
	try {
		FileReader reader=new FileReader(new File(apiRequests.getResourcePath()+"config/folderPaths.properties"));
		FileReader kernelApiReader=new FileReader(new File(apiRequests.getResourcePath()+"config/UmcValidationApis.properties"));
		property.load(reader);
		kernelApis.load(kernelApiReader);
		reader.close();
		kernelApiReader.close();
		String validPacketpath=property.getProperty("pathForValidRegProcPackets");
		File file=new File(validPacketpath);
		File[] listOfFiles=file.listFiles();
		for(File packet:listOfFiles){
			if(packet.getName().contains(".zip")) {
			LocalDateTime ldt=createTimeStamp(packet.getName().substring(0,packet.getName().lastIndexOf('.')));
			String currentTimeStamp=ldt.atOffset(ZoneOffset.UTC).toString();
			String centerId=packet.getName().substring(0,5);
			String machineId=packet.getName().substring(5,10);
			
			JSONObject decryptingRequest=encryptDecrypt.generateCryptographicData(packet);
		
			try {
				decryptedFile=encryptDecrypt.decryptFile(decryptingRequest, validPacketpath, packet.getName());
			} catch (ZipException e1) {
				logger.error("Could Not Extract",e1);
			} catch (org.json.simple.parser.ParseException e1) {
				logger.error("Could not parse into json request",e1);
			}

			for(File insidePacketFiles: decryptedFile.listFiles()) {
				if(insidePacketFiles.getName().equals("packet_meta_info.json")) {
					FileReader metaFileReader = new FileReader(insidePacketFiles.getPath());
					try {
						metaInfo = (JSONObject) new JSONParser().parse(metaFileReader);
						JSONObject identity = (JSONObject) metaInfo.get("identity");
						JSONArray registeredDevices = (JSONArray) identity.get("capturedRegisteredDevices");
						getDeviceIds(registeredDevices);
					} catch (org.json.simple.parser.ParseException e) {
						logger.error("Could not parse packetMetaInfo.json", e);
					}
					metaFileReader.close();
				}
			}
		}
		}
	} catch (IOException e) {
		
		logger.error("File Not Found :: ",e);
	}
}

public void getDeviceIds(JSONArray registeredDevices) {
	Map<String, String> deviceInfo=new HashMap<String,String>();
	for(int i=0;i<registeredDevices.size();i++) {
		JSONObject labels = (JSONObject) registeredDevices.get(i);
		deviceInfo.put(labels.get("label").toString(), labels.get("value").toString());
	}
	logger.info("device Info are :: "+deviceInfo );
}
public LocalDateTime createTimeStamp(String regID) {
	LocalDateTime ldt = null;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
	String packetCreatedDateTime = regID.substring(regID.length() - 14);
	int n = 100 + new Random().nextInt(900);
	String milliseconds = String.valueOf(n);
	Date date;
	try {
		date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
				+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6)+milliseconds);
		ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	} catch (ParseException e) {
		logger.error("Could Not Parse Date",e);
	}
	return ldt;
}
public void getCenterHistory(Properties prop, JSONObject request) {
	
}
public static void main(String[] args) {
	OsiValidator osi=new OsiValidator();
	osi.UmcValidation();
}
}
