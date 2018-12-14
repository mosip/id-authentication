package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.mosip.kernel.syncdata.entity.id.CodeAndLanguageCodeID;
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
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gender", schema = "master")
@IdClass(CodeAndLanguageCodeID.class)
public class Gender extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1323022736883315822L;

	
	
	@Id
	@Column(name = "code", unique = true, nullable = false, length = 16)
	private String code;
    
	@Column(name = "name", unique = true, nullable = false, length = 64)
	private String genderName;

	@Id
	@Column(name = "lang_code", unique = true, nullable = false, length = 3)
	private String langCode;

}
