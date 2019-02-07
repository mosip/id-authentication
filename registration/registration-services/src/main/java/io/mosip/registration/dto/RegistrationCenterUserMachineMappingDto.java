package io.mosip.registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationCenterUserMachineMappingDto {
	private String cntrId;
	private String machineId;
	private String usrId;

	private boolean isActive;

	/**
	 * @return the cntrId
	 */
	public String getCntrId() {
		return cntrId;
	}

	/**
	 * @param cntrId
	 *            the cntrId to set
	 */
	public void setCntrId(String cntrId) {
		this.cntrId = cntrId;
	}

	/**
	 * @return the machineId
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * @param machineId
	 *            the machineId to set
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	/**
	 * @return the usrId
	 */
	public String getUsrId() {
		return usrId;
	}

	/**
	 * @param usrId
	 *            the usrId to set
	 */
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}

	/**
	 * @return the isActive
	 */
	@JsonProperty("isActive")
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
