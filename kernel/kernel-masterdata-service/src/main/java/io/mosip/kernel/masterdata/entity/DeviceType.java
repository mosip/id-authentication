package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "device_type", schema = "master")
@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceType extends BaseEntity implements Serializable{
	
	private static final long serialVersionUID = -8541947587557590379L;
	
	@Id
	@Column(name = "code", nullable = false, length = 128)
	private String code;
	
	
	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	/**
	 * Field for language code
	 */
	
	
	@Column(name = "descr", length = 256)
	private String description;
	
}
