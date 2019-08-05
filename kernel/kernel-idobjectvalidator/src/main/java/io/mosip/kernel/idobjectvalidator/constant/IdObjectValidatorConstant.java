package io.mosip.kernel.idobjectvalidator.constant;

/**
 * This enum provides all the constants for property source to be used.
 * 
 * @author Manoj SP
 * @author Swati Raj
 *
 */
public enum IdObjectValidatorConstant {

	LEVEL("level"), 
	MESSAGE("message"), 
	WARNING("warning"), 
	INSTANCE("instance"), 
	POINTER("pointer"), 
	AT(" at "),
	ERROR("error"), 
	PATH_SEPERATOR("/"),
	REFERENCE_IDENTITY_NUMBER_REGEX("^([0-9]{10,30})$"),
	DOB_FORMAT("uuuu/MM/dd"),
	INVALID_ATTRIBUTE("Invalid attribute"),
	ROOT_PATH("identity"),
	IDENTITY_REFERENCE_IDENTITY_NUMBER_PATH("identity.referenceIdentityNumber"),
	IDENTITY_DOB_PATH("identity.dateOfBirth"),
	IDENTITY_LANGUAGE_PATH("identity.*.*.language"),
	IDENTITY_POSTAL_CODE_PATH("identity.postalCode"),
	IDENTITY_GENDER_LANGUAGE_PATH("identity.gender.*.language"),
	IDENTITY_GENDER_VALUE_PATH("identity.gender.*.value"),
	IDENTITY_REGION_LANGUAGE_PATH("identity.region.*.language"),
	IDENTITY_REGION_VALUE_PATH("identity.region.*.value"),
	IDENTITY_PROVINCE_LANGUAGE_PATH("identity.province.*.language"),
	IDENTITY_PROVINCE_VALUE_PATH("identity.province.*.value"),
	IDENTITY_CITY_LANGUAGE_PATH("identity.city.*.language"),
	IDENTITY_CITY_VALUE_PATH("identity.city.*.value"),
	IDENTITY_ZONE_LANGUAGE_PATH("identity.zone.*.language"),
	IDENTITY_ZONE_VALUE_PATH("identity.zone.*.value"),
	MASTERDATA_LANGUAGE_PATH("response.languages.*"),
	MASTERDATA_LOCATIONS_PATH("locations.*"),
	MASTERDATA_LANGUAGE_URI("mosip.kernel.idobjectvalidator.masterdata.languages.rest.uri"),
	MASTERDATA_GENDERTYPES_URI("mosip.kernel.idobjectvalidator.masterdata.gendertypes.rest.uri"),
	MASTERDATA_DOCUMENT_CATEGORIES_URI("mosip.kernel.idobjectvalidator.masterdata.documentcategories.rest.uri"),
	MASTERDATA_DOCUMENT_TYPES_URI("mosip.kernel.idobjectvalidator.masterdata.documenttypes.rest.uri"),
	MASTERDATA_LOCATIONS_URI("mosip.kernel.idobjectvalidator.masterdata.locations.rest.uri"),
	MASTERDATA_LOCATION_HIERARCHY_URI("mosip.kernel.idobjectvalidator.masterdata.locationhierarchy.rest.uri"),
	APPLICATION_ID("application.id"),
	FIELD_LIST("mosip.kernel.idobjectvalidator.mandatory-attributes.%s.%s");

	/**
	 * The property present in Report.
	 */
	private final String value;

	/**
	 * Setter for property.
	 * 
	 * @param property The propert to be set
	 */
	private IdObjectValidatorConstant(String value) {
		this.value = value;
	}

	/**
	 * Getter for property.
	 * 
	 * @return The property.
	 */
	public String getValue() {
		return value;
	}

}
