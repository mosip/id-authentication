package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.TransactionStatus;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;
import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.dbdto.RegistrationPacketSyncDTO;

public class StageValidationMethods extends BaseTestCase {
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	//StageValidationMethods apiRequest=new StageValidationMethods();
	private static Logger logger = Logger.getLogger(StageValidationMethods.class);
	RegProcApiRequests apiRequests=new RegProcApiRequests();
	RegProcTransactionDb packetTransaction = new RegProcTransactionDb();
	String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
	Properties prop =  new Properties();
	final String configPath = "src/test/resources/regProc/Stagevalidation";
	public String finalStatus = "";
	static public List<String> statusFromDb=new LinkedList<String>();
	ApplicationLibrary applnMethods=new ApplicationLibrary();
	EncryptData encryptData=new EncryptData();
	String validToken="";
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	private final String encrypterURL="/v1/cryptomanager/encrypt";
	@SuppressWarnings("unchecked")
	public String syncPacket(File packet) {
		validToken=getToken("syncTokenGenerationFilePath");
		RegistrationPacketSyncDTO registrationPacketSyncDto=null;;
		try {
			registrationPacketSyncDto = encryptData.createSyncRequest(packet);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String regId=registrationPacketSyncDto.getSyncRegistrationDTOs().get(0).getRegistrationId();
		JSONObject requestToEncrypt=encryptData.encryptData(registrationPacketSyncDto);
		String center_machine_refID=regId.substring(0,5)+"_"+regId.substring(5, 10);
		Response resp=apiRequests.postRequestToDecrypt(encrypterURL,requestToEncrypt,MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON,validToken);
		String encryptedData = resp.jsonPath().get("response.data").toString();
		LocalDateTime timeStamp=null;
		try {
			timeStamp = encryptData.getTime(regId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response actualResponse = apiRequests.regProcSyncRequest(prop.getProperty("syncListApi"),encryptedData,center_machine_refID,
				timeStamp.toString()+"Z", MediaType.APPLICATION_JSON,validToken);
		int status=actualResponse.statusCode();
		if(status==200) {
			return "Sync Successfull";
		} else {
			return "Sync Unsuccessfull";
		}
	}
	public void uploadPacket(File file) {
		validToken=getToken("syncTokenGenerationFilePath");
		String propertyFilePath=System.getProperty("user.dir")+"\\"+"src\\config\\RegistrationProcessorApi.properties";
		Properties prop=new Properties();
		try {
			prop.load(new FileReader(new File(propertyFilePath)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Response response=apiRequests.regProcPacketUpload(file, prop.getProperty("packetReceiverApi"),validToken);
		logger.info("Response from packet upload :: "+ response.asString());
	}
	public List<String> getStatusList(String regID) {
		List<String> regStatus = packetTransaction.readStatus(regID);
		return regStatus;
	}
	
	public String getRegID(String testCaseName) {
		String reg_ID = "";
		File file = new File(configPath + "/" + testCaseName);
		File[] listOfFile = file.listFiles();
		for (File f : listOfFile) {
			reg_ID = f.getName().substring(0, f.getName().lastIndexOf('.'));
		}
		return reg_ID;
	}

	public String uploadPacketToVirusScanStage(List<String> statusCodes) {
		for (String status : statusCodes) {
			if (status.equals(TransactionStatus.PACKET_UPLOADED_TO_VIRUS_SCAN.toString())) {
				statusFromDb.add(TransactionStatus.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
				finalStatus = status;
				break;
			} else {
				finalStatus = TransactionStatus.PACKET_UPLOAD_TO_VIRUS_SCAN_FAILED.toString();
				statusFromDb.add(TransactionStatus.PACKET_UPLOAD_TO_VIRUS_SCAN_FAILED.toString());
			}
		}
		if(statusCodes.isEmpty()) {
			statusFromDb.add(TransactionStatus.PACKET_UPLOAD_TO_VIRUS_SCAN_FAILED.toString());
			finalStatus=TransactionStatus.PACKET_UPLOAD_TO_VIRUS_SCAN_FAILED.toString();
		}
		logger.info("Status of upload Packet to virus scan stage is :: " + finalStatus);
		return finalStatus;
	}

	public String virusScanner(List<String> statusCodes) {
		String previousStageStatus = uploadPacketToVirusScanStage(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.PACKET_UPLOADED_TO_VIRUS_SCAN.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.VIRUS_SCAN_SUCCESSFUL.toString())) {
					finalStatus = status;
					statusFromDb.add(TransactionStatus.VIRUS_SCAN_SUCCESSFUL.toString());
					break;
				} else {
					finalStatus = TransactionStatus.VIRUS_SCAN_FAILED.toString();
					statusFromDb.add(TransactionStatus.VIRUS_SCAN_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.VIRUS_SCAN_FAILED.toString());
		}
		logger.info("Final Status of virusScanner :: " + finalStatus);
		return finalStatus;
	}

	public String packetUploadToFileSystem(List<String> statusCodes) {
		String previousStageStatus = virusScanner(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.VIRUS_SCAN_SUCCESSFUL.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.PACKET_UPLOADED_TO_FILESYSTEM.toString())) {
					statusFromDb.add(TransactionStatus.PACKET_UPLOADED_TO_FILESYSTEM.toString());
					finalStatus = status;
					break;
				} 
			}
		} else {
			statusFromDb.add(TransactionStatus.PACKET_UPLOADED_TO_FILESYSTEM.toString());
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
	}
		logger.info("Final Status of Upload Packet To File System is :: " + finalStatus);
		return finalStatus;
	}

	public String structureValidationStage(List<String> statusCodes) {
		String previousStageStatus = packetUploadToFileSystem(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.PACKET_UPLOADED_TO_FILESYSTEM.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.STRUCTURE_VALIDATION_SUCCESS.toString())) {
					statusFromDb.add(TransactionStatus.STRUCTURE_VALIDATION_SUCCESS.toString());
					finalStatus = status;
					break;
				} else {
					finalStatus = TransactionStatus.STRUCTURE_VALIDATION_FAILED.toString();
					statusFromDb.add(TransactionStatus.STRUCTURE_VALIDATION_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.STRUCTURE_VALIDATION_FAILED.toString());
		}
		logger.info("Status of structure validation stage is :: " + finalStatus);
		return finalStatus;
	}

	public String osiValidationStage(List<String> statusCodes) {
		String previousStageStatus = structureValidationStage(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.STRUCTURE_VALIDATION_SUCCESS.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.PACKET_OSI_VALIDATION_SUCCESS.toString())) {
					statusFromDb.add(TransactionStatus.PACKET_OSI_VALIDATION_SUCCESS.toString());
					finalStatus = status;
					break;
				} else {
					finalStatus = TransactionStatus.PACKET_OSI_VALIDATION_FAILED.toString();
					statusFromDb.add(TransactionStatus.PACKET_OSI_VALIDATION_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.PACKET_OSI_VALIDATION_FAILED.toString());
		}
		logger.info("Status of OSI Validation Stage is :: " + finalStatus);
		return finalStatus;
	}

	public String demoDedupeStage(List<String> statusCodes) {
		String previousStageStatus = osiValidationStage(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.PACKET_OSI_VALIDATION_SUCCESS.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.PACKET_DEMO_DEDUPE_SUCCESS.toString())) {
					statusFromDb.add(TransactionStatus.PACKET_DEMO_DEDUPE_SUCCESS.toString());
					finalStatus = status;
					break;
				} else {
					finalStatus = TransactionStatus.PACKET_DEMO_DEDUPE_FAILED.toString();
					statusFromDb.add(TransactionStatus.PACKET_DEMO_DEDUPE_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.PACKET_DEMO_DEDUPE_FAILED.toString());
		}
		logger.info("Status of Demo Dedupe Stage is :: " + finalStatus);
		return finalStatus;
	}

	public String bioDedupeStage(List<String> statusCodes) {
		String previousStageStatus = demoDedupeStage(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.PACKET_DEMO_DEDUPE_SUCCESS.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.PACKET_BIO_DEDUPE_SUCCESS.toString())) {
					statusFromDb.add(TransactionStatus.PACKET_BIO_DEDUPE_SUCCESS.toString());
					finalStatus = status;
					break;
				} else {
					finalStatus = TransactionStatus.PACKET_BIO_DEDUPE_FAILED.toString();
					statusFromDb.add(TransactionStatus.PACKET_BIO_DEDUPE_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.PACKET_BIO_DEDUPE_FAILED.toString());
		}
		logger.info("Status of bioDedupeStage Stage is :: " + finalStatus);
		return finalStatus;
	}

	public String uinGenerationStage(List<String> statusCodes) {
		String previousStageStatus = bioDedupeStage(statusCodes);
		if (previousStageStatus.equals(TransactionStatus.PACKET_BIO_DEDUPE_SUCCESS.toString())) {
			for (String status : statusCodes) {
				if (status.equals(TransactionStatus.PACKET_UIN_UPDATION_SUCCESS.toString())) {
					statusFromDb.add(TransactionStatus.PACKET_UIN_UPDATION_SUCCESS.toString());
					finalStatus = status;
					break;
				} else {
					finalStatus = TransactionStatus.UIN_GENERATION_FAILED.toString();
					statusFromDb.add(TransactionStatus.UIN_GENERATION_FAILED.toString());
				}
			}
		} else {
			finalStatus = TransactionStatus.PREVIOUS_STAGE_FAILED.toString();
			statusFromDb.add(TransactionStatus.UIN_GENERATION_FAILED.toString());
		}
		logger.info("Status of UIN Generation Stage is :: " + finalStatus);
		return finalStatus;
	}
	
	public List<String> getStatusCodeListFromDb(List<String> statusCodes){
		uinGenerationStage(statusCodes);
		return statusFromDb;
	}
}
