package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineUserID;
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
//@IdClass(RegistrationCenterMachineUserID.class)
public class RegistrationCenterUserMachine extends BaseEntity implements Serializable {

	/**
	 * Generated Serial Id
	 */
	private static final long serialVersionUID = -4167453471874926985L;

	/**
	 * Composite key for this table
	 */
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cntrId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "usrId", column = @Column(name = "usr_id")),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id")) })
	private RegistrationCenterMachineUserID registrationCenterMachineUserID;
	
	/*@Id
	@AttributeOverrides({
		@AttributeOverride(name = "cntrId", column = @Column(name = "regcntr_id", nullable = false, length = 10)),
		@AttributeOverride(name = "usrId", column = @Column(name = "usr_id", nullable = false, length = 36)),
		@AttributeOverride(name = "machineId", column = @Column(name = "machine_id", nullable = false, length = 10)) })
	private String cntrId;
	private String usrId;
	private String machineId;*/
	
	

	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "regcntr_id", referencedColumnName = "id", insertable = false, updatable = false),
		@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false)
		})
	private RegistrationCenter registrationCenter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "usr_id", referencedColumnName = "id", insertable = false, updatable = false)
		//@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false)
	})
	private UserDetails userDetails;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "machine_id", referencedColumnName = "id", insertable = false, updatable = false),
		@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false)
	})
	private Machine machine;*/
}
