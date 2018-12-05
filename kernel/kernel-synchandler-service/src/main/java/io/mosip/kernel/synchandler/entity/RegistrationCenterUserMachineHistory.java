package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.synchandler.entity.id.RegistrationCenterMachineUserID;
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
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cntrId", column = @Column(name = "regcntr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "usrId", column = @Column(name = "usr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id", nullable = false, length = 36)) })
	private RegistrationCenterMachineUserID id;

	/**
	 * Effective TimeStamp
	 */
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;

}
