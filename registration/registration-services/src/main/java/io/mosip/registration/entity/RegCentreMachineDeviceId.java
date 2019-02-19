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
	@Column(name = "regcntr_id")
	private String regCentreId;
	@Column(name = "machine_id")
	private String machineId;
	@Column(name = "device_id")
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((machineId == null) ? 0 : machineId.hashCode());
		result = prime * result + ((regCentreId == null) ? 0 : regCentreId.hashCode());
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
		RegCentreMachineDeviceId other = (RegCentreMachineDeviceId) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (machineId == null) {
			if (other.machineId != null)
				return false;
		} else if (!machineId.equals(other.machineId))
			return false;
		if (regCentreId == null) {
			if (other.regCentreId != null)
				return false;
		} else if (!regCentreId.equals(other.regCentreId))
			return false;
		return true;
	}
}
