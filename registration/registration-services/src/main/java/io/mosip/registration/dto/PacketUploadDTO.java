package io.mosip.registration.dto;

/**
 * The Class PacketUploadDTO.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public class PacketUploadDTO {
	private String userid;
	private String password;
	private String filepath;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}
