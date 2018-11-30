package io.mosip.registration.processor.manual.adjudication.dto;
/**
 * 
 * @author M1049617
 *
 */
public class UserDto {
	private static final long serialVersionUID = 1L;
	private String userId;
	private String name;
	private String status;
	private String office;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOffice() {
		return office;
	}
	public void setOffice(String office) {
		this.office = office;
	}
}
