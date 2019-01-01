package io.mosip.preregistration.datasync.code;

/**
 * 
 * Various Status codes for Data Sync
 * 
 * @author M1046129 - Jagadishwari
 *
 */
public enum StatusCodes {
	PENDINGAPPOINTMENT("Pending_Appointment"), 
	BOOKED("Booked"), 
	EXPIRED("Expired"), 
	CONSUMED("Consumed"),
	CANCELED("Canceled");
	
	/**
	 * @param code
	 */
	private StatusCodes(String code) {
		this.code = code;
	}

	private final String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
}