package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * composite primary key of {@link RegCentreMachineDeviceId}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Embeddable
public class RegCentreMachineDeviceId implements Serializable {

	private static final long serialVersionUID = -1247474923778950226L;
	@Column(name = "regcntr_id", length = 36)
	private String regCentreId;
	@Column(name = "machine_id", length = 36)
	private String machineId;
	@Column(name = "device_id", length = 36)
	private String deviceId;

	/**
	 * @return the regCentreId
	 */
	public String getRegCentreId() {
		return regCentreId;
	}

	/**
	 * @param regCentreId
	 *            the regCentreId to set
	 */
	public void setRegCentreId(String regCentreId) {
		this.regCentreId = regCentreId;
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
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
