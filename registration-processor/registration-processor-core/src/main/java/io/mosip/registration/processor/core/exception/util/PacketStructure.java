package io.mosip.registration.processor.core.exception.util;
	
import io.mosip.registration.processor.core.constant.PacketFiles;

/**
 * The Class PacketStructure.
 */
public class PacketStructure {

	/** The Constant FILE_SEPERATOR. */
	public static final String FILE_SEPERATOR = "\\";
<<<<<<< Updated upstream
	
	/** The Constant APPLICANTPHOTO. */
	public static final String APPLICANTPHOTO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.APPLICANTPHOTO;
	
	/** The Constant DEMOGRAPHICINFO. */
	public static final String DEMOGRAPHICINFO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.DEMOGRAPHICINFO;
	
	/** The Constant PROOFOFADDRESS. */
	public static final String PROOFOFADDRESS = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.PROOFOFADDRESS;
	
	/** The Constant PROOFOFIDENTITY. */
	public static final String PROOFOFIDENTITY = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.PROOFOFIDENTITY;
	
	/** The Constant EXCEPTIONPHOTO. */
	public static final String EXCEPTIONPHOTO = PacketFiles.DEMOGRAPHIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.EXCEPTIONPHOTO;
	
	/** The Constant BOTHTHUMBS. */
	public static final String BOTHTHUMBS = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.BOTHTHUMBS;
	
	/** The Constant LEFTEYE. */
	public static final String LEFTEYE = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.LEFTEYE;
	
	/** The Constant RIGHTEYE. */
	public static final String RIGHTEYE = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.RIGHTEYE;
	
	/** The Constant LEFTPALM. */
	public static final String LEFTPALM = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.LEFTPALM;
	
	/** The Constant RIGHTPALM. */
	public static final String RIGHTPALM = PacketFiles.BIOMETRIC + FILE_SEPERATOR + PacketFiles.APPLICANT
			+ FILE_SEPERATOR + PacketFiles.RIGHTPALM;
	
	/** The Constant PACKETMETAINFO. */
	public static final String PACKETMETAINFO = PacketFiles.PACKETMETAINFO.name();

	/**
	 * Instantiates a new packet structure.
	 */
=======
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
	
>>>>>>> Stashed changes
	private PacketStructure() {

	}
}
