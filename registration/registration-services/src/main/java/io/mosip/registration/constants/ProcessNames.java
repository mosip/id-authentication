package io.mosip.registration.constants;

/**
 * Enum for Process Names
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public enum ProcessNames {
	LOGIN("login_auth"), 
	PACKET("packet_auth"),
	EOD("eod_auth"),
	EXCEPTION("exception_auth"),
	ONBOARD("onboard_auth");	
	
	
	/**
	 * @param type
	 */
	private ProcessNames(String type) {
		this.type=type;
	}
	
	private final String type;
	
	/**
	 * 
	 * @return the type 
	 */
	public String getType() {
		return type;
	}

}
