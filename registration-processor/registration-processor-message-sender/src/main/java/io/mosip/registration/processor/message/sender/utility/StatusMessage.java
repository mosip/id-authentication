package io.mosip.registration.processor.message.sender.utility;

public class StatusMessage {
	
	private StatusMessage() {
		
	}
	
	public static final String PHONENUMBER_NOT_FOUND = "SMS not sent as Phone number not found in packet";
	
	public static final String EMAILID_NOT_FOUND = "Email not sent as emailId not found in packet";

}
