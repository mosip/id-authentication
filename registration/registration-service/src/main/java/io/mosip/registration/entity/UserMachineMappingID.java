package io.mosip.registration.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserMachineMappingID implements Serializable {

	private static final long serialVersionUID = -1883492292190913762L;

	@Column(name = "usr_id")
	private String userID;
	@Column(name = "regcntr_id")
	private String centreID;
	@Column(name = "machine_id")
	private String machineID;

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the centreID
	 */
	public String getCentreID() {
		return centreID;
	}

	/**
	 * @param centreID
	 *            the centreID to set
	 */
	public void setCentreID(String centreID) {
		this.centreID = centreID;
	}

	/**
	 * @return the machineID
	 */
	public String getMachineID() {
		return machineID;
	}

	/**
	 * @param machineID
	 *            the machineID to set
	 */
	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

}