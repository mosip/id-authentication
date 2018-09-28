package org.mosip.registration.constants;

/**
 * Enum for Registration UI exception Codes
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum RegistrationUIExceptionEnum {
		
	REG_UI_SHEDULER_ARG_EXCEPTION("REG-UI-SHE-001", "IllegalArgumentException"),
	REG_UI_SHEDULER_STATE_EXCEPTION("REG-UI-SHE-002", "IllegalStateException"),
	REG_UI_SHEDULER_NULLPOINTER_EXCEPTION("REG-UI-SHE-003", "NULL POINTER Exception"),
	REG_UI_SHEDULER_IOEXCEPTION_EXCEPTION("REG-UI-SHE-004", "FXML NOT FOUND"),
	REG_UI_LOGIN_NULLPOINTER_EXCEPTION("REG-UI-SHE-005", "NullPointerException"),
	REG_UI_LOGIN_RESOURCE_EXCEPTION("REG-UI-SHE-007", "ResourceAccessException"),
	REG_UI_LOGIN_INITIALSCREEN_NULLPOINTER_EXCEPTION("REG-UI-SHE-008", "Initial Login Screen NullPointerException"),
	REG_UI_LOGIN_SCREEN_NULLPOINTER_EXCEPTION("REG-UI-SHE-009", "Login Screen NullPointerException"),
	REG_UI_HOMEPAGE_NULLPOINTER_EXCEPTION("REG-UI-SHE-012", "Home Screen NullPointerException"),
	REG_ACK_TEMPLATE_IO_EXCEPTION("REG-UI-SHE-013","Exception while writing acknowledgement image into OutputStream");
	

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
