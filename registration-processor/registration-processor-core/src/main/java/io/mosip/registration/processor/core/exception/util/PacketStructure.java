package io.mosip.registration.processor.core.exception.util;

import io.mosip.registration.processor.core.constant.PacketFiles;

public class PacketStructure {

	public static final String FILE_SEPERATOR = "\\";
	public static final String APPLICANTDEMOGRAPHIC = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR;
	public static final String BIOMETRIC = PacketFiles.BIOMETRIC + FILE_SEPERATOR;

	public static final String APPLICANTPHOTO = APPLICANTDEMOGRAPHIC + PacketFiles.APPLICANTPHOTO;
	public static final String DEMOGRAPHICINFO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.ID;
	public static final String PROOFOFADDRESS = APPLICANTDEMOGRAPHIC + PacketFiles.PROOFOFADDRESS;
	public static final String PROOFOFIDENTITY = APPLICANTDEMOGRAPHIC + PacketFiles.PROOFOFIDENTITY;
	public static final String EXCEPTIONPHOTO = APPLICANTDEMOGRAPHIC + PacketFiles.EXCEPTIONPHOTO;

	public static final String BOTHTHUMBS = BIOMETRIC + PacketFiles.BOTHTHUMBS;
	public static final String LEFTEYE = BIOMETRIC + PacketFiles.LEFTEYE;
	public static final String RIGHTEYE = BIOMETRIC + PacketFiles.RIGHTEYE;
	public static final String LEFTPALM = BIOMETRIC + PacketFiles.LEFTPALM;
	public static final String RIGHTPALM = BIOMETRIC + PacketFiles.RIGHTPALM;

	public static final String PACKETMETAINFO = PacketFiles.PACKET_META_INFO.name();

	private PacketStructure() {

	}
}
