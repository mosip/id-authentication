package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.RegCenterDevice;
import lombok.Data;

/**
 * contains the composite primary key for {@link RegCenterDevice}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Embeddable
@Data
public class RegCenterDeviceId implements Serializable {

	private static final long serialVersionUID = 683329999784335720L;

	@Column(name = "regcntr_id")
	private String regCenterId;
	@Column(name = "device_id")
	private String deviceId;

	/**
	 * @return the regCenterId
	 */
	public String getRegCenterId() {
		return regCenterId;
	}

	/**
	 * @param regCenterId
	 *            the regCenterId to set
	 */
	public void setRegCenterId(String regCenterId) {
		this.regCenterId = regCenterId;
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
		result = prime * result + ((regCenterId == null) ? 0 : regCenterId.hashCode());
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
		RegCenterDeviceId other = (RegCenterDeviceId) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (regCenterId == null) {
			if (other.regCenterId != null)
				return false;
		} else if (!regCenterId.equals(other.regCenterId))
			return false;
		return true;
	}
}
