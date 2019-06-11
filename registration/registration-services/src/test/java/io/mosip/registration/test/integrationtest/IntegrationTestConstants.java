package io.mosip.registration.test.integrationtest;

public class IntegrationTestConstants {

	public static final String FINGERPRINT10PATHNOTMATCHWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/Resident_10_FP_NotMatchWithDB.json";
	public static final String FINGERPRINT10PATHMATCHRIGHTINDEXWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/Resident_10_FP_Match_RightIndex_WithDB.json";
	public static final String FINGERPRNT6PATHNOTMATCHWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/Resident_6_FP_NotMatchWithDB.json";
	public static final String FINGERPRINT6PATHMATCHRIGHTINDEXWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/Resident_6_FP_Match_RightIndex_WithDB.json";
	public static final String FINGERPRINTPATHSINGLEFFPAUTHNOTMATCHWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/OfficerOrSupervisor_Single_FP_Auth_Not_matchWithDB.json";
	public static final String FINGERPRINTPATHSINGLEFPAUTHMATCHWITHDB = "src/test/resources/testData/FingerPrintCaptureServiceData/OfficerOrSupervisor_Single_FP_Auth_Match_WithDB.json";

	public String USERNAMEONBOARD = "testQA";

	public String PASSWORD = "mosip";
	public String COMPASSWORD = "E2E488ECAF91897D71BEAC2589433898414FEEB140837284C690DFC26707B262";
	// 773257F5C4279780CC87E47B6346908C54783C37E2091DE01F8B66A163FCC481";
	//
	public String[] ONBOARDQUERIES = { "ADD_USERDETAIL", "ADD_USRPWD", "ADD_USER_ROLE", "ADD_REGCENTER" };

	public String CENTERIDEXPECTED = null;
	public String STATIONIDEXPECTED = null;
	public static final String CENTERIDACTUAL = "10031";
	public static final String STATIONIDATUAL = "10001";

	public static final String CENTERIDLBL = "centerId";
	public static final String CENTERIDVAL = "20916";
	public static final String REGCENTERIDVAL = "10014";
	public static final String USERIDVAL = "110017";
	public static final String STATIONIDLBL = "stationId";
	public static final String STATIONIDVAL = "10001";
	public static final String FINGERPRINTSCORE = "90";

	public static final String USERONBOARDTHRESHOLDLIMIT = "USER_ON_BOARD_THRESHOLD_LIMIT";
	public static final String USERONBOARDTHRESHOLDLIMITVAL = "10";
	public static final String USERONBOARDERRORMSG = "USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG";
	public static final String USERONBOARDVALIDATENULLPATH = "src/test/resources/testData/UserOnboardServiceData/OfficerOrSupervisor_No_FP_No_IRIS_No_FACE.json";
	public static final String UOBNOIRISNOTHUMB9 = "src/test/resources/testData/UserOnboardServiceData/OfficerOrSupervisor_NO_IRIS_NoTHUMB_9Count.json";
	public static final String USERONBOARDSUCCESSMSG = "USER_ONBOARD_SUCCESS";
	public static final String IRISFINGERPRINTNOLEFTTHUMBPATH = "src/test/resources/testData/UserOnboardServiceData/OfficerOrSupervisor_IRIS_FP_NoLeftThumbs.json";
	public static final String FPNOIRISPATH = "src/test/resources/testData/UserOnboardServiceData/OfficerOrSupervisor_FP_NoIRIS.json";

	public static final String NSSUCCESSMSG = "success";
	public static final String SMSMESSAGENS = "Testing SMS service";
	public static final String EMAILMESSAGENS = "Testing EMAIL service";
	public static final String NSNUMBER = "9551085729";
	public static final String NSREGID = "12345678901234567890123456789";
	public static final String NSEMAIL = "newmailid0123@gmail.com";
	public static final String NSERRORMSG = "Unable to send EMAIL Notification";
	public static final String NSEMAILINCORRECT = "maryroseline2@gmail.com";
	public static final String NSERRORSMS = "Contact number cannot contains alphabet,special character or less than or more than ";
	public static final String NSNUMBERINCORRECT = "95510";
	public final static String INVALIDEMAILNS = "newmailid0123@";
	public final static String INVALIDNUM = "955108qwee";
	public static final String REGDETAILSJSON = "src/test/resources/testData/PacketHandlerServiceData/RegistrationUserDetails.json";
	public static final String IDENTITYJSON = "src/test/resources/testData/PacketHandlerServiceData/RegistrationIdentityDetails.json";
	public static final String CHILDIDENTITYJSON = "src/test/resources/testData/PacketHandlerServiceData/ChildRegistrationIdentityDetails.json";
	public static final String POAPOBPORPOIJPG = "src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg";
	public static final String RANDOMVAL = "RandomUserID";
	public static final String REGDETAILSWITHOUTBIOJSON = "src/test/resources/testData/PacketHandlerServiceData/RegistrationUserDetailsNonBimetricWithFaceDetails.json";
	public static final String centerID="";
	public static final String userOBErrormsg="";
	public static final String UOB_Validate_null_path="";
	public static final String userId_val="";
	public static final String stationId="";
	public static final String stationId_val="";
	public static final String USER_ON_BOARD_THRESHOLD_LIMIT="";
	public static final String USER_ON_BOARD_THRESHOLD_LIMIT_val="";
	
	
	public static final String UOB_NO_IRIS_NoTHUMB_9="";

	public static final String IRIS_FP_NoLeftThumbs_path="";
	public static final String UOB_success_msg="";
	public static final String FP_NoIRIS_path="";
	/*
	 * 
	 * Constants to be used in ReregistrationServiceTest test cases
	 * 
	 */
	// public static final String REREGISTRATION_ROLES = "SUPERADMIN,SUPERVISOR";
	public static final String REREGISTRATION_ROLES = "REGISTRATION_ADMIN,REGISTRATION_SUPERVISOR";
	public static final String REREGISTRATION_USERID = "110017";

	/*
	 * 
	 * Constants to be used in LoginServiceTest test cases
	 */
	public static final String LOGIN_USERID = "110017";

	public static final String RegistrationCenterId_val = "10022";

}
