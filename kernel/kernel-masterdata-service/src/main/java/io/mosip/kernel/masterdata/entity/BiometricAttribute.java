package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

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
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "biometric_attribute", schema = "master")
public class BiometricAttribute extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1302520630931393544L;
	@Id
	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "bmtyp_code", nullable = false)
	private String biometricTypeCode;

	@Column(name = "lang_code", nullable = false)
	private String langCode;

}
