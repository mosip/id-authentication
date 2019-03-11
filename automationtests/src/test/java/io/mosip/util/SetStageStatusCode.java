package io.mosip.util;

import java.util.ArrayList;
import java.util.List;

import io.mosip.dbentity.StageValidtionStatusEnum;

public class SetStageStatusCode {
	static List<String> statusToAssert=new ArrayList<String>(); 
	public void setStageStatusCode(StringBuilder sb) {
		for(int i=0;i<sb.length();i++) {
			switch (i) {
			case 0:
				uploadToVirusScannerStage(sb.charAt(i));
				break;
			case 1:
				virusScanner(sb.charAt(i));
				break;
			case 2:
				packetUploadToFileSystem(sb.charAt(i));
				break;
			case 3: 
				structureValidationStage(sb.charAt(i));
				break;
			case 4: 
				osiValidationStage(sb.charAt(i));
				break;
			case 5:
				demoDedupeStage(sb.charAt(i));
				break;
			case 6: 
				bioDedupeStage(sb.charAt(i));
				break;
			case 7: 
				uinGenerationStage(sb.charAt(i));
				break;
			default:
				break;
			}
		}
	}
	public void uploadToVirusScannerStage(char bit) {
		if(bit== '1') 
			statusToAssert.add(StageValidtionStatusEnum.PACKET_UPLOADED_TO_VIRUS_SCAN.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.PACKET_UPLOAD_TO_VIRUS_SCAN_FAILED.toString());
	}
	
	public void virusScanner(char bit) {
		if(bit=='1') 
			statusToAssert.add(StageValidtionStatusEnum.VIRUS_SCAN_SUCCESSFUL.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.VIRUS_SCAN_FAILED.toString());
	}
	
	public void packetUploadToFileSystem(char bit) {
		statusToAssert.add(StageValidtionStatusEnum.PACKET_UPLOADED_TO_FILESYSTEM.toString());
	}
	
	public void structureValidationStage(char bit) {
		if(bit=='1') 
			statusToAssert.add(StageValidtionStatusEnum.STRUCTURE_VALIDATION_SUCCESS.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.STRUCTURE_VALIDATION_FAILED.toString());
	}
	
	public void osiValidationStage(char bit) {
		if(bit == '1')
			statusToAssert.add(StageValidtionStatusEnum.PACKET_OSI_VALIDATION_SUCCESS.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.PACKET_OSI_VALIDATION_FAILED.toString());
	}
	
	public void demoDedupeStage(char bit) {
		if(bit == '1')
			statusToAssert.add(StageValidtionStatusEnum.PACKET_DEMO_DEDUPE_SUCCESS.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.PACKET_DEMO_DEDUPE_FAILED.toString());
	}
	
	public void  bioDedupeStage(char bit) {
		if(bit == '1')
			statusToAssert.add(StageValidtionStatusEnum.PACKET_BIO_DEDUPE_SUCCESS.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.PACKET_BIO_DEDUPE_FAILED.toString());
	}
	
	public void uinGenerationStage(char bit) {
		if(bit == '1')
			statusToAssert.add(StageValidtionStatusEnum.PACKET_UIN_UPDATION_SUCCESS.toString());
		else
			statusToAssert.add(StageValidtionStatusEnum.UIN_GENERATION_FAILED.toString());
	}
	public List<String> getStatusCodesList(StringBuilder sb){
		setStageStatusCode(sb);
		return statusToAssert;
	}
}
