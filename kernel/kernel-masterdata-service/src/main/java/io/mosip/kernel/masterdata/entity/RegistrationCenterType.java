package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class for RegistrationCenterType.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Data
@Entity
@Table(name = "reg_center_type", schema = "master")
@IdClass(CodeAndLanguageCodeId.class)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RegistrationCenterType extends BaseEntity implements Serializable {

	/**
	 * Serializable version ID.
	 */
	private static final long serialVersionUID = 7869240207930949234L;

	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", length = 36)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3)) })
	/**
	 * The code of the registration center type.
	 */
	private String code;

	/**
	 * The language code of the registration center type.
	 */
	private String langCode;

	/**
	 * The name of the registration center type.
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

	/**
	 * The description of the registration center type.
	 */
	@Column(name = "descr", length = 128)
	private String descr;
}
