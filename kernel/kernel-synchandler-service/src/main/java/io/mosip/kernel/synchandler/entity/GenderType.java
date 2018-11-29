package io.mosip.kernel.synchandler.entity;

import java.io.Serializable;

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
@IdClass(GenderTypeId.class)
public class GenderType extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	
	
	@Id
	@Column(name = "code", unique = true, nullable = false, length = 16)
	private String genderCode;
    @Id
	@Column(name = "name", unique = true, nullable = false, length = 64)
	private String genderName;

	@Column(name = "lang_code", unique = true, nullable = false, length = 3)
	private String languageCode;

}
