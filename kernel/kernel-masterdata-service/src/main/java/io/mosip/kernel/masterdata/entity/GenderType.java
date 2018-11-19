package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Gender entity mapped according to DB
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gender", schema = "master")
public class GenderType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "genderCode", column = @Column(name = "code", nullable = false, length = 16)),
			@AttributeOverride(name = "genderName", column = @Column(name = "name", nullable = false, length = 64)) })
	@Column(name = "code", unique = true, nullable = false, length = 16)
	private GenderTypeId id;

	@Column(name = "lang_code", unique = true, nullable = false, length = 3)
	private String languageCode;

}
