package io.mosip.kernel.syncdata.constant;

public enum MasterDataErrorCode {
	LAST_UPDATED_PARSE_EXCEPTION("KER-SNC-100","Error occurred while parsing lastUpdated timesatamp"),
	APPLICATION_FETCH_EXCEPTION("KER-SNC-101","Error occurred while fetching Applications"), 
	MACHINE_DETAIL_FETCH_EXCEPTION("KER-SNC-102","Error occurred while fetching Machine Details"), 
	MACHINE_REG_CENTER_FETCH_EXCEPTION("KER-SNC-103","Error occurred while fetching Machine Registration Center"), 
	REG_CENTER_FETCH_EXCEPTION("KER-SNC-104","Error occurred while fetching Registration Center"), 
	REG_CENTER_TYPE_FETCH_EXCEPTION("KER-SNC-105", "Error occurred while fetching Registration Center Type"),
	TEMPLATE_FETCH_EXCEPTION("KER-SNC-106", "Error occurred while fetching Templates"),
	TEMPLATE_TYPE_FETCH_EXCEPTION("KER-SNC-107", "Error occurred while fetching Template Types"),
	REASON_CATEGORY_FETCH_EXCEPTION("KER-SNC-108", "Error occurred while fetching Reason Category"),
	HOLIDAY_FETCH_EXCEPTION("KER-SNC-109", "Error occurred while fetching Holidays"),
	BLACKLISTED_WORDS_FETCH_EXCEPTION("KER-SNC-110", "Error occurred while fetching Blacklisted Words"),
	BIOMETRIC_TYPE_FETCH_EXCEPTION("KER-SNC-111", "Error occurred while fetching Biometric types"),
	BIOMETRIC_ATTR_TYPE_FETCH_EXCEPTION("KER-SNC-112", "Error occurred while fetching Biometric Attribute types"),
	TITLE_FETCH_EXCEPTION("KER-SNC-113", "Error occurred while fetching Titles"),
	LANGUAGE_FETCH_EXCEPTION("KER-SNC-114", "Error occurred while fetching Languages"),
	GENDER_FETCH_EXCEPTION("KER-SNC-115", "Error occurred while fetching Genders"),
	REGISTARTION_CENTER_DEVICES_FETCH_EXCEPTION("KER-SNC-116", "Error occurred while fetching Registration Center Devices"),
	DEVICES_FETCH_EXCEPTION("KER-SNC-117", "Error occurred while fetching Devices"),
	DOCUMENT_CATEGORY_FETCH_EXCEPTION("KER-SNC-118", "Error occurred while fetching Document Category"),
	DOCUMENT_TYPE_FETCH_EXCEPTION("KER-SNC-119", "Error occurred while fetching Document Type"),
	ID_TYPE_FETCH_EXCEPTION("KER-SNC-120", "Error occurred while fetching Id Type"),
	DEVICE_SPECIFICATION_FETCH_EXCEPTION("KER-SNC-121", "Error occurred while fetching Device Specification"),
	MACHINE_SPECIFICATION_FETCH_EXCEPTION("KER-SNC-122", "Error occurred while fetching Machine Specification"),
	MACHINE_TYPE_FETCH_EXCEPTION("KER-SNC-123", "Error occurred while fetching Machine Type"),
	LOCATION_FETCH_EXCEPTION("KER-SNC-124", "Error occurred while fetching Location"),
	DEVICE_TYPE_FETCH_EXCEPTION("KER-SNC-125", "Error occurred while fetching Device Type"),
	VALID_DOCUMENT_FETCH_EXCEPTION("KER-SNC-126", "Error occurred while fetching Valid Document Type"),
	REASON_LIST_FETCH_EXCEPTION("KER-SNC-127", "Error occurred while fetching Valid Document Type"),
	THREAD_INTERRUPTED_WHILE_FETCH_EXCEPTION("KER-SNC-128", "Error occurred while fetching data"),
	REG_CENTER_MACHINE_FETCH_EXCEPTION("KER-SNC-129","Error occurred while fetching Registration Center Machine"),
	REG_CENTER_DEVICE_FETCH_EXCEPTION("KER-SNC-130","Error occurred while fetching Registration Center Device"),
	REG_CENTER_MACHINE_DEVICE_FETCH_EXCEPTION("KER-SNC-131","Error occurred while fetching Registration Center Machine Device"),
	REG_CENTER_USER_MACHINE_DEVICE_FETCH_EXCEPTION("KER-SNC-132","Error occurred while fetching Registration Center Machine Device"),
	REG_CENTER_USER_FETCH_EXCEPTION("KER-SNC-133","Error occurred while fetching Registration Center User");

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
