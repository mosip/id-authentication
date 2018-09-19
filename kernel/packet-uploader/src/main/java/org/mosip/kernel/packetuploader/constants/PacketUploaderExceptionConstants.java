package org.mosip.kernel.packetuploader.constants;

public enum PacketUploaderExceptionConstants {
	
MOSIP_ILLEGAL_CONFIGURATION_EXCEPTION("errorcode","errormessage"),
MOSIP_CONNECTION_EXCEPTION("errorcode","errormessage"),
MOSIP_ILLEGAL_IDENTITY_EXCEPTION("errorcode","errormessage"),
MOSIP_SFTP_EXCEPTION("errorcode","errormessage"),
MOSIP_NO_SESSION_FOUND_EXCEPTION("errorcode","errormessage");
	
	
private PacketUploaderExceptionConstants() {
}

PacketUploaderExceptionConstants(String errorCode,String errorMessage){
	this.setErrorCode(errorCode);
	this.setErrorMessage(errorMessage);
}

public String getErrorCode() {
	return errorCode;
}

private void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
}

public String getErrorMessage() {
	return errorMessage;
}

private void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
}

String errorCode;
String errorMessage;
}
