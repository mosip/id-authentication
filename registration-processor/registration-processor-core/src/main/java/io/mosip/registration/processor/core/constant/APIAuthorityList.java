package io.mosip.registration.processor.core.constant;

public enum APIAuthorityList {

	PACKETRECEIVER(new String[] { "REGISTRATION_ ADMIN","REGISTRATION_PROCESSOR","REGISTRATION_CLIENT"}),
	
	PACKETSYNC(new String[] { "REGISTRATION_ADMIN", "REGISTRATION_PROCESSOR","REGISTRATION_CLIENT"}),
	
	REGISTRATIONSTATUS(new String[] {"REGISTRATION_ADMIN","REGISTRATION_PROCESSOR","REGISTRATION_CLIENT"}),
	
	MANUALVERIFICTION(new String[] {"REGISTRATION_ADMIN","MANUAL_VERIFIER"}),
	
	PRINTSTAGE(new String[] {"REGISTRATION_PROCESSOR"}),
	
	CONNECTORSTAGE(new String[] {"REGISTRATION_PROCESSOR"}),

	PRINTUINCARD(new String[] { "REGISTRATION_ADMIN"});
	
	private final String[] list;

	private APIAuthorityList(String[] list) {
		this.list = list;
	}

	public String[] getList(){
		return this.list;
	}
}