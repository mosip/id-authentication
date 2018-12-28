package io.mosip.registration.processor.core.exception.util;

import io.mosip.registration.processor.core.constant.PacketFiles;

public class PacketStructure {

	public static final String FILE_SEPERATOR = "\\";
	public static final String APPLICANTDEMOGRAPHIC = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT+ FILE_SEPERATOR;
	public static final String APPLICANTBIOMETRIC = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT+ FILE_SEPERATOR;
	
	public static final String APPLICANTPHOTO = APPLICANTDEMOGRAPHIC + PacketFiles.APPLICANTPHOTO;
	public static final String DEMOGRAPHICINFO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.DEMOGRAPHICINFO;
	public static final String PROOFOFADDRESS = APPLICANTDEMOGRAPHIC + PacketFiles.PROOFOFADDRESS;
	public static final String PROOFOFIDENTITY = APPLICANTDEMOGRAPHIC + PacketFiles.PROOFOFIDENTITY;
	public static final String EXCEPTIONPHOTO = APPLICANTDEMOGRAPHIC + PacketFiles.EXCEPTIONPHOTO;
	
	public static final String BOTHTHUMBS =  APPLICANTBIOMETRIC + PacketFiles.BOTHTHUMBS;
	public static final String LEFTEYE = APPLICANTBIOMETRIC + PacketFiles.LEFTEYE;
	public static final String RIGHTEYE = APPLICANTBIOMETRIC + PacketFiles.RIGHTEYE;
	public static final String LEFTPALM = APPLICANTBIOMETRIC + PacketFiles.LEFTPALM;
	public static final String RIGHTPALM = APPLICANTBIOMETRIC + PacketFiles.RIGHTPALM;
	
	public static final String PACKETMETAINFO = PacketFiles.PACKETMETAINFO.name();
	
	private PacketStructure() {

	}
}
