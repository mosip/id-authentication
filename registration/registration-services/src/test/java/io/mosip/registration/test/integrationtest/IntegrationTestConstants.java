package io.mosip.registration.test.integrationtest;

public class IntegrationTestConstants {

	
	
public static final String FP_Path_10_FP_NotMatchWithDB="src/test/resources/testData/FingerPrintCaptureServiceData/10_FP_NotMatchWithDB.json";	
public static final String FpPath_10_FP_Match_RightIndex_WithDB="src/test/resources/testData/FingerPrintCaptureServiceData/10_FP_Match_RightIndex_WithDB.json";
public static final String FpPath_6_FP_NotMatchWithDB="src/test/resources/testData/FingerPrintCaptureServiceData/6_FP_NotMatchWithDB.json";	
public static final String FpPath_6_FP_Match_RightIndex_WithDB="src/test/resources/testData/FingerPrintCaptureServiceData/6_FP_Match_RightIndex_WithDB.json";
public static final String FpPath_Single_FP_Auth_Not_matchWithDB="src/test/resources/testData/FingerPrintCaptureServiceData/Single_FP_Auth_Not_matchWithDB.json";
public static final String FpPath_Single_FP_Auth_Match_WithDB="src/test/resources/testData/FingerPrintCaptureServiceData/Single_FP_Auth_Match_WithDB.json";

public static String expectedCenterID=null;
public static String expectedStatinID=null;
public static final String actualCenterID="10031";
public static final String actualStationID="10011";

public static final String centerID="centerId";
public static final String CenterId_val="20916";
public static final String RegistrationCenterId_val="10011";
public static final String userId_val="mosip";
public static final String stationId="stationId";
public static final String stationId_val="10011";

public static final String USER_ON_BOARD_THRESHOLD_LIMIT="USER_ON_BOARD_THRESHOLD_LIMIT";
public static final String USER_ON_BOARD_THRESHOLD_LIMIT_val="10";
public static final String userOBErrormsg="Threshold for number of successful authentications not met.";
public static final String UOB_Validate_null_path="src/test/resources/testData/UserOnboardServiceData/Validate_null.json";
public static final String UOB_NO_IRIS_NoTHUMB_9="src/test/resources/testData/UserOnboardServiceData/NO_IRIS_NoTHUMB_9.json";
public static final String UOB_success_msg="User on-boarded successfully.";
public static final String IRIS_FP_NoLeftThumbs_path="src/test/resources/testData/UserOnboardServiceData/IRIS_FP_NoLeftThumbs.json";
public static final String FP_NoIRIS_path="src/test/resources/testData/UserOnboardServiceData/FP_NoIRIS.json";

public static final String NS_success_msg="Success";
public static final String NS_msg="Testing SMS service";
public static final String NS_number="9551085729";
public static final String NS_RegIds="12345678901234567890123456789";
public static final String NS_email="maryroseline2@gmail.com";
public static final String NS_errormsg="Unable to send EMAIL Notification";
public static final String NS_emailIncorrect="maryroseline2@gmail.com";
public static final String NS_errorSMS="Contact number cannot contains alphabet,special character or less than or more than 10 digits";
public static final String NS_numberIncorrect="95510";




}
