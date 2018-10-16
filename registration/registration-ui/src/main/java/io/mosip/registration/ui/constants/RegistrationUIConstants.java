package io.mosip.registration.ui.constants;

import java.util.List;

import org.assertj.core.util.Arrays;

public class RegistrationUIConstants {
	
	private RegistrationUIConstants() {
		
	}

	//Login
	public static final String LOGIN_METHOD_PWORD = "PWD";	
	public static final String OTP = "OTP";
	public static final String BLOCKED = "BLOCKED";
	public static final String USERNAME_FIELD_EMPTY = "Please Enter UserName";
	public static final String PWORD_FIELD_EMPTY = "Please Enter Password";
	public static final String CREDENTIALS_FIELD_EMPTY = "Please Enter UserName and Password";
	public static final String CREDENTIALS_FIELD_ERROR = "Please Enter valid UserName and Password";
	public static final String BLOCKED_USER_ERROR = "Unable to process. Please contact Admin";
	public static final String LOGIN_INFO_MESSAGE = "Login Information";
	public static final String OTP_FIELD_EMPTY="Please Enter OTP";
	public static final String LOGIN_ALERT_TITLE="LOGIN ALERT";
	public static final String LOGIN_FAILURE="Unable To Login";
	public static final String ALERT_ERROR="ERROR";
	public static final String OTP_INFO_MESSAGE="OTP Login Information";
	public static final String OTP_VALIDATION_ERROR_MESSAGE="Please Enter Valid OTP";
	public static final String USERNAME_FIELD_ERROR="Please Enter Valid Username";
	public static final String CENTER_ID = "centerId";
	public static final String HH_MM_SS = "HH:mm:ss";
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
