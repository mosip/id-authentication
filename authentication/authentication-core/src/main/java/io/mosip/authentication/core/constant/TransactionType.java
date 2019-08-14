package io.mosip.authentication.core.constant;

/**
 * The Enum TransactionType is used to  set the transaction type in auth transaction type 
 */
public enum TransactionType {
	
	INTERNAL("INTERNAL"),
   PARTNER("PARTNER");
	
	/** The type. */
	private String type; 
	
	/**
	 * Instantiates a new transaction type.
	 *
	 * @param type the type
	 */
	private TransactionType(String type) {
	  this.type=type;	
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
