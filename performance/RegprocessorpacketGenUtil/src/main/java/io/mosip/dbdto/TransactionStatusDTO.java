package io.mosip.dbdto;

public class TransactionStatusDTO {
	private String reg_id;
	private String status_Code;
	private String trn_type_code;
	private String cr_dtimes;
	public String getCr_dtimes() {
		return cr_dtimes;
	}
	public void setCr_dtimes(String cr_dtimes) {
		this.cr_dtimes = cr_dtimes;
	}
	public String getReg_id() {
		return reg_id;
	}
	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}
	public String getStatus_Code() {
		return status_Code;
	}
	public void setStatus_Code(String status_Code) {
		this.status_Code = status_Code;
	}
	public String getTrn_type_code() {
		return trn_type_code;
	}
	public void setTrn_type_code(String trn_type_code) {
		this.trn_type_code = trn_type_code;
	}
	@Override
	public String toString() {
		return "TransactionStatusDTO [reg_id=" + reg_id + ", status_Code=" + status_Code + ", trn_type_code="
				+ trn_type_code + "]";
	}
	
	

}
