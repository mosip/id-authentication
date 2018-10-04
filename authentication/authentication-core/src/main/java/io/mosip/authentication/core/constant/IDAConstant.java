package io.mosip.authentication.core.constant;

/**
 * The Enum IDAConstant.
 * 
 * @author Rakesh Roshan
 */
public enum IDAConstant {

	/** product id */
	PRODUCT_ID("ida");

	/** Constant value */
	String value;

	/**
	 * Instantiates a enum IDAConstant.
	 *
	 * @param value value of constant
	 */
	private IDAConstant(String value) {
		this.value = value;
	}

	/**
	 * Get the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
