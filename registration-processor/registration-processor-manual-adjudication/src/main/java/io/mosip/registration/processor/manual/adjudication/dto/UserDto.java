package io.mosip.registration.processor.manual.adjudication.dto;

import java.io.Serializable;

/**
 * The {@link UserDto} class
 * 
 * @author Shuchita
 * @since 0.0.1
 *
 */
public class UserDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String userId;
	private String name;
	private String status;
	private String office;

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
