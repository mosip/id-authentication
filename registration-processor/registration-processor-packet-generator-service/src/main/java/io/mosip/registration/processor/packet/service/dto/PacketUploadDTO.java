package io.mosip.registration.processor.packet.service.dto;

/**
 * The Class PacketUploadDTO.
 * 
 * @author Rishabh Keshari
 */
public class PacketUploadDTO {

	/** The userid. */
	private String userid;

	/** The password. */
	private String password;

	/** The filepath. */
	private String filepath;

	/**
	 * Gets the userid.
	 *
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * Sets the userid.
	 *
	 * @param userid
	 *            the new userid
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the filepath.
	 *
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * Sets the filepath.
	 *
	 * @param filepath
	 *            the new filepath
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

}
