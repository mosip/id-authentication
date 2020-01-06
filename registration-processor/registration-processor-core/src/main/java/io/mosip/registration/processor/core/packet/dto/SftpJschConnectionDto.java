package io.mosip.registration.processor.core.packet.dto;

import java.io.Serializable;

/**
 * The Class SftpJschConnectionDto.
 * @author  Rishabh Keshari
 */
public class SftpJschConnectionDto implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7536756021050950998L;
	
	/** The host. */
	private String host;
	
	/** The port. */
	private int port;
	
	/** The user. */
	private String user;
	
	/** The ppk file location. */
	private String ppkFileLocation;
	
	/** The ppk file location. */
	private String protocal;
	
	
	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	
	/**
	 * Gets the ppk file location.
	 *
	 * @return the ppk file location
	 */
	public String getPpkFileLocation() {
		return ppkFileLocation;
	}
	
	/**
	 * Sets the ppk file location.
	 *
	 * @param ppkFileLocation the new ppk file location
	 */
	public void setPpkFileLocation(String ppkFileLocation) {
		this.ppkFileLocation = ppkFileLocation;
	}

	/**
	 * Gets the protocal.
	 *
	 * @return the protocal
	 */
	public String getProtocal() {
		return protocal;
	}

	/**
	 * Sets the protocal.
	 *
	 * @param protocal the new protocal
	 */
	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}
	
	


}