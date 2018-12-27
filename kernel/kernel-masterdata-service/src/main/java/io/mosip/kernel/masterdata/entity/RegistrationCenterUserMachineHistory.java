package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserHistoryID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for User and Registration mappings
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@IdClass(RegistrationCenterMachineUserHistoryID.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reg_center_user_machine_h", schema = "master")
public class RegistrationCenterUserMachineHistory extends BaseEntity implements Serializable {

	/**
	 * Generated Serial Id
	 */
	private static final long serialVersionUID = -4167453471874926985L;

	/**
	 * Composite key for this table
	 */
	@Id
	@AttributeOverrides({
			@AttributeOverride(name = "cntrId", column = @Column(name = "regcntr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "usrId", column = @Column(name = "usr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id", nullable = false, length = 36)),
			@AttributeOverride(name = "effectivetimes", column = @Column(name = "eff_dtimes", nullable = false)),})
	
	/**
	 * Center Id
	 */
	private String cntrId;

	/**
	 * User Id
	 */
	private String usrId;

	/**
	 * Machine Id
	 */
	private String machineId;
	
	/**
	 * Effective TimeStamp
	 */
	private LocalDateTime effectivetimes;
}
