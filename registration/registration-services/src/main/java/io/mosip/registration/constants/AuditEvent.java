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

	PACKET_CREATION_SUCCESS("PKT_CRT", "Success", "Packet Succesfully Created"),
	PACKET_ENCRYPTED("PKT_ENC", "Encrypted","Packet Encrypted Sucessfully"),
	PACKET_UPLOADED("PKT_UPL", "Uploaded","Packet Uploaded Successfully"),
	PACKET_SYNCED_TO_SERVER("PKT_SYN", "Synced to Server","Packet Synced to Server Sucesfully"),
	PACKET_DELETED("PKT_DLT", "Deleted","Packet Deleted Successfully"),
	PACKET_APPROVED("PKT_APR", "Approved","Packet Approved Successfully"),
	PACKET_REJECTED("PKT_RJC", "Rejected","Packet Rejected Successfully"),
	PACKET_HOLDED("PKT_HLD", "Holded","Packet Holded for particular reason"),
	PACKET_INTERNAL_ERROR("PKT_INE", "Internal Error", "Packet Creation Error"),
	PACKET_INTERNAL_ZIP("PKT_ZIP", "Internal Packet Zip", "Packet internally zipped successfully"),
	PACKET_DEMO_JSON_CREATED("PKT_DGJ", "Demographic JSON", "Packet Demographic JSON created successfully"),
	PACKET_HMAC_FILE_CREATED("PKT_HMF", "HMAC File", "Packet HMAC File created successfully"),
	PACKET_META_JSON_CREATED("PKT_MDJ", "Packet Meta Data JSON", "Packet Meta-Data JSON created successfully"),
	PACKET_AUDIT_JSON_CREATED("PKT_AUD", "Packet Audit JSON", "Packet Audit JSON created successfully"),
	PACKET_AES_ENCRYPTED("PKT_AES", "AES Encrypted","Packet Encrypted Sucessfully"),
	UI_SCHEDULER_STARTED("PKT_SCH", "Scheduler","Scheduler started Sucessfully"),
	//Login and User related
	LOGIN_MODES_FETCH("LGN_MOD","Login Modes","Fetching Login Modes"),
	USER_STATUS_FETCH("USR_STS","User Status","Fetching User Status"),
	VALIDATE_USER_CRED("VLD_USR","Validate User","Validating User credentials"),
	FETCH_USR_DET("USR_DTL","User Detail","Fetching User Details"),
	FETCH_CNTR_NAME("CNT_NME","Center Name","Fetching Center Name"),
	FETCH_CNTR_DET("CNT_DET","Center Details","Fetching Center Details"),
	FETCH_USR_ROLE("USR_RLE","User Roles","Fetching User Roles"),
	FETCH_SCR_AUTH("SCR_ATH","Screen AUthorization","Fetching screens to be authorized"),
	SYNCJOB_INFO_FETCH("SYNC_INFO", "Sync Information", "SyncJobInfo containing the synccontrol list and yettoexportpacket count fetched successfully"),
	SYNC_INFO_VALIDATE("SYNC_INFV", "Sync Information validation", "Validating the sync status ended successfully"),
	SYNC_PKT_COUNT_VALIDATE("SYNC_PKT_CNT", "Sync Packet Count validation", "Validating yet to export packets frequency with the configured limit count"),
	PACKET_RETRIVE("PKT_RETRIVE", "Retiving created packets", "Packets which are in created state for approval are retrived"),
	PACKET_UPDATE("PKT_UPDATE", "Updating created packets status", "Packets which are in created state are updated"),
	SYNC_GEO_VALIDATE("SYNC_INFO", "Geo Information validation", "Validating the geo information ended successfully"),
	PENDING_PKT_CNT_VALIDATE("PACKET_INFO", "Pending Packet Count validation", "Validating the Pending packets count"),
	PENDING_PKT_DUR_VALIDATE("PACKET_INFO", "Pending Packet Duration validation", "Validating the Pending packets Duration"),
	DEVICE_MAPPING_SUCCESS("CMD_MAP","Success","device mapped successfully"),
	DEVICE_UN_MAPPING("CMD_UMAP","Success","device is unmapped "),
	
	//Sync Packets
	SYNC_SERVER("SYNC_INFO", "User", "Synchronize the packet status to the server"),
	
	//Packet Upload
	PACKET_UPLOAD("PKT_UPLD", "User", "Upload the local packets to the server"),
	
	// Device Onboarding
	GET_ONBOARDING_DEVICES_TYPES("DVC_TYP", "Get Devices Types", "Get the types of devices for device onboarding"),
	GET_ONBOARDING_DEVICES("GET_DVC", "Get Devices", "Get the available and mapped devices for the requested device type"),
	UPDATE_DEVICES_ONBOARDING("UPD_DVC", "Update Devices", "Devices onboarding updated for the registration client"),
	// Notification Service
	// Notification Service
	NOTIFICATION_STATUS("NOT_SER", "Notification SERVICE", "Notification request status"),
	
	// Registration Audits
	GET_REGISTRATION_CONTROLLER("REG_INI","Registration initialization","Initializing the registration controller"),
	SAVE_DETAIL_TO_DTO("SAVE","Save to DTO","Saving the details to DTO"),
	
	// Login
	LOGIN_AUTHENTICATE_USER_ID("LOG", USER_EVENT.getCode(), "Login authenticating user id: Click of Submit"),
	LOGIN_WITH_PASSWORD("LOG", USER_EVENT.getCode(), "Login with password: Click of Submit"),
	LOGIN_GET_OTP("LOG", USER_EVENT.getCode(), "Login with OTP: Get OTP"),
	LOGIN_SUBMIT_OTP("LOG", USER_EVENT.getCode(), "Login with OTP: Submit OTP"),
	LOGIN_RESEND_OTP("LOG", USER_EVENT.getCode(), "Login with OTP: Resend OTP"),
	LOGIN_WITH_FINGERPRINT("LOG", USER_EVENT.getCode(), "Login with fingerprint: Capture and submit"),
	LOGIN_WITH_IRIS("LOG", USER_EVENT.getCode(), "Login with iris: Capture and submit"),
	LOGIN_WITH_FACE("LOG", USER_EVENT.getCode(), "Login with face: Capture and submit"),
	LOGOUT_USER("LOG", USER_EVENT.getCode(), "Logout"),
	
	// Navigation
	NAV_NEW_REG("NAV", USER_EVENT.getCode(), "Click of navigation link: New Registration"),
	NAV_UIN_UPDATE("NAV", USER_EVENT.getCode(), "Navigation link: UIN Update"),
	NAV_APPROVE_REG("NAV", USER_EVENT.getCode(), "Navigation link: Approve Registration"),
	NAV_SYNC_PACKETS("NAV", USER_EVENT.getCode(), "Navigation link: Sync Packet IDs"),
	NAV_UPLOAD_PACKETS("NAV", USER_EVENT.getCode(), "Navigation link: Upload Packets"),
	NAV_VIRUS_SCAN("NAV", USER_EVENT.getCode(), "Navigation link: Virus Scan"),
	NAV_SYNC_DATA("NAV", USER_EVENT.getCode(), "Navigation link: Sync Data"),
	NAV_DOWNLOAD_PRE_REG_DATA("NAV", USER_EVENT.getCode(), "Navigation link: Download Pre-registration Data"),
	NAV_GEO_LOCATION("NAV", USER_EVENT.getCode(), "Navigation link: Geo-location"),
	NAV_ON_BOARD_USER("NAV", USER_EVENT.getCode(), "Navigation link: On-board Users"),
	NAV_RE_REGISTRATION("NAV", SYSTEM_EVENT.getCode(), "Navigation link: Re-Registration"),
	NAV_HOME("NAV", SYSTEM_EVENT.getCode(), "Navigation link: Home"),
	NAV_REDIRECT_HOME("NAV", SYSTEM_EVENT.getCode(), "Navigation link: Redirect to Home"),
	NAV_ON_BOARD_DEVICES("NAV", USER_EVENT.getCode(), "Navigation link: On-board Devices"),

	// Registration : Demographics Details
	REG_DEMO_PRE_REG_DATA_FETCH("REG", USER_EVENT.getCode(), "Pre-registration: Fetch data for selected Pre-registration"),
	REG_DEMO_NEXT("REG", USER_EVENT.getCode(), "Click of Next after capturing demographic details"),

	// Registration : Documents
	REG_DOC_POA_SCAN("REG", USER_EVENT.getCode(), "PoA: Click of Scan"),
	REG_DOC_POA_VIEW("REG", USER_EVENT.getCode(), "PoA: View"),
	REG_DOC_POA_DELETE("REG", USER_EVENT.getCode(), "PoA: Delete"),
	REG_DOC_POI_SCAN("REG", USER_EVENT.getCode(), "PoI: Click of Scan"),
	REG_DOC_POI_VIEW("REG", USER_EVENT.getCode(), "PoI: View"),
	REG_DOC_POI_DELETE("REG", USER_EVENT.getCode(), "PoI: Delete"),
	REG_DOC_POR_SCAN("REG", USER_EVENT.getCode(), "PoR: Click of Scan"),
	REG_DOC_POR_VIEW("REG", USER_EVENT.getCode(), "PoR: View"),
	REG_DOC_POR_DELETE("REG", USER_EVENT.getCode(), "PoR: Delete"),
	REG_DOC_POB_SCAN("REG", USER_EVENT.getCode(), "PoB: Click of Scan"),
	REG_DOC_POB_VIEW("REG", USER_EVENT.getCode(), "PoB: View"),
	REG_DOC_POB_DELETE("REG", USER_EVENT.getCode(), "PoB: Delete"),
	REG_DOC_NEXT("REG", USER_EVENT.getCode(), "Click of Next after uploading documents"),
	REG_DOC_BACK("REG", USER_EVENT.getCode(), "Click of Back to demographic details"),

	// Registration: Biometrics
	REG_BIO_EXCEPTION_MARKING("REG", USER_EVENT.getCode(), "Biometric Exceptions: Marking"),
	REG_BIO_EXCEPTION_NEXT("REG", USER_EVENT.getCode(), "Click of Next after marking exceptions"),
	REG_BIO_EXCEPTION_BACK("REG", USER_EVENT.getCode(), "Click of Back from biometric exception capture screen"),
	REG_BIO_LEFT_SLAP_SCAN("REG", USER_EVENT.getCode(), "Fingerprints: Scan of left slap"),
	REG_BIO_RIGHT_SLAP_SCAN("REG", USER_EVENT.getCode(), "Fingerprints: Scan of right slap"),
	REG_BIO_THUMBS_SCAN("REG", USER_EVENT.getCode(), "Fingerprints: Scan of thumbs"),
	REG_BIO_FINGERPRINT_NEXT("REG", USER_EVENT.getCode(), "Click of Next after capturing fingerprints"),
	REG_BIO_FINGERPRINT_BACK("REG", USER_EVENT.getCode(), "Click of Back from fingerprint capture screen"),
	REG_BIO_IRIS_SCAN("REG", USER_EVENT.getCode(), "Iris: Scan of both irises"),
	REG_BIO_IRIS_NEXT("REG", USER_EVENT.getCode(), "Click of Next after capturing irises"),
	REG_BIO_IRIS_BACK("REG", USER_EVENT.getCode(), "Click of Back from iris capture screen"),
	REG_BIO_FACE_CAPTURE("REG", USER_EVENT.getCode(), "Photo: Face capture"),
	REG_BIO_FACE_CAPTURE_NEXT("REG", USER_EVENT.getCode(), "Click of Next after capturing face photo"),
	REG_BIO_FACE_CAPTURE_BACK("REG", USER_EVENT.getCode(), "Click of Back from face photo capture screen"),

	// Registration Preview
	REG_PREVIEW_DEMO_EDIT("REG", USER_EVENT.getCode(), "Click of Edit demographics"),
	REG_PREVIEW_DOC_EDIT("REG", USER_EVENT.getCode(), "Click of Edit documents"),
	REG_PREVIEW_BIO_EDIT("REG", USER_EVENT.getCode(), "Click of Biometrics Edit"),
	REG_PREVIEW_SUBMIT("REG", USER_EVENT.getCode(), "Submit"),

	// Registration: Operator/Supervisor Authentication
	REG_OPERATOR_AUTH_PASSWORD("REG", USER_EVENT.getCode(), "Operator authentication with password: Click of Submit"),
	REG_OPERATOR_AUTH_GET_OTP("REG", USER_EVENT.getCode(), "Operator authentication with OTP: Get OTP"),
	REG_OPERATOR_AUTH_SUBMIT_OTP("REG", USER_EVENT.getCode(), "Operator authentication with OTP: Submit OTP"),
	REG_OPERATOR_AUTH_RESEND_OTP("REG", USER_EVENT.getCode(), "Operator authentication with OTP: Resend OTP"),
	REG_OPERATOR_AUTH_FINGERPRINT("REG", USER_EVENT.getCode(), "Operator authentication with fingerprint: Capture and submit"),
	REG_OPERATOR_AUTH_IRIS("REG", USER_EVENT.getCode(), "Operator authentication with iris: Capture and submit"),
	REG_OPERATOR_AUTH_FACE("REG", USER_EVENT.getCode(), "Operator authentication with face: Capture and submit"),
	REG_OPERATOR_AUTH_PREVIEW("REG", USER_EVENT.getCode(), "Back to Preview"),
	REG_ACK_PRINT("REG", USER_EVENT.getCode(), "Print receipt"),
	REG_SUPERVISOR_AUTH_PASSWORD("REG", USER_EVENT.getCode(), "Supervisor authentication with password: Click of Submit"),
	REG_SUPERVISOR_AUTH_GET_OTP("REG", USER_EVENT.getCode(), "Supervisor authentication with OTP: Get OTP"),
	REG_SUPERVISOR_AUTH_SUBMIT_OTP("REG", USER_EVENT.getCode(), "Supervisor authentication with OTP: Submit OTP"),
	REG_SUPERVISOR_AUTH_RESEND_OTP("REG", USER_EVENT.getCode(), "Supervisor authentication with OTP: Resend OTP"),
	REG_SUPERVISOR_AUTH_FINGERPRINT("REG", USER_EVENT.getCode(), "Supervisor authentication with fingerprint: Capture and submit"),
	REG_SUPERVISOR_AUTH_IRIS("REG", USER_EVENT.getCode(), "Supervisor authentication with iris: Capture and submit"),
	REG_SUPERVISOR_AUTH_FACE("REG", USER_EVENT.getCode(), "Supervisor authentication with face: Capture and submit"),
	REG_SUPERVISOR_AUTH_PREVIEW("REG", USER_EVENT.getCode(), "Back to Preview"),

	// Approve Registration
	APPR_VIEW_REG("REG", USER_EVENT.getCode(), "View registration detail"),
	APPR_REG("REG", USER_EVENT.getCode(), "Approve registration"),
	REJECT_REG("REG", USER_EVENT.getCode(), "Reject registration"),

	// Sync Packet Ids
	SYNC_PACKET_IDS("REG", SYSTEM_EVENT.getCode(), "Send Packet IDs to server"),

	// Upload Packets
	UPLOAD_PACKET("REG", SYSTEM_EVENT.getCode(), "Upload packets"),

	// Virus Scan
	VIRUS_SCAN_REG_CLIENT("REG", USER_EVENT.getCode(), "Scan registration client"),
	VIRUS_SCAN_REG_PACKETS("REG", USER_EVENT.getCode(), "Scan registration packets"),

	// Geo-Location
	GEO_LOCATION_CAPTURE("REG", SYSTEM_EVENT.getCode(), "Capture geo-location"),

	// On-Board Users
	ON_BOARD_USER("REG", USER_EVENT.getCode(), "On-board user"),
	DE_BOARD_MAPPED_USER("REG", USER_EVENT.getCode(), "Deactivate user mapping"),
	ACTIVATE_MAPPED_USER("REG", USER_EVENT.getCode(), "Activate user mapping"),
	DELETE_ON_BOARDER_USER("REG", USER_EVENT.getCode(), "Delete user mapping"),

	// Server To Client Sync
	SYNC_MASTER_DATA("REG", USER_EVENT.getCode(), "Sync master data"),
	SYNC_REGISTRATION_CENTER_DETAILS("REG", USER_EVENT.getCode(), "Sync registration centre details"),
	SYNC_MACHINE_DETAILS("REG", USER_EVENT.getCode(), "Sync machine details"),
	SYNC_DEVICE_DETAILS("REG", USER_EVENT.getCode(), "Sync device details"),
	SYNC_USER_DETAILS("REG", USER_EVENT.getCode(), "Sync user details"),
	SYNC_REGISTRATION_PACKET_STATUS("REG", USER_EVENT.getCode(), "Sync registration packet status"),
	SYNC_PRE_REGISTRATION_PACKET("REG", USER_EVENT.getCode(), "Sync pre-registration data"),

	// Client To Server Sync
	SYNC_USER_MAPPING("REG", USER_EVENT.getCode(), "Sync user mapping"),
	SYNC_DEVICE_MAPPING("REG", USER_EVENT.getCode(), "Sync device mapping"),
	SYNC_CLIENT_STATE("REG", USER_EVENT.getCode(), "Sync client state"),

	// Export Packets
	EXPORT_REG_PACKETS("REG", USER_EVENT.getCode(), "Export Packets: To external device");
	
	/**
	 * The constructor
	 */
	private AuditEvent(String id, String type, String name) {
		this.id = id;
		this.type = type;
		this.name = name;
	}
	
	private final String id;
	private final String type;
	private final String name;
	
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
}
