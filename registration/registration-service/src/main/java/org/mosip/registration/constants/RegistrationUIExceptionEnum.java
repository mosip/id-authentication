package org.mosip.registration.constants;

/**
 * Enum for Registration UI exception Codes
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationUIExceptionEnum {
		
	REG_UI_SHEDULER_ARG_EXCEPTION("REG-UI-SHE-001", "Please verify the argument passed"),
	REG_UI_SHEDULER_STATE_EXCEPTION("REG-UI-SHE-002", "The state not found"),
	REG_UI_SHEDULER_IOEXCEPTION_EXCEPTION("REG-UI-SHE-004", "Unable to load the screen"),
	REG_UI_LOGIN_IO_EXCEPTION("REG-UI-SHE-005", "IO Exception"),
	REG_UI_LOGIN_RESOURCE_EXCEPTION("REG-UI-SHE-007", "Unable to load the Resource"),
	REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION("REG-UI-SHE-008", "Unable to Initial Login Screen"),
	REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION("REG-UI-SHE-009", "Unable to load the Login Screen"),
	REG_UI_HOMEPAGE_IO_EXCEPTION("REG-UI-SHE-012", "Unable to load the Home Screen"),
	REG_ACK_TEMPLATE_IO_EXCEPTION("REG-UI-SHE-013","Unable to write the image file");
	

	private final String errorCode;
	private final String errorMessage;

	private RegistrationUIExceptionEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
