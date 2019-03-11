package io.mosip.dbdto;

public class TransactionStatusDTO {
	private String registrationId;
	private String statusCode;
	private String statusComment;
	
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusComment() {
		return statusComment;
	}
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}
	@Override
	public String toString() {
		return "TransactionStatusDTO [registrationId=" + registrationId + ", statusCode=" + statusCode
				+ ", statusComment=" + statusComment + "]";
	}

}
