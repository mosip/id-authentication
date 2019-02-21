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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((centreID == null) ? 0 : centreID.hashCode());
		result = prime * result + ((machineID == null) ? 0 : machineID.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserMachineMappingID other = (UserMachineMappingID) obj;
		if (centreID == null) {
			if (other.centreID != null)
				return false;
		} else if (!centreID.equals(other.centreID))
			return false;
		if (machineID == null) {
			if (other.machineID != null)
				return false;
		} else if (!machineID.equals(other.machineID))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}
}