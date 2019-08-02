package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for Individual type
 * 
 * @author Bal Vikash Sharma
 * @author Sidhant Agarwal
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "individual_type", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class IndividualType extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for individual type code
	 */
	@Id
	@Column(name = "code")
	private String code;

	@Id
	@Column(name = "lang_code", nullable = false)
	private String langCode;

	/**
	 * Field for individual type name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String name;

}