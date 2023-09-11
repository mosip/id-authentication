/**
 * 
 */
package io.mosip.authentication.core.constant;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import io.mosip.authentication.core.indauth.dto.IdType;

/**
 * List of all IDA error codes and its respective error messages.
 * 
 * @author Manoj SP
 * @author Nagarjuna K
 *
 */
public enum IdAuthenticationErrorConstants {

	// OTP
	OTP_REQUEST_FLOODED("IDA-OTA-001", "Innumerous OTP requests received"),
	OTP_GENERATION_FAILED("IDA-OTA-002", "Could not generate/send OTP"),
	EXPIRED_OTP("IDA-OTA-003", "OTP has expired", "Please regenerate OTP and try again after sometime"),
	INVALID_OTP("IDA-OTA-004", "OTP is invalid", "Please provide correct OTP value"),
	INVALID_TXN_ID("IDA-OTA-005", "Input transactionID does not match transactionID of OTP Request"),
	BLOCKED_OTP_GENERATE("IDA-OTA-006", "UIN is locked for OTP generation. Please try again later"),
	BLOCKED_OTP_VALIDATE("IDA-OTA-007", "UIN is locked for OTP validation due to exceeding no of invalid OTP trials"),
	OTP_CHANNEL_NOT_PROVIDED("IDA-OTA-008", "OTP Notification Channel not provided"),
	OTP_CHANNEL_NOT_CONFIGURED("IDA-OTA-009", "%s not configured for the country"),
	OTP_AUTH_IDTYPE_MISMATCH("IDA-OTA-010", "Input Identity Type does not match Identity Type of OTP Request"),
	PARTNER_ID_MISMATCH("IDA-OTA-011", "Input Partner-ID does not match Partner-ID of OTP Request"),


	INVALID_TIMESTAMP("IDA-MLC-001", "Request to be received at MOSIP within %s seconds",
			"Please send the request within %s seconds"),
	INVALID_UIN("IDA-MLC-002", "Invalid UIN", "Please retry with the correct UIN"),
	UIN_DEACTIVATED("IDA-MLC-003", "UIN has been deactivated", "Your UIN status is not active"),
	INVALID_VID("IDA-MLC-004", "Invalid VID", "Please retry with correct VID"),
	EXPIRED_VID("IDA-MLC-005", "%s", "Please regenerate VID and try again"),
	MISSING_INPUT_PARAMETER("IDA-MLC-006", "Missing Input Parameter - %s"),
	UNABLE_TO_PROCESS("IDA-MLC-007", "Request could not be processed. Please try again"),
	NO_AUTHENTICATION_TYPE_SELECTED_IN_REQUEST("IDA-MLC-008", "No authentication type selected"),
	INVALID_INPUT_PARAMETER("IDA-MLC-009", "Invalid Input Parameter - %s"),
	VID_DEACTIVATED_UIN("IDA-MLC-010", "VID corresponding to a deactivated UIN"),
	AUTH_TYPE_NOT_SUPPORTED("IDA-MLC-011", "Unsupported Authentication Type - %s",
			"Please use other Authentication Types in the request"),
	CONSENT_NOT_AVAILABLE("IDA-MLC-012", "Individual's Consent is not available"),
	MISSING_AUTHTYPE("IDA-MLC-013", "Missing %s auth attribute"),
	PHONE_EMAIL_NOT_REGISTERED("IDA-MLC-014", "%s not registered. Individual has to register and try again",
			"Please register your %s and try again"),
	IDENTITYTYPE_NOT_ALLOWED("IDA-MLC-015", "Identity Type - %s not configured for the country"),
	INVALID_TXNID_BIO("IDA-MLC-016", "Transaction ID parameters in the request does not match"),
	INVALID_USERID( "IDA-MLC-017","Invalid UserID"),
	ID_NOT_AVAILABLE("IDA-MLC-018", "%s not available in database"),
	AUTH_TYPE_LOCKED("IDA-MLC-019", "%s Auth Type is Locked for the UIN"),
	FAILED_TO_ENCRYPT("IDA-MLC-020", "Unable to encrypt data"),
	FAILED_TO_FETCH_KEY("IDA-MLC-021", "Failed to fetch key from HSM"),
	UIN_DEACTIVATED_BLOCKED("IDA-MLC-022", "UIN is deactivated/blocked"),
	VID_EXPIRED_DEACTIVATED_REVOKED("IDA-MLC-023", "VID is expired/deactivated"),
	INPUT_MISMATCH("IDA-MLC-024", "%s of request is not matching with %s of biometrics"),
	PARTNER_CERT_NOT_AVAILABLE("IDA-MLC-025", "Partner Certificate is not available"),
	IDVID_DEACTIVATED_BLOCKED("IDA-MLC-022", "%s is blocked"),	
	INVALID_BIO_TIMESTAMP("IDA-MLC-030", "Biometrics not captured within %s seconds of previous biometrics",
			"Please capture biometrics within %s seconds of previous biometric capture"),
	INVALID_BIO_DIGITALID_TIMESTAMP("IDA-MLC-031", "DigitalId of Biometrics not captured within %s seconds of previous biometrics",
			"Please capture DigitalId of biometrics within %s seconds of previous biometric capture"),
		
	DEMOGRAPHIC_DATA_MISMATCH_LANG("IDA-DEA-001", "Demographic data %s in %s did not match",
				"Please re-enter your %s in %s"),
	DEMO_DATA_MISMATCH("IDA-DEA-001", "Demographic data %s did not match", "Please re-enter your %s"),
	UNSUPPORTED_LANGUAGE("IDA-DEA-002", "Unsupported Language Code - %s", "Please provide valid Language"),
    DEMO_MISSING("IDA-DEA-003", "Demographic data %s not available in database"),
	DEMO_MISSING_LANG("IDA-DEA-003", "Demographic data %s in %s not available in database"),

	BIO_MISMATCH("IDA-BIA-001", "Biometric data%s did not match", "Please give your biometrics again"),
	DUPLICATE_FINGER("IDA-BIA-002", "Duplicate fingers in request", "Please try again with distinct fingers"),
	FINGER_EXCEEDING("IDA-BIA-003", "Number of Fingers should not exceed %s"),
	INVALID_DEVICEID("IDA-BIA-004", "Device not registered with MOSIP"),
	INVALID_PROVIDERID("IDA-BIA-005", "Device provider not registered with MOSIP"),
	BIOMETRIC_MISSING("IDA-BIA-006", "Biometric data %s not available in database",
			"Your Biometric data is not available in MOSIP"),
	DUPLICATE_IRIS("IDA-BIA-007", "Duplicate Irises in request", "Please try again with distinct Irises"),
	IRIS_EXCEEDING("IDA-BIA-008", "Number of Iris should not exceed 2"),
	FACE_EXCEEDING("IDA-BIA-009", "Number of FACE records should not exceed 1"),
	FINGER_EXCEEDING_FMR("IDA-BIA-010", "Single FMR record contains more than one finger"),
	INVALID_BIOMETRIC("IDA-BIA-011", "Invalid biometric data"),
	INVALID_MDS("IDA-BIA-012", "MDS verification failed"),
	INVALID_HASH("IDA-BIA-014", "Hash Validation Failed"),
	INVALID_SIGNATURE("IDA-BIA-015", "Signature Validation Failed"),
	QUALITY_CHECK_FAILED("IDA-BIA-016", "Unable to Perform Quality Check due to a Technical Issue"),
	BIO_MATCH_FAILED_TO_PERFORM("IDA-BIA-017", "Unable to Perform Biometric Match due to a Technical Issue"),
	UNABLE_TO_PROCESS_BIO("IDA-BIA-018", "Unable to Process the Request due to a Technical Issue"),

	BINDED_KEY_NOT_FOUND("IDA-KBT-001", "Certificate not found for the input x5t#S256: %s and authtype: %s"),
	BINDED_TOKEN_EXPIRED("IDA-KBT-002", "Signed token issued at (iat) is not in allowed time range."),
	ERROR_TOKEN_VERIFICATION("IDA-KBT-003", "Error verifying key binded token. Error: %s"),


	INVALID_ENCRYPT_EKYC_RESPONSE("IDA-EKA-001", "Unable to encrypt eKYC response"),
	INVALID_REQ_PRINT_FORMAT("IDA-EKA-002", "Invalid value in print format request"),
	

	PIN_MISMATCH("IDA-SPA-001", "Pin value did not match", "Please re-enter your pin"),
	PIN_NOT_STORED("IDA-SPA-002", "Could not store the static pin of the individual"),

	DSIGN_FALIED("IDA-MPA-001", "Digital signature verification failed for %s"),
	INVALID_CERTIFICATE("IDA-MPA-002", "Invalid certificate in digital signature"),
	INVALID_ENCRYPTION("IDA-MPA-003", "Unable to decrypt Request"),
	PUBLICKEY_EXPIRED("IDA-MPA-004", "MOSIP Public key expired"),
	OTPREQUEST_NOT_ALLOWED("IDA-MPA-005", "OTP Request Usage not allowed as per policy"),
	AUTHTYPE_NOT_ALLOWED("IDA-MPA-006", "%s Authentication usage not allowed as per policy",
			"Please use other Authentication Types in the request"),
	INVALID_LICENSEKEY("IDA-MPA-007", "License key does not belong to a registered MISP"),
	LICENSEKEY_EXPIRED("IDA-MPA-008", "License key of MISP has expired"),
	PARTNER_NOT_REGISTERED("IDA-MPA-009", "Partner is not registered"),
	PARTNER_NOT_MAPPED("IDA-MPA-010", "MISP and Partner not mapped"),
	LICENSEKEY_SUSPENDED("IDA-MPA-017", "License key of MISP is blocked"),
	PARTNER_DEACTIVATED("IDA-MPA-012", "Partner is deactivated"),
	UNAUTHORISED_PARTNER("IDA-MPA-013", "Partner is unauthorised for eKYC"),
	PARTNER_POLICY_NOTMAPPED("IDA-MPA-014", "Partner is not assigned with any policy"),
	AUTHTYPE_MANDATORY("IDA-MPA-015", "%s-authentiation usage is mandatory as per policy"),
	INVALID_POLICY_ID("IDA-MPA-018", "Policy ID does not belong to a registered Partner"),
	PARTNER_POLICY_NOT_ACTIVE("IDA-MPA-019", "Partner policy is not active"),
	PARTNER_CERTIFICATE_NOT_FOUND("IDA-MPA-020", "Partner (Auth) Certificate not found in DB."),
	PARTNER_CERTIFICATE_NOT_MATCHED("IDA-MPA-021", "Partner (Auth) Certificate not matching with signature header certificate."),
	PARTNER_CERTIFICATE_NOT_FOUND_IN_REQ_HEADER("IDA-MPA-022", "Partner (Auth) Certificate not found in Request signature header."),
	MISP_POLICY_NOT_FOUND("IDA-MPA-023", "MISP Partner Policy not availble."),
	OIDC_CLIENT_NOT_FOUND("IDA-MPA-024", "OIDC Client not availble."),
	UNAUTHORISED_KYC_AUTH_PARTNER("IDA-MPA-025", "Partner is unauthorised for KYC-Auth"),
	UNAUTHORISED_KYC_EXCHANGE_PARTNER("IDA-MPA-026", "Partner is unauthorised for KYC-Exchange"),
	OIDC_CLIENT_DEACTIVATED("IDA-MPA-027", "OIDC Client is deactivated"),
	OIDC_CLIENT_NOT_REGISTERED("IDA-MPA-028", "OIDC Client is not registered"),
	OIDC_CLIENT_AUTHTYPE_NOT_ALLOWED("IDA-MPA-029", "%s Authentication usage not allowed as per client AMR configuration",
			"Please use other Authentication Types in the request"),
	KYC_AUTH_NOT_ALLOWED("IDA-MPA-030", "%s Authentication usage not allowed as per policy",
			"Please try after updating misp policy"),
	KYC_EXCHANGE_NOT_ALLOWED("IDA-MPA-031", "%s not allowed as per policy",
			"Please try after updating misp policy"),
	KEY_BINDING_NOT_ALLOWED("IDA-MPA-032", "%s not allowed as per policy",
			"Please try after updating misp policy"),
	UNAUTHORISED_KEY_BINDING_PARTNER("IDA-MPA-033", "Partner is unauthorised for KeyBinding"),
	KEY_BINDING_MISSING("IDA-MPA-034", "For the input VID/UIN - No Binded key found in DB or binded key is expired.",
			"Please bind a key for the input VID/UIN before performing KBT Auth."),
	KEY_BINDING_CHECK_FAILED("IDA-MPA-035", "KeyBindedToken check failed for the given token.",
			"Provide Valid KeyBindedToken to perform auth."),
	UNAUTHORISED_VCI_EXCHANGE_PARTNER("IDA-MPA-036", "Partner is unauthorised for VCI-Exchange"),
	VCI_EXCHANGE_NOT_ALLOWED("IDA-MPA-037", "%s not allowed as per policy",
			"Please try after updating misp policy"),


	DATA_VALIDATION_FAILED("IDA-IDV-001", "Input Data Validation Failed"),

	// Rest Helper Error Constants
	INVALID_URI("IDA-RST-001", "URI should not be empty"),
	INVALID_HTTP_METHOD("IDA-RST-002", "httpMethod is empty or invalid"),
	INVALID_RETURN_TYPE("IDA-RST-003", "returnType is empty"),
	INVALID_REST_SERVICE("IDA-RST-004", "Rest service name is empty or invalid"),
	INVALID_TIMEOUT("IDA-RST-005", "Timeout is invalid"), CLIENT_ERROR("IDA-RST-006", "4XX - Client Error occurred"),
	SERVER_ERROR("IDA-RST-007", "5XX - Server Error occurred"),
	CONNECTION_TIMED_OUT("IDA-RST-008", "Connection timed out"),
	DOWNLOAD_ERROR("IDA-RST-009", "Error Downloading the data file/config file."),
	
	HMAC_VALIDATION_FAILED("IDA-MPA-016", "HMAC Validation failed"),

	// Device verification validation
	DEVICE_VERIFICATION_FAILED("IDA-DPM-001", "Device is not registered with MOSIP"),
	MDS_VERIFICATION_FAILED("IDA-DPM-002", "MDS is not registered with MOSIP"),
	DEVICE_TYPE_BIO_TYPE_NOT_MATCH("IDA-DPM-004","Device Type and Biometric Type do not match"),
	
	//Partner and Misp validations
	PARTNER_NOT_ACTIVE("PMS_PMP_016","Partner is not active."),
	PARTNER_NOT_MAPPED_TO_POLICY("PMS_PMP_017","Partner is not mapped to any policy."),
	MISP_LICENSE_KEY_NOT_EXISTS("PMS_PMP_020","MISP license key not exists."),
	MISP_LICENSE_KEY_EXPIRED("PMS_PMP_021","MISP license key is expired."),
	PARTNER_NOT_REGISTRED("PMS_PMP_024","Partner is not registered."),
	MISP_IS_BLOCKED("PMS_PMP_025","License key of MISP is blocked"),
	
	POLICY_DATA_NOT_FOUND_EVENT_DATA("PMS_PMP_026","Policy Data is not available in Event data."),
	PARTNER_DATA_NOT_FOUND_EVENT_DATA("PMS_PMP_027","Partner Data is not available in Event data."),
	OIDC_CLIENT_DATA_NOT_FOUND_EVENT_DATA("PMS_PMP_028","OIDC Client Data is not available in Event data."),
	OIDC_CLIENT_DATA_ALREADY_EXIST("PMS_PMP_029","OIDC Client ID already exist in DB."),
	OIDC_CLIENT_DATA_INVALID_PARTNER("PMS_PMP_030","Not Allowed to change the Partner mapping of OIDC Client."),

	
	// UIN and VID validations
	UIN_VAL_ILLEGAL_LENGTH("IDA-MLC-026", "UIN length should be - %s."),
	UIN_VAL_ILLEGAL_CHECKSUM("IDA-MLC-027", "UIN should match checksum."),
	VID_VAL_ILLEGAL_LENGTH("IDA-MLC-028", "UIN length should be  - %s."),
	VID_VAL_ILLEGAL_CHECKSUM("IDA-MLC-029", "UIN should match checksum."),


	KYC_TOKEN_NOT_FOUND("IDA-KYE-001", "KYC Token not found in Store."),
	KYC_TOKEN_EXPIRED("IDA-KYE-002", "KYC Token Expired."),
	KYC_TOKEN_ALREADY_PROCESSED("IDA-KYE-003", "KYC Token already processed."),
	KYC_TOKEN_INVALID_OIDC_CLIENT_ID("IDA-KYE-004", "KYC Token does not belong to the input oidc client id."),
	KYC_TOKEN_INVALID_TRANSACTION_ID("IDA-KYE-005", "KYC Auth and KYC Exchange transaction ids are different."),
	PARTNER_POLICY_NOT_FOUND("IDA-KYE-006", "Partner Policy not found."),
	KYC_TOKEN_INVALID_UIN_VID("IDA-KYE-007", "KYC Token does not belong to the input UIN/VID."),
	
	ID_KEY_BINDING_NOT_ALLOWED("IDA-IKB-001", "Key Binding not allowed for the Id."),
	CREATE_PUBLIC_KEY_OBJECT_ERROR("IDA-IKB-002", "Error creating Public Key object."),
	PUBLIC_KEY_BINDING_NOT_ALLOWED("IDA-IKB-003", "Publick Key already Binded to another Id."),
	IDENTITY_NAME_NOT_FOUND("IDA-IKB-004", "Identity Name not found."),
	CREATE_CERTIFICATE_OBJECT_ERROR("IDA-IKB-005", "Error creating Certificate object."),
	
	TOKEN_AUTH_IDTYPE_MISMATCH("IDA-TOA-001", "Input Identity Type does not match Identity Type of Token Request"),
	
	KEY_TYPE_NOT_SUPPORT("IDA-VCI-001", "Not Supported JWK Key Type."),
	CREATE_VCI_PUBLIC_KEY_OBJECT_ERROR("IDA-VCI-002", "Error creating Public Key object."),
	KEY_ALREADY_MAPPED_ERROR("IDA-VCI-003", "Error Key already mapped to different id/vid."),
	VCI_NOT_SUPPORTED_ERROR("IDA-VCI-004", "Error VCI not supported."),
	LDP_VC_GENERATION_FAILED("IDA-VCI-005", "Ldp VC generation Failed.");

	
	private final String errorCode;
	private final String errorMessage;
	private String actionMessage;

	/**
	 * Constructor for {@link IdAuthenticationErrorConstants}
	 * 
	 * @param errorCode    - id-usage error codes which follows
	 *                     "<product>-<module>-<component>-<number>" pattern
	 * @param errorMessage - short error message
	 */
	private IdAuthenticationErrorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	private IdAuthenticationErrorConstants(String errorCode, String errorMessage, String actionMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.actionMessage = actionMessage;
	}

	/**
	 * Getter for errorCode
	 * 
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return replaceIdTypeStrWithAlias(errorMessage);
	}

	public String getActionMessage() {
		return replaceIdTypeStrWithAlias(actionMessage);
	}
	
	/**
	 * Replace the occurrences of ID Type to its alias if available.
	 * @param str the string to check 
	 * @return the string which has replacements with alias
	 */
	private String replaceIdTypeStrWithAlias(String str) {
		String s = str;
		if (str != null) {
			for (IdType idType : IdType.values()) {
				Optional<String> alias = idType.getAlias();
				if (alias.isPresent()) {
					String type = idType.getType();
					if (str.contains(type)) {
						s = s.replace(type, alias.get());
					}
				}
			}
		}
		return s;
	}

	public static Optional<String> getActionMessageForErrorCode(String errorCode) {
		return Stream.of(values()).filter(ele -> ele.getErrorCode().equals(errorCode))
				.map(IdAuthenticationErrorConstants::getActionMessage).filter(Objects::nonNull).findAny();
	}

}
