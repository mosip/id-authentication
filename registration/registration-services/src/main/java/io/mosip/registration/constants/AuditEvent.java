package io.mosip.registration.constants;

import static io.mosip.registration.constants.AuditEventType.USER_EVENT;
import static io.mosip.registration.constants.AuditEventType.SYSTEM_EVENT;

/**
 * Enum for Audit Events
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public enum AuditEvent {
	
	// Login
	LOGIN_AUTHENTICATE_USER_ID("REG-EVT-001", USER_EVENT.getCode(), "LOGIN_AUTHENTICATE_USER_ID", "Login authenticating user id: Click of Submit"),
	LOGIN_WITH_PASSWORD("REG-EVT-002", USER_EVENT.getCode(), "LOGIN_WITH_PASSWORD", "Login with password: Click of Submit"),
	LOGIN_GET_OTP("REG-EVT-003", USER_EVENT.getCode(), "LOGIN_GET_OTP", "Login with OTP: Get OTP"),
	LOGIN_SUBMIT_OTP("REG-EVT-004", USER_EVENT.getCode(), "LOGIN_SUBMIT_OTP", "Login with OTP: Submit OTP"),
	LOGIN_RESEND_OTP("REG-EVT-005", USER_EVENT.getCode(), "LOGIN_RESEND_OTP", "Login with OTP: Resend OTP"),
	LOGIN_WITH_FINGERPRINT("REG-EVT-006", USER_EVENT.getCode(), "LOGIN_WITH_FINGERPRINT", "Login with fingerprint: Capture and submit"),
	LOGIN_WITH_IRIS("REG-EVT-007", USER_EVENT.getCode(), "LOGIN_WITH_IRIS", "Login with iris: Capture and submit"),
	LOGIN_WITH_FACE("REG-EVT-008", USER_EVENT.getCode(), "LOGIN_WITH_FACE", "Login with face: Capture and submit"),
	LOGOUT_USER("REG-EVT-009", USER_EVENT.getCode(), "LOGOUT_USER", "Logout"),
	
	// Navigation
	NAV_NEW_REG("REG-EVT-010", USER_EVENT.getCode(), "NAV_NEW_REG", "Click of navigation link: New Registration"),
	NAV_UIN_UPDATE("REG-EVT-011", USER_EVENT.getCode(), "NAV_UIN_UPDATE", "Navigation link: UIN Update"),
	NAV_APPROVE_REG("REG-EVT-012", USER_EVENT.getCode(), "NAV_APPROVE_REG", "Navigation link: Approve Registration"),
	NAV_SYNC_PACKETS("REG-EVT-013", USER_EVENT.getCode(), "NAV_SYNC_PACKETS", "Navigation link: Sync Packet IDs"),
	NAV_UPLOAD_PACKETS("REG-EVT-014", USER_EVENT.getCode(), "NAV_UPLOAD_PACKETS", "Navigation link: Upload Packets"),
	NAV_VIRUS_SCAN("REG-EVT-015", USER_EVENT.getCode(), "NAV_VIRUS_SCAN", "Navigation link: Virus Scan"),
	NAV_SYNC_DATA("REG-EVT-016", USER_EVENT.getCode(), "NAV_SYNC_DATA", "Navigation link: Sync Data"),
	NAV_DOWNLOAD_PRE_REG_DATA("REG-EVT-017", USER_EVENT.getCode(), "NAV_DOWNLOAD_PRE_REG_DATA", "Navigation link: Download Pre-registration Data"),
	NAV_GEO_LOCATION("REG-EVT-018", USER_EVENT.getCode(), "NAV_GEO_LOCATION", "Navigation link: Geo-location"),
	NAV_ON_BOARD_USER("REG-EVT-019", USER_EVENT.getCode(), "NAV_ON_BOARD_USER", "Navigation link: On-board Users"),
	NAV_RE_REGISTRATION("REG-EVT-020", SYSTEM_EVENT.getCode(), "NAV_RE_REGISTRATION", "Navigation link: Re-Registration"),
	NAV_HOME("REG-EVT-021", SYSTEM_EVENT.getCode(), "NAV_HOME", "Navigation link: Home"),
	NAV_REDIRECT_HOME("REG-EVT-022", SYSTEM_EVENT.getCode(), "NAV_REDIRECT_HOME", "Navigation link: Redirect to Home"),
	NAV_ON_BOARD_DEVICES("REG-EVT-023", USER_EVENT.getCode(), "NAV_ON_BOARD_DEVICES", "Navigation link: On-board Devices"),

	// Registration : Demographics Details
	REG_DEMO_PRE_REG_DATA_FETCH("REG-EVT-024", USER_EVENT.getCode(), "REG_DEMO_PRE_REG_DATA_FETCH", "Pre-registration: Fetch data for selected Pre-registration"),
	REG_DEMO_NEXT("REG-EVT-025", USER_EVENT.getCode(), "REG_DEMO_NEXT", "Click of Next after capturing demographic details"),

	// Registration : Documents
	REG_DOC_POA_SCAN("REG-EVT-026", USER_EVENT.getCode(), "REG_DOC_POA_SCAN", "PoA: Click of Scan"),
	REG_DOC_POA_VIEW("REG-EVT-027", USER_EVENT.getCode(), "REG_DOC_POA_VIEW", "PoA: View"),
	REG_DOC_POA_DELETE("REG-EVT-028", USER_EVENT.getCode(), "REG_DOC_POA_DELETE", "PoA: Delete"),
	REG_DOC_POI_SCAN("REG-EVT-029", USER_EVENT.getCode(), "REG_DOC_POI_SCAN", "PoI: Click of Scan"),
	REG_DOC_POI_VIEW("REG-EVT-030", USER_EVENT.getCode(), "REG_DOC_POI_VIEW", "PoI: View"),
	REG_DOC_POI_DELETE("REG-EVT-031", USER_EVENT.getCode(), "REG_DOC_POI_DELETE", "PoI: Delete"),
	REG_DOC_POR_SCAN("REG-EVT-032", USER_EVENT.getCode(), "REG_DOC_POR_SCAN", "PoR: Click of Scan"),
	REG_DOC_POR_VIEW("REG-EVT-033", USER_EVENT.getCode(), "REG_DOC_POR_VIEW", "PoR: View"),
	REG_DOC_POR_DELETE("REG-EVT-034", USER_EVENT.getCode(), "REG_DOC_POR_DELETE", "PoR: Delete"),
	REG_DOC_POB_SCAN("REG-EVT-035", USER_EVENT.getCode(), "REG_DOC_POB_SCAN", "PoB: Click of Scan"),
	REG_DOC_POB_VIEW("REG-EVT-036", USER_EVENT.getCode(), "REG_DOC_POB_VIEW", "PoB: View"),
	REG_DOC_POB_DELETE("REG-EVT-037", USER_EVENT.getCode(), "REG_DOC_POB_DELETE", "PoB: Delete"),
	REG_DOC_POE_SCAN("REG-EVT-147", USER_EVENT.getCode(), "REG_DOC_POB_SCAN", "PoB: Click of Scan"),
	REG_DOC_POE_VIEW("REG-EVT-148", USER_EVENT.getCode(), "REG_DOC_POB_VIEW", "PoB: View"),
	REG_DOC_POE_DELETE("REG-EVT-149", USER_EVENT.getCode(), "REG_DOC_POB_DELETE", "PoB: Delete"),
	REG_DOC_NEXT("REG-EVT-038", USER_EVENT.getCode(), "REG_DOC_NEXT", "Click of Next after uploading documents"),
	REG_DOC_BACK("REG-EVT-039", USER_EVENT.getCode(), "REG_DOC_BACK", "Click of Back to demographic details"),

	// Registration: Biometrics
	REG_BIO_EXCEPTION_MARKING("REG-EVT-040", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_MARKING", "Biometric Exceptions: Marking"),
	REG_BIO_EXCEPTION_NEXT("REG-EVT-041", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_NEXT", "Click of Next after marking exceptions"),
	REG_BIO_EXCEPTION_BACK("REG-EVT-042", USER_EVENT.getCode(), "REG_BIO_EXCEPTION_BACK", "Click of Back from biometric exception capture screen"),
	REG_BIO_LEFT_SLAP_SCAN("REG-EVT-043", USER_EVENT.getCode(), "REG_BIO_LEFT_SLAP_SCAN", "Fingerprints: Scan of left slap"),
	REG_BIO_RIGHT_SLAP_SCAN("REG-EVT-044", USER_EVENT.getCode(), "REG_BIO_RIGHT_SLAP_SCAN", "Fingerprints: Scan of right slap"),
	REG_BIO_THUMBS_SCAN("REG-EVT-045", USER_EVENT.getCode(), "REG_BIO_THUMBS_SCAN", "Fingerprints: Scan of thumbs"),
	REG_BIO_FINGERPRINT_NEXT("REG-EVT-046", USER_EVENT.getCode(), "REG_BIO_FINGERPRINT_NEXT", "Click of Next after capturing fingerprints"),
	REG_BIO_FINGERPRINT_BACK("REG-EVT-047", USER_EVENT.getCode(), "REG_BIO_FINGERPRINT_BACK", "Click of Back from fingerprint capture screen"),
	REG_BIO_LEFT_IRIS_SCAN("REG-EVT-048", USER_EVENT.getCode(), "REG_BIO_IRIS_SCAN", "Iris: Scan of left iris"),
	REG_BIO_RIGHT_IRIS_SCAN("REG-EVT-143", USER_EVENT.getCode(), "REG_BIO_IRIS_SCAN", "Iris: Scan of right iris"),
	REG_BIO_IRIS_NEXT("REG-EVT-049", USER_EVENT.getCode(), "REG_BIO_IRIS_NEXT", "Click of Next after capturing irises"),
	REG_BIO_IRIS_BACK("REG-EVT-050", USER_EVENT.getCode(), "REG_BIO_IRIS_BACK", "Click of Back from iris capture screen"),
	REG_BIO_FACE_CAPTURE("REG-EVT-051", USER_EVENT.getCode(), "REG_BIO_FACE_CAPTURE", "Photo: Face capture"),
	REG_BIO_EXCEP_FACE_CAPTURE("REG-EVT-144", USER_EVENT.getCode(), "REG_BIO_EXCEPION_FACE_CAPTURE", "Photo: Exception face capture"),
	REG_BIO_FACE_CAPTURE_NEXT("REG-EVT-052", USER_EVENT.getCode(), "REG_BIO_FACE_CAPTURE_NEXT", "Click of Next after capturing face photo"),
	REG_BIO_FACE_CAPTURE_BACK("REG-EVT-053", USER_EVENT.getCode(), "REG_BIO_FACE_CAPTURE_BACK", "Click of Back from face photo capture screen"),

	// Registration Preview
	REG_PREVIEW_DEMO_EDIT("REG-EVT-054", USER_EVENT.getCode(), "REG_PREVIEW_DEMO_EDIT", "Click of Edit demographics"),
	REG_PREVIEW_DOC_EDIT("REG-EVT-055", USER_EVENT.getCode(), "REG_PREVIEW_DOC_EDIT", "Click of Edit documents"),
	REG_PREVIEW_BIO_EDIT("REG-EVT-056", USER_EVENT.getCode(), "REG_PREVIEW_BIO_EDIT", "Click of Biometrics Edit"),
	REG_PREVIEW_SUBMIT("REG-EVT-057", USER_EVENT.getCode(), "REG_PREVIEW_SUBMIT", "Submit"),
	REG_PREVIEW_BACK("REG-EVT-140", USER_EVENT.getCode(), "REG_PREVIEW_BACK", "Click of Back from registration preview screen"),

	// Registration: Operator/Supervisor Authentication
	REG_OPERATOR_AUTH_PASSWORD("REG-EVT-058", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PASSWORD", "Operator authentication with password: Click of Submit"),
	REG_OPERATOR_AUTH_GET_OTP("REG-EVT-059", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_GET_OTP", "Operator authentication with OTP: Get OTP"),
	REG_OPERATOR_AUTH_SUBMIT_OTP("REG-EVT-060", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_SUBMIT_OTP", "Operator authentication with OTP: Submit OTP"),
	REG_OPERATOR_AUTH_RESEND_OTP("REG-EVT-061", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_RESEND_OTP", "Operator authentication with OTP: Resend OTP"),
	REG_OPERATOR_AUTH_FINGERPRINT("REG-EVT-062", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_FINGERPRINT", "Operator authentication with fingerprint: Capture and submit"),
	REG_OPERATOR_AUTH_IRIS("REG-EVT-063", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_IRIS", "Operator authentication with iris: Capture and submit"),
	REG_OPERATOR_AUTH_FACE("REG-EVT-064", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_FACE", "Operator authentication with face: Capture and submit"),
	REG_OPERATOR_AUTH_PREVIEW("REG-EVT-065", USER_EVENT.getCode(), "REG_OPERATOR_AUTH_PREVIEW", "Back to Preview"),
	REG_ACK_PRINT("REG-EVT-066", USER_EVENT.getCode(), "REG_ACK_PRINT", "Print receipt"),
	REG_SUPERVISOR_AUTH_PASSWORD("REG-EVT-067", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_PASSWORD", "Supervisor authentication with password: Click of Submit"),
	REG_SUPERVISOR_AUTH_GET_OTP("REG-EVT-068", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_GET_OTP", "Supervisor authentication with OTP: Get OTP"),
	REG_SUPERVISOR_AUTH_SUBMIT_OTP("REG-EVT-069", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_SUBMIT_OTP", "Supervisor authentication with OTP: Submit OTP"),
	REG_SUPERVISOR_AUTH_RESEND_OTP("REG-EVT-070", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_RESEND_OTP", "Supervisor authentication with OTP: Resend OTP"),
	REG_SUPERVISOR_AUTH_FINGERPRINT("REG-EVT-071", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_FINGERPRINT", "Supervisor authentication with fingerprint: Capture and submit"),
	REG_SUPERVISOR_AUTH_IRIS("REG-EVT-072", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_IRIS", "Supervisor authentication with iris: Capture and submit"),
	REG_SUPERVISOR_AUTH_FACE("REG-EVT-073", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_FACE", "Supervisor authentication with face: Capture and submit"),
	REG_SUPERVISOR_AUTH_PREVIEW("REG-EVT-074", USER_EVENT.getCode(), "REG_SUPERVISOR_AUTH_PREVIEW", "Back to Preview"),

	// Approve Registration
	APPR_VIEW_REG("REG-EVT-075", USER_EVENT.getCode(), "APPR_VIEW_REG", "View registration detail"),
	APPR_REG("REG-EVT-076", USER_EVENT.getCode(), "APPR_REG", "Approve registration"),
	REJECT_REG("REG-EVT-077", USER_EVENT.getCode(), "REJECT_REG", "Reject registration"),

	// Sync Packet Ids
	SYNC_PACKET_IDS("REG-EVT-078", SYSTEM_EVENT.getCode(), "SYNC_PACKET_IDS", "Send Packet IDs to server"),

	// Upload Packets
	UPLOAD_PACKET("REG-EVT-079", SYSTEM_EVENT.getCode(), "UPLOAD_PACKET", "Upload packets"),

	// Virus Scan
	VIRUS_SCAN_REG_CLIENT("REG-EVT-080", USER_EVENT.getCode(), "VIRUS_SCAN_REG_CLIENT", "Scan registration client"),
	VIRUS_SCAN_REG_PACKETS("REG-EVT-081", USER_EVENT.getCode(), "VIRUS_SCAN_REG_PACKETS", "Scan registration packets"),

	// Geo-Location
	GEO_LOCATION_CAPTURE("REG-EVT-082", SYSTEM_EVENT.getCode(), "GEO_LOCATION_CAPTURE", "Capture geo-location"),

	// On-Board Users
	ON_BOARD_USER("REG-EVT-083", USER_EVENT.getCode(), "ON_BOARD_USER", "On-board user"),
	DE_BOARD_MAPPED_USER("REG-EVT-084", USER_EVENT.getCode(), "DE_BOARD_MAPPED_USER", "Deactivate user mapping"),
	ACTIVATE_MAPPED_USER("REG-EVT-085", USER_EVENT.getCode(), "ACTIVATE_MAPPED_USER", "Activate user mapping"),
	DELETE_ON_BOARDER_USER("REG-EVT-086", USER_EVENT.getCode(), "DELETE_ON_BOARDER_USER", "Delete user mapping"),

	// Server To Client Sync
	SYNC_MASTER_DATA("REG-EVT-087", USER_EVENT.getCode(), "SYNC_MASTER_DATA", "Sync master data"),
	SYNC_REGISTRATION_CENTER_DETAILS("REG-EVT-088", USER_EVENT.getCode(), "SYNC_REGISTRATION_CENTER_DETAILS", "Sync registration centre details"),
	SYNC_MACHINE_DETAILS("REG-EVT-089", USER_EVENT.getCode(), "SYNC_MACHINE_DETAILS", "Sync machine details"),
	SYNC_DEVICE_DETAILS("REG-EVT-090", USER_EVENT.getCode(), "SYNC_DEVICE_DETAILS", "Sync device details"),
	SYNC_USER_DETAILS("REG-EVT-091", USER_EVENT.getCode(), "SYNC_USER_DETAILS", "Sync user details"),
	SYNC_REGISTRATION_PACKET_STATUS("REG-EVT-092", USER_EVENT.getCode(), "SYNC_REGISTRATION_PACKET_STATUS", "Sync registration packet status"),
	SYNC_PRE_REGISTRATION_PACKET("REG-EVT-093", USER_EVENT.getCode(), "SYNC_PRE_REGISTRATION_PACKET", "Sync pre-registration data"),

	// Client To Server Sync
	SYNC_USER_MAPPING("REG-EVT-094", USER_EVENT.getCode(), "SYNC_USER_MAPPING", "Sync user mapping"),
	SYNC_DEVICE_MAPPING("REG-EVT-095", USER_EVENT.getCode(), "SYNC_DEVICE_MAPPING", "Sync device mapping"),
	SYNC_CLIENT_STATE("REG-EVT-096", USER_EVENT.getCode(), "SYNC_CLIENT_STATE", "Sync client state"),

	// Export Packets
	EXPORT_REG_PACKETS("REG-EVT-097", USER_EVENT.getCode(), "EXPORT_REGISTRATION_PACKETS", "Export Packets: To external device"),

	// Registration Packet Creation
	PACKET_CREATION_SUCCESS("REG-EVT-098", USER_EVENT.getCode(), "PACKET_CREATION_SUCCESS", "Packet Succesfully Created"),
	PACKET_ENCRYPTED("REG-EVT-099", USER_EVENT.getCode(), "PACKET_ENCRYPTED", "Packet Encrypted Sucessfully"),
	PACKET_UPLOADED("REG-EVT-100", USER_EVENT.getCode(), "PACKET_UPLOADED", "Packet Uploaded Successfully"),
	PACKET_SYNCED_TO_SERVER("REG-EVT-101", USER_EVENT.getCode(), "PACKET_SYNCED_TO_SERVER", "Packet Synced to Server Sucesfully"),
	PACKET_DELETED("REG-EVT-102", USER_EVENT.getCode(), "PACKET_DELETED", "Packet Deleted Successfully"),
	PACKET_APPROVED("REG-EVT-103", USER_EVENT.getCode(), "PACKET_APPROVED", "Packet Approved Successfully"),
	PACKET_REJECTED("REG-EVT-104", USER_EVENT.getCode(), "PACKET_REJECTED", "Packet Rejected Successfully"),
	PACKET_HOLDED("REG-EVT-105", USER_EVENT.getCode(), "PACKET_HOLDED","Packet Holded for particular reason"),
	PACKET_INTERNAL_ERROR("REG-EVT-106", USER_EVENT.getCode(), "PACKET_INTERNAL_ERROR", "Packet Creation Error"),
	PACKET_INTERNAL_ZIP("REG-EVT-107", USER_EVENT.getCode(), "PACKET_INTERNAL_ZIP", "Packet internally zipped successfully"),
	PACKET_DEMO_JSON_CREATED("REG-EVT-108", USER_EVENT.getCode(), "PACKET_DEMO_JSON_CREATED", "Packet Demographic JSON created successfully"),
	PACKET_HMAC_FILE_CREATED("REG-EVT-109", USER_EVENT.getCode(), "PACKET_HMAC_FILE_CREATED", "Packet HMAC File created successfully"),
	PACKET_META_JSON_CREATED("REG-EVT-110", USER_EVENT.getCode(), "PACKET_META_JSON_CREATED", "Packet Meta-Data JSON created successfully"),
	PACKET_AUDIT_JSON_CREATED("REG-EVT-111", USER_EVENT.getCode(), "PACKET_AUDIT_JSON_CREATED", "Packet Audit JSON created successfully"),
	PACKET_AES_ENCRYPTED("REG-EVT-112", USER_EVENT.getCode(), "PACKET_AES_ENCRYPTED","Packet Encrypted Sucessfully"),

	// Scheduler
	UI_SCHEDULER_STARTED("REG-EVT-113", USER_EVENT.getCode(), "UI_SCHEDULER_STARTED","Scheduler started Sucessfully"),

	//Login and User related
	LOGIN_MODES_FETCH("REG-EVT-114", USER_EVENT.getCode(), "LOGIN_MODES_FETCH", "Fetching Login Modes"),
	USER_STATUS_FETCH("REG-EVT-115", USER_EVENT.getCode(), "USER_STATUS_FETCH", "Fetching User Status"),
	VALIDATE_USER_CRED("REG-EVT-116", USER_EVENT.getCode(), "VALIDATE_USER_CREDENTIALS", "Validating User credentials"),
	FETCH_USR_DET("REG-EVT-117", USER_EVENT.getCode(), "FETCH_USER_DETAILS", "Fetching User Details"),
	FETCH_CNTR_NAME("REG-EVT-118", USER_EVENT.getCode(), "FETCH_CENTER_NAME", "Fetching Center Name"),
	FETCH_CNTR_DET("REG-EVT-119", USER_EVENT.getCode(), "FETCH_CENTER_DETAILS", "Fetching Center Details"),
	FETCH_USR_ROLE("REG-EVT-120", USER_EVENT.getCode(), "FETCH_USER_ROLE", "Fetching User Roles"),
	FETCH_SCR_AUTH("REG-EVT-121", USER_EVENT.getCode(), "FETCH_SCREEN_AUTH", "Fetching screens to be authorized"),
	SYNCJOB_INFO_FETCH("REG-EVT-122", USER_EVENT.getCode(), "SYNC_JOB_INFO_FETCH", "SyncJobInfo containing the sync control list and yet to export packet count fetched successfully"),
	SYNC_INFO_VALIDATE("REG-EVT-123", USER_EVENT.getCode(), "SYNC_INFO_VALIDATION", "Validating the sync status ended successfully"),
	SYNC_PKT_COUNT_VALIDATE("REG-EVT-124", USER_EVENT.getCode(), "SYNC_PKT_COUNT_VALIDATION", "Validating yet to export packets frequency with the configured limit count"),
	PACKET_RETRIVE("REG-EVT-125", USER_EVENT.getCode(), "PACKET_RETRIVE", "Packets which are in created state for approval are retrived"),
	PACKET_UPDATE("REG-EVT-126", USER_EVENT.getCode(), "PACKET_UPDATE", "Packets which are in created state are updated"),
	SYNC_GEO_VALIDATE("REG-EVT-127", USER_EVENT.getCode(), "SYNC_GEO_VALIDATE", "Validating the geo information ended successfully"),
	PENDING_PKT_CNT_VALIDATE("REG-EVT-128", USER_EVENT.getCode(), "PENDING_PKT_COUNT_VALIDATION", "Validating the Pending packets count"),
	PENDING_PKT_DUR_VALIDATE("REG-EVT-129", USER_EVENT.getCode(), "PENDING_PKT_DURATION_VALIDATION", "Validating the Pending packets Duration"),
	DEVICE_MAPPING_SUCCESS("REG-EVT-130", USER_EVENT.getCode(), "DEVICE_MAPPING_SUCCESS","device mapped successfully"),
	DEVICE_UN_MAPPING("REG-EVT-131", USER_EVENT.getCode(), "DEVICE_UN_MAPPING", "Device is unmapped "),
	
	//Sync Packets
	SYNC_SERVER("REG-EVT-132", USER_EVENT.getCode(), "SYNC_SERVER", "Synchronize the packet status to the server"),
	
	//Packet Upload
	PACKET_UPLOAD("REG-EVT-133", USER_EVENT.getCode(), "PACKET_UPLOAD", "Upload the local packets to the server"),
		
	// Notification Service
	NOTIFICATION_STATUS("REG-EVT-137", USER_EVENT.getCode(), "NOTIFICATION_SERVICE", "Notification request status"),
	
	// Registration Audits
	GET_REGISTRATION_CONTROLLER("REG-EVT-138", USER_EVENT.getCode(),"GET_REGISTRATION_CONTROLLER", "Initializing the registration controller"),
	SAVE_DETAIL_TO_DTO("REG-EVT-139", USER_EVENT.getCode(), "SAVE_DETAIL_TO_DTO", "Saving the details to DTO"),
	MACHINE_REMAPPED("REG-EVT-140", USER_EVENT.getCode(),"CENTER_MACHINE_REMAP_SERVICE","machine is remapped to another center"),
	
	//Scheduler Util
	SCHEDULER_REFRESHED_TIMEOUT("REG-EVT-141", SYSTEM_EVENT.getCode(),"REFRESHED_TIMEOUT", "The time task remainder alert"),
	SCHEDULER_SESSION_TIMEOUT("REG-EVT-142", SYSTEM_EVENT.getCode(),"SESSION_TIMEOUT", "The time task session expires"),
	
	//MDM 
	MDM_CAPTURE_FAILED("REG-EVT-143", USER_EVENT.getCode(), "CAPTURE_FAILED", "Biometric capture failed"),
	MDM_CAPTURE_SUCCESS("REG-EVT-144", USER_EVENT.getCode(), "CAPTURE_SUCESS", "Biometric capture completed"),
	MDM_NO_DEVICE_AVAILABLE("REG-EVT-145", USER_EVENT.getCode(), "DEVICE_NOT_FOUND", "No devic3 is available"),
	MDM_DEVICE_FOUND("REG-EVT-146", USER_EVENT.getCode(), "MDM_DEVICE_FOUND", "Device is found");
	
	/**
	 * The constructor
	 */
	private AuditEvent(String id, String type, String name, String description) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
	}
	
	private final String id;
	private final String type;
	private final String name;
	private final String description;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}
