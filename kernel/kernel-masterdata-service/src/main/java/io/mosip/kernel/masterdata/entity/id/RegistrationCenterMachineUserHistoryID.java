package io.mosip.kernel.masterdata.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** ID class for Registration Center User Machine History
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterMachineUserHistoryID implements Serializable {

	/**
	 *  Generated serial version
	 */
	private static final long serialVersionUID = -1169819225048676557L;

	/**
	 *  Center Id
	 */
	@Column(name = "regcntr_id", nullable = false, length = 10)
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
	
	/**
	 * Effective TimeStamp
	 */
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;


}
