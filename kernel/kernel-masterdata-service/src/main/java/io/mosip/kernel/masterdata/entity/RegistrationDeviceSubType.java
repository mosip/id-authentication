package io.mosip.kernel.masterdata.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Megha Tanga
 *
 */

@Entity
@Table(name = "reg_device_sub_type", schema = "master")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDeviceSubType extends BaseEntity {
	
	
	@Id
	@Column(name="code", nullable=false, length=36)
	String code;
	
	@Column(name="dtyp_code", nullable=false, length=36)
	String regDeviceTypeCode;
	
	@Column(name="name", nullable=false, length=64)
	String name;
	
	@Column(name="descr", length=512)
	String description;

}
