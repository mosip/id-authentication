package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.RegistrationCenterMachineID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity class to map Registration center id and Machine id.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 *
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reg_center_machine", schema = "master")
public class RegistrationCenterMachine extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -8541947587557590379L;

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "regCenterId", column = @Column(name = "regcntr_id")),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id")) })
	private RegistrationCenterMachineID registrationCenterMachinePk;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "machine_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private Machine machine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "regcntr_id", referencedColumnName = "id", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private RegistrationCenter registrationCenter;
}
