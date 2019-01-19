package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
 * Gender entity mapped according to DB
 * 
 * @author Urvil Joshi
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
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code", nullable = false,length = 16)),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code", nullable = false, length = 3))})


	private String code;
    
	private String langCode;
	
	@Column(name = "name", unique = true, nullable = false, length = 64)
	private String genderName;

	
	

}
