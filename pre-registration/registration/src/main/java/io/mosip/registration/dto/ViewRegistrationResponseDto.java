package io.mosip.registration.dto;

public class ViewRegistrationResponseDto {

	@Override
	public String toString() {
		return "ResponseDto [upd_dtimesz=" + upd_dtimesz + ", group_id=" + group_id + ", status_code=" + status_code
				+ ", firstname=" + firstname + ", noOfRecords=" + noOfRecords + "]";
	}

	private String upd_dtimesz;
	private String group_id;
	private String status_code;
	private String firstname;
	private int noOfRecords;

	public String getUpd_dtimesz() {
		return upd_dtimesz;
	}

	public void setUpd_dtimesz(String upd_dtimesz) {
		this.upd_dtimesz = upd_dtimesz;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getStatus_code() {
		return status_code;
	}

	public void setStatus_code(String status_code) {
		this.status_code = status_code;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public int getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(int noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

}
