package io.mosip.registration.ui.constants;

import java.util.Arrays;
import java.util.List;

public class RegistrationUIConstants {

	private RegistrationUIConstants() {

	}


	//paths of FXML pages to be loaded
	public static final String ERROR_PAGE = "/fxml/ErrorPage.fxml";
	public static final String INITIAL_PAGE = "/fxml/RegistrationLogin.fxml";
	public static final String LOGIN_PWORD_PAGE = "/fxml/LoginWithCredentials.fxml";
	public static final String LOGIN_OTP_PAGE = "/fxml/LoginWithOTP.fxml";
	public static final String HOME_PAGE = "/fxml/RegistrationOfficerLayout.fxml";
	public static final String HEADER_PAGE = "/fxml/Header.fxml";
	public static final String UPDATE_PAGE = "/fxml/UpdateLayout.fxml";
	public static final String OFFICER_PACKET_PAGE = "/fxml/RegistrationOfficerPacketLayout.fxml";
	public static final String CREATE_PACKET_PAGE = "/fxml/Registration.fxml";
	public static final String ACK_RECEIPT_PATH = "/fxml/AckReceipt.fxml";
	public static final String APPROVAL_PAGE = "/fxml/RegistrationApproval.fxml";
	public static final String FTP_UPLOAD_PAGE =  "/fxml/FTPLogin.fxml";
	public static final String USER_MACHINE_MAPPING =  "/fxml/UserClientMachineMapping.fxml";
	public static final String SYNC_STATUS =  "/fxml/RegPacketStatus.fxml";
	
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
	
	//UI Registration Validations
	public static final String FIRST_NAME_EMPTY = "Please Provide First Name";
	public static final String MIDDLE_NAME_EMPTY = "Please Provide Middle Name";
	public static final String LAST_NAME_EMPTY = "Please Provide Last Name";
	public static final String AGE_EMPTY = "Please Provide Age";
	public static final String AGE_WARNING = "Please Provide Date Of Birth";
	public static final String DATE_OF_BIRTH_EMPTY = "Please Provide Date Of Birth";
	public static final String GENDER_EMPTY = "Please Provide Gender";
	public static final String ADDRESS_LINE_1_EMPTY = "Please Provide Adress Line 1";
	public static final String ADDRESS_LINE_2_EMPTY = "Please Provide Adress Line 2";
	public static final String COUNTRY_EMPTY = "Please Provide Country";
	public static final String STATE_EMPTY = "Please Provide State";
	public static final String DISTRICT_EMPTY = "Please Provide District";
	public static final String REGION_EMPTY = "Please Provide Region";
	public static final String PIN_EMPTY = "Please Provide Adress Pin";
	public static final String MOBILE_NUMBER_EMPTY = "Please Provide Mobile Number";
	public static final String MOBILE_NUMBER_EXAMPLE = "Example : 99-9854-2496";
	public static final String LAND_LINE_NUMBER_EMPTY = "Please Provide Land Line Number";
	public static final String LAND_LINE_NUMBER_EXAMPLE = "Example : 44-9854";
	public static final String PARENT_NAME_EMPTY = "Please Provide Parent Name";
	public static final String UIN_ID_EMPTY = "Please Provide Uin Id Of The Parent";
	public static final String ADDRESS_LINE_WARNING = "Address should be between 6 and 20 characters";

	public static final String MACHINE_MAPPING_ACTIVE = "ACTIVE";
	public static final String MACHINE_MAPPING_IN_ACTIVE = "IN-ACTIVE";
	
	//PacketStatusSync
	public static final String PACKET_STATUS_SYNC_ALERT_TITLE="PACKET STATUS SYNC ALERT";
	public static final String PACKET_STATUS_SYNC_INFO_MESSAGE="Packet Status Sync Information";
	
	//onBoard User
	public static final String ONBOARD_BIOMETRICS="Biometrics - ";
	
	public static List getCountries(){
		
		String countries[] = new String[] {"Morocco","Zimbabwe","Zambia","Yemen","Vietnam","Venezuela","Vatican City (Holy See)","Vanuatu","Uzbekistan","Uruguay"};
		
		return Arrays.asList(countries);
	}
	
	public static List getStates(){
		
		String states[] = new String[] {"Tanger-Tetouan-Al Hoceima","Oriental","Fès-Meknès","Rabat-Salé-Kénitra","Béni Mellal-Khénifra"};
		
		return Arrays.asList(states);
	}
	
	public static List getDistricts(){
		
		String districts[] = new String[] {"Casablanca-Settat","Marrakesh-Safi","Drâa-Tafilalet","Souss-Massa","Guelmim-Oued"};
		
		return Arrays.asList(districts);
	}
	
	public static List getRegions(){
		
		String regions[] = new String[] {"Tangier","Oujda","Fès","Rabat","Béni Mellal"};
		
		return Arrays.asList(regions);
	}
	
	public static List getPins(){
		
		String pins[] = new String[] {"80851","80852","80853","80854","80855"};
		
		return Arrays.asList(pins);
	}

}
