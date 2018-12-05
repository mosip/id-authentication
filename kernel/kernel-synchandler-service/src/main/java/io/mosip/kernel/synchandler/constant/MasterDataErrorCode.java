package io.mosip.kernel.synchandler.constant;

public enum MasterDataErrorCode {
	LAST_UPDATED_PARSE_EXCEPTION("KER_SYNC-100","Erro occurred while parsing lastUpdated timesatamp"),
	APPLICATION_FETCH_EXCEPTION("KER-SYNC-101","Error occurred while fetching Applications"), 
	MACHINE_DETAIL_FETCH_EXCEPTION("KER-SYNC-102","Error occurred while fetching Machine Details"), 
	MACHINE_REG_CENTER_FETCH_EXCEPTION("KER-SYNC-103","Error occurred while fetching Machine Registration Center"), 
	REG_CENTER_FETCH_EXCEPTION("KER-SYNC-104","Error occurred while fetching Registration Center"), 
	REG_CENTER_TYPE_FETCH_EXCEPTION("KER-SYNC-105", "Error occurred while fetching Registration Center Type"),
	TEMPLATE_FETCH_EXCEPTION("KER-SYNC-106", "Error occurred while fetching Templates"),
	TEMPLATE_TYPE_FETCH_EXCEPTION("KER-SYNC-107", "Error occurred while fetching Template Types"),
	REASON_CATEGORY_FETCH_EXCEPTION("KER-SYNC-108", "Error occurred while fetching Reason Category"),
	HOLIDAY_FETCH_EXCEPTION("KER-SYNC-109", "Error occurred while fetching Holidays"),
	BLACKLISTED_WORDS_FETCH_EXCEPTION("KER-SYNC-110", "Error occurred while fetching Blacklisted Words"),
	BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-SYNC-111", "Error occurred while fetching Biometric types"),
	BIOMETRIC_ATTR_TYPE_FETCH_EXCEPTION("KER-SYNC-112", "Error occurred while fetching Biometric Attribute types"),
	TITLE_FETCH_EXCEPTION("KER-SYNC-113", "Error occurred while fetching Titles"),
	LANGUAGE_FETCH_EXCEPTION("KER-SYNC-114", "Error occurred while fetching Languages"),
	GENDER_FETCH_EXCEPTION("KER-SYNC-115", "Error occurred while fetching Genders"),
	REGISTARTION_CENTER_DEVICES_FETCH_EXCEPTION("KER-SYNC-116", "Error occurred while fetching Registration Center Devices"),
	DEVICES_FETCH_EXCEPTION("KER-SYNC-117", "Error occurred while fetching Devices"),
	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-SYNC-118", "Error occurred while fetching Document Category"),
	DOCUMENT_TYPE_FETCH_EXCEPTION("KER-SYNC-119", "Error occurred while fetching Document Type"),
	ID_TYPE_FETCH_EXCEPTION("KER-SYNC-120", "Error occurred while fetching Id Type"),
	DEVICE_SPECIFICATION_FETCH_EXCEPTION("KER-SYNC-121", "Error occurred while fetching Device Specification"),
	MACHINE_SPECIFICATION_FETCH_EXCEPTION("KER-SYNC-122", "Error occurred while fetching Machine Specification"),
	MACHINE_TYPE_FETCH_EXCEPTION("KER-SYNC-123", "Error occurred while fetching Machine Type"),
	LOCATION_FETCH_EXCEPTION("KER-SYNC-124", "Error occurred while fetching Location"),
	DEVICE_TYPE_FETCH_EXCEPTION("KER-SYNC-125", "Error occurred while fetching Device Type"),
	VALID_DOCUMENT_FETCH_EXCEPTION("KER-SYNC-126", "Error occurred while fetching Valid Document Type"),
	REASON_LIST_FETCH_EXCEPTION("KER-SYNC-126", "Error occurred while fetching Valid Document Type");

	private final String errorCode;
	private final String errorMessage;

	private MasterDataErrorCode(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}
