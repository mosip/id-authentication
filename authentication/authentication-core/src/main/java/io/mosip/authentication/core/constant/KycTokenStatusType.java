package io.mosip.authentication.core.constant;

/**
 * The Enum KYC Token Status is used to set the token status for each transactions.
 * 
 *  @author Mahammed Taheer
 */
public enum KycTokenStatusType {
	
	/** The active status. */
	ACTIVE("ACTIVE"),
   
   /** The processed status. */
   PROCESSED("PROCESSED"),
   
   /** The expired Status */
   EXPIRED ("EXPIRED");
	
	/** The type. */
	private String status; 
	
	/**
	 * Instantiates a new status type.
	 *
	 * @param status the status
	 */
	private KycTokenStatusType(String status) {
	  this.status = status;	
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
