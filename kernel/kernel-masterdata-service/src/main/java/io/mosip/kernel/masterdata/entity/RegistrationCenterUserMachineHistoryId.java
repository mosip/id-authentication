package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity class for Registration Center User Machine
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterUserMachineHistoryId implements Serializable {

	/**
	 *  Generated serial version
	 */
	private static final long serialVersionUID = -1169819225048676557L;

	/**
	 *  Center Id
	 */
	@Column(name = "regcntr_id", nullable = false, length = 36)
	private String cntrId;

	/**
	 * User Id
	 */
	@Column(name = "usr_id", nullable = false, length = 36)
	private String usrId;

	/**
	 * Machine Id
	 */
	@Column(name = "machine_id", nullable = false, length = 36)
	private String machineId;

}
