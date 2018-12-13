package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity for Machine Specifications
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "machine_spec", schema = "master")
public class MachineSpecification extends BaseEntity implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * Field for machine Specification id
	 */
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String id;

	/**
	 * Field for machine Specification name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * Field for machine Specification brand
	 */
	@Column(name = "brand", nullable = false, length = 32)
	private String brand;

	/**
	 * Field for machine Specification model
	 */
	@Column(name = "model", nullable = false, length = 16)
	private String model;

	/**
	 * Field for machine Specification machineTypeCode
	 */
	@Column(name = "mtyp_code", nullable = false, length = 36)
	private String machineTypeCode;

	/**
	 * Field for machine Specification minDriverversion
	 */
	@Column(name = "min_driver_ver", nullable = false, length = 16)
	private String minDriverversion;

	/**
	 * Field for machine Specification description
	 */
	@Column(name = "descr", length = 256)
	private String description;

	/**
	 * Field for machine Specification langCode
	 */
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private MachineType machineType;

}
