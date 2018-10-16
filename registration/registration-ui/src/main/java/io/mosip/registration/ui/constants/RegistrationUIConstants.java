package io.mosip.registration.ui.constants;

public class RegistrationUIConstants {

	private RegistrationUIConstants() {

	}


	//paths of FXML pages to be loades
	public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
	public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
	public static final String LOGIN_PWORD_PAGE = "/fxml/LoginWithCredentials.fxml";
	public static final String LOGIN_OTP_PAGE = "/fxml/LoginWithOTP.fxml";
	public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
	public static final String HEADER_PAGE = "/fxml/Header.fxml";
	public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
	public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
	public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
	public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
	public static final String FTP_UPLOAD_PAGE =  "/fxml/FTPLogin.fxml";
	
	
	//CSS file
	public static final String CSS_FILE_PATH = "application.css";
	// Login
	public static final String LOGIN_METHOD_PWORD = "PWD";
	public static final String OTP = "OTP";
	public static final String BLOCKED = "BLOCKED";
	public static final String MISSING_MANDATOTY_FIELDS = "Missing mandatory fields";
	public static final String USERNAME_FIELD_EMPTY = "UserName is required";
	public static final String PWORD_FIELD_EMPTY = "Password is required";
	public static final String CREDENTIALS_FIELD_EMPTY = "UserName and Password are required";
	public static final String INCORRECT_PWORD = "Incorrect Password";
	public static final String BLOCKED_USER_ERROR = "You are not authorized to perform registration.";
	public static final String LOGIN_INFO_MESSAGE = "Login Information";
	public static final String OTP_FIELD_EMPTY = "Please Enter OTP";
	public static final String LOGIN_ALERT_TITLE = "LOGIN ALERT";
	public static final String LOGIN_FAILURE = "Unable To Login";
	public static final String ALERT_ERROR = "ERROR";
	public static final String OTP_INFO_MESSAGE = "OTP Login Information";
	public static final String OTP_VALIDATION_ERROR_MESSAGE = "Please Enter Valid OTP";
	public static final String USERNAME_FIELD_ERROR = "Please Enter Valid Username";
	public static final String LOGIN_INITIAL_SCREEN = "initialMode";
	public static final String LOGIN_INITIAL_VAL = "1";
	public static final String HH_MM_SS = "HH:mm:ss";

	// Authorization Info
	public static final String AUTHORIZATION_ALERT_TITLE = "Authorization Alert";
	public static final String AUTHORIZATION_INFO_MESSAGE = "Authorization Information";
	public static final String ADMIN_ROLE = "SUPERADMIN";
	public static final String AUTHORIZATION_ERROR = "Please contact Admin.";
	public static final String ROLES_EMPTY = "RolesEmpty";
	public static final String ROLES_EMPTY_ERROR = "You are not authorized to perform registration.";
	public static final String MACHINE_MAPPING = "MachineMapping";
	public static final String MACHINE_MAPPING_ERROR = "You are not Authorized to login as you are not mapped to this machine. Please contact Admin.";
	public static final String SUCCESS_MSG = "Success";
	
	//Acknowledement Form
	public static final String ACKNOWLEDGEMENT_FORM_TITLE = "Registration Acknowledgement";
	
	//Date Format
	public static final String DATE_FORMAT = "MM/dd/yyy hh:mm:ss";
}
