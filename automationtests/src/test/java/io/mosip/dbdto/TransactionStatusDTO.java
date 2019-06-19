package io.mosip.dbdto;

public class TransactionStatusDTO {
	private String registrationId;
	private String trn_type_code;
	private String status_code;
	private String cr_dtimes;
	
	public String getCr_dtimes() {
		return cr_dtimes;
	}
	public void setCr_dtimes(String cr_dtimes) {
		this.cr_dtimes = cr_dtimes;
	}
	public String getRegistrationId() {
		return registrationId;
	}
	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}
	public String getTrn_type_code() {
		return trn_type_code;
	}
	public void setTrn_type_code(String trn_type_code) {
		this.trn_type_code = trn_type_code;
	}
	public String getStatus_code() {
		return status_code;
	}
	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}
	@Override
	public String toString() {
		return "TransactionStatusDTO [registrationId=" + registrationId + ", trn_type_code=" + trn_type_code
				+ ", status_code=" + status_code + ", cr_dtimes=" + cr_dtimes + "]";
	}

}
