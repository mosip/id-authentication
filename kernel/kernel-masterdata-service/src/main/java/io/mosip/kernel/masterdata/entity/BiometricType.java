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
 * @author Neha
 * @since 1.0.0
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "biometric_type", schema = "master")
public class BiometricType extends BaseEntity implements Serializable {

	/**
	 * Generated serialization id
	 */
	private static final long serialVersionUID = 4605128758645778470L;

	@Id
	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "descr")
	private String description;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

}
