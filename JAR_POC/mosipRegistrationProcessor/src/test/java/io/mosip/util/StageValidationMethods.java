package io.mosip.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import io.mosip.entity.TransactionStatus;
import io.mosip.dao.RegProcTransactionDb;

public class StageValidationMethods {
	private static Logger logger = Logger.getLogger(StageValidationMethods.class);
	RegProcTransactionDb packetTransaction = new RegProcTransactionDb();
	final String configPath = "src/test/resources/regProc/Stagevalidation";
	public String finalStatus = "";
	static public List<String> statusFromDb=new LinkedList<String>();
	public List<String> getStatusList(String testCaseName) {
		List<String> regStatus = packetTransaction.readStatus(getRegID(testCaseName));
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
