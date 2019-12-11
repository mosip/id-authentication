package io.mosip.kernel.masterdata.constant;

/**
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class MasterDataConstant {
	/**
	 * Constructor for this class
	 */
	private MasterDataConstant() {
	}

	public static final Double METERTOMILECONVERSION = 0.000621371;
	public static final String DATETIMEFORMAT = " format should be yyyy-mm-ddThh:mm:ss format";
	public static final String VALID = "Valid";
	public static final String INVALID = "Invalid";
	public static final String CENTERTYPENAME = "centertypename";
	public static final String CENTERTYPECODE = "centerTypeCode";
	public static final String CENTERLOCCODE = "locationCode";
	public static final String NAME = "name";
	public static final String POSTAL_CODE = "postalCode";
	public static final String CITY = "city";
	public static final String PROVINCE = "province";
	public static final String REGION = "region";
	public static final String ADMINISTRATIVE_ZONE = "administrativeZone";
	public static final String HIERARCHY_NAME = "hierarchyName";
	public static final String ZONE = "zone";
	public static final String ZONE_CODE = "zoneCode";
	public static final String HIERARCHY_LEVEL = "hierarchyLevel";
	public static final String MAPPED_SUCCESSFULLY = "mapped";
	public static final String DOC_CATEGORY_AND_DOC_TYPE_MAPPING_SUCCESS_MESSAGE = "Document Category %s - Document Type Mapping %s is added successfully";
	public static final String UNMAPPED_SUCCESSFULLY = "un-mapped";
	public static final String DOC_CATEGORY_AND_DOC_TYPE_UNMAPPING_SUCCESS_MESSAGE = "Document Category %s - Document Type Mapping %s is updated successfully";
	public static final String DEVICE_AND_REGISTRATION_CENTER_UNMAPPING_SUCCESS_MESSAGE = "Device %s is successfully Un-Mapped to the Registration Center %s ";
    public static final String USER_AND_REGISTRATION_CENTER_UNMAPPING_SUCCESS_MESSAGE = "User %s - Registration Center Mapping %s is updated successfully";
    public static final String USER_AND_REGISTRATION_CENTER_MAPPING_SUCCESS_MESSAGE = "User %s - Registration Center Mapping %s is added successfully";
    public static final String SUCCESS="Success"; 
    public static final String IS_ACTIVE="isActive";
    public static final String DEVICE_REGISTER_UPDATE_MESSAGE="Device status updated successfully";
    public static final String INVALID_REG_CENTER_TYPE="Invalid centerTypeCode";
    public static final String INVALID_LOCATION_CODE="Invalid Location Code";
	public static final String SEARCH_API_IS_CALLED = "Search API called::";
	public static final String SUCCESSFUL_SEARCH=" %s Search Success";
	public static final String SUCCESSFUL_SEARCH_DESC=" System successfully searched List of %s";
	public static final String SEARCH_FAILED="%s - Search Failure";
	public static final String FILTER_API_IS_CALLED = "Filter API called::";
	public static final String SUCCESSFUL_FILTER=" %s - Filter Success";
	public static final String SUCCESSFUL_FILTER_DESC=" System successfully searched filter values for %s";
	public static final String FILTER_FAILED="%s - Filter Failure";
	public static final String CREATE_API_IS_CALLED = "Create API called::";
	public static final String SUCCESSFUL_CREATE="Create %s - Success";
	public static final String SUCCESSFUL_CREATE_DESC="System successfully created a %s";
	public static final String SUCCESSFUL_UPDATE_DESC="System successfully updated a %s";
	public static final String FAILURE_CREATE="Create %s - Failure";
	public static final String UPDATE_API_IS_CALLED= "Update API called::";
	public static final String SUCCESSFUL_UPDATE="Update %s - Success";
	public static final String FAILURE_UPDATE="Update %s - Failure";
	public static final String DECOMMISION_API_CALLED="%s Decommission API called";
	public static final String DECOMMISSION_SUCCESS="Decommission %s - Success";
	public static final String DECOMMISSION_SUCCESS_DESC="System successfully decommissioned a %s";
	public static final String FAILURE_DECOMMISSION="Decommission %s - Failure";
	public static final String FAILURE_DESC="%s_%s";
	
	
	public static final String AUDIT_SYSTEM = "SYSTEM";
}
