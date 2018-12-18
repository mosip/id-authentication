package io.mosip.registration.processor.core.exception.util;

import io.mosip.registration.processor.core.constant.PacketFiles;

public class PacketStructure {

	public static final String FILE_SEPERATOR = "\\";
	public static final String APPLICANTPHOTO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.APPLICANTPHOTO;
	public static final String DEMOGRAPHICINFO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.DEMOGRAPHICINFO;
	public static final String PROOFOFADDRESS = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.PROOFOFADDRESS;
	public static final String PROOFOFIDENTITY = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.PROOFOFIDENTITY;
	public static final String EXCEPTIONPHOTO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.EXCEPTIONPHOTO;
	public static final String BOTHTHUMBS = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.BOTHTHUMBS;
	public static final String LEFTEYE = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.LEFTEYE;
	public static final String RIGHTEYE = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.RIGHTEYE;
	public static final String LEFTPALM = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.LEFTPALM;
	public static final String RIGHTPALM = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.RIGHTPALM;
	public static final String PACKETMETAINFO = PacketFiles.PACKETMETAINFO.name();

	private PacketStructure() {

	}
}
