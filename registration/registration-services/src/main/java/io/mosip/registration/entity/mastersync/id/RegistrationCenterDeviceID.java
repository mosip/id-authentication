package io.mosip.registration.entity.mastersync.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */

@Embeddable
public class RegistrationCenterDeviceID implements Serializable {

	private static final long serialVersionUID = 1L;

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
	 * @param regCenterId the regCenterId to set
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
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	

}
