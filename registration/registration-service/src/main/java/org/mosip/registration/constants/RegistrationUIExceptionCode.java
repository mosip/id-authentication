package org.mosip.registration.constants;

/**
 * Class for Registration UI exception Codes
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegistrationUIExceptionCode {
	
	/**
	 * The constructor
	 */
	private RegistrationUIExceptionCode() {
		
	}

	private static String REG_UI_CODE = "REG-UI";
	
	public static String REG_UI_LOGIN_LOADER_EXCEPTION = REG_UI_CODE + "RAI-001";
	public static String REG_UI_LOGIN_SCREEN_LOADER_EXCEPTION = REG_UI_CODE +"LC-002";
	public static String REG_UI_HOMEPAGE_LOADER_EXCEPTION = REG_UI_CODE + "ROC-003";
	public static String REG_UI_SHEDULER_RUNTIME_EXCEPTION = REG_UI_CODE + "SHE-004";
	
}
