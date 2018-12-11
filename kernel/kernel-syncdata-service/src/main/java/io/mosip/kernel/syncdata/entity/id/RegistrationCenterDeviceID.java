package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegistrationCenterDeviceID implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "regcntr_id", unique = true, nullable = false, length = 36)
	private String regCenterId;

	@Column(name = "device_id", unique = true, nullable = false, length = 36)
	private String deviceId;

}
