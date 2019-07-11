package io.mosip.admin.iddefinition.constant;
/**
 * Identity Json Schema Validation Error Constants
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
public enum JsonIdentitySchemaErrorConstant {
	
	INVALID_JSON("ADM-IDV-001","Json schema is either null or empty"),
	TITLE_ATTR_MISSING("ADM-IDV-002","Title attribute is missing"),
	ID_ATTR_MISSING("ADM-IDV-003","$id attribute is missing"),
	SCHEMA_ATTR_MISSING("ADM-IDV-004","Title attribute is missing"),
	PROPERTIES_ATTR_MISSING("ADM-IDV-005","Properties attribute is missing"),
	IDENTITY_ATTR_MISSING("ADM-IDV-006","Identity attribute is missing"),
	IDENTITY_PROPS_ATTR_MISSING("ADM-IDV-007","Identity properties attribute is missing"),
	NO_IDENTITY_PROPS("ADM-IDV-008","No Identity properties fields");
	
	
	private final String errorCode;
	private final String errorMessage;
	private JsonIdentitySchemaErrorConstant(String errorCode,String errorMessage) {
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
	
	public String getErrorCode() {
		return this.errorCode;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
