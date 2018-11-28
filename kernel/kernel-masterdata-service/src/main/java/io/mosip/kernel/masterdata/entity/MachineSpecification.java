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

@EqualsAndHashCode(callSuper = false)
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
	
	

	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String id;
	
	@Column(name = "name", nullable = false, length = 64)
	private String name;
	
	@Column(name = "brand", nullable = false, length = 32)
	private String brand;
	
	@Column(name = "model", nullable = false, length = 16)
	private String model;
	
	@Column(name = "mtyp_code", nullable = false, length = 36)
	private String machineTypeCode;
	
	@Column(name = "min_driver_ver", nullable = false, length = 16)
	private String minDriverversion;
	
	@Column(name = "descr", length = 256)
	private String description;
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "mtyp_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private MachineType machineType;

}
