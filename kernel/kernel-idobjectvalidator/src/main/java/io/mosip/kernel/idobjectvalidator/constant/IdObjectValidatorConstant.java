package io.mosip.kernel.idobjectvalidator.constant;

/**
 * This enum provides all the constants for property source to be used.
 * 
 * @author Manoj SP
 * @author Swati Raj
 *
 */
public class IdObjectValidatorConstant {

	public static final String LEVEL = "level"; 
	public static final String MESSAGE = "message"; 
	public static final String WARNING = "warning"; 
	public static final String INSTANCE = "instance"; 
	public static final String POINTER = "pointer"; 
	public static final String AT = " at ";
	public static final String ERROR = "error"; 
	public static final String PATH_SEPERATOR = "/";
	public static final String REFERENCE_IDENTITY_NUMBER_REGEX = "^ = [0-9]{10,30})$";
	public static final String INVALID_ATTRIBUTE = "Invalid attribute";
	public static final String ROOT_PATH = "identity";
	public static final String IDENTITY_ARRAY_VALUE_FIELD = "value";
	public static final String APPLICATION_ID = "application.id";
	public static final String FIELD_LIST = "mosip.kernel.idobjectvalidator.mandatory-attributes.%s.%s";
	public static final String REFERENCE_VALIDATOR = "mosip.kernel.idobjectvalidator.referenceValidator";

}
