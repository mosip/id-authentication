package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.RegistrationCenterMachineUserID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for User and Registration mappings
 * 
 * @author Dharmesh Khandelwal
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reg_center_user_machine", schema = "master")
public class RegistrationCenterUserMachine extends BaseEntity implements Serializable {

	/**
	 * Generated Serial Id
	 */
	private static final long serialVersionUID = -4167453471874926985L;

	/**
	 * Composite key for this table
	 */
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cntrId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "usrId", column = @Column(name = "usr_id")),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id")) })
	private RegistrationCenterMachineUserID registrationCenterMachineUserID;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
