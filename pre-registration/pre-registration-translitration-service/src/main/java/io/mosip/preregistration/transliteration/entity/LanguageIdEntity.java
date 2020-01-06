/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity class defines the database table details for Transliteration application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Entity
@Table(name="language_transliteration",schema="prereg")
@Getter
@Setter
@NoArgsConstructor
public class LanguageIdEntity implements Serializable {
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3287888371458080052L;

	/** The Language Id. */
	@Column(name = "lang_id", nullable = false)
	@Id
	String languageId;
	
	/** The from language. */
	@Column(name = "lang_from_code", nullable = false)
	String fromLang;
	
	/** The to language. */
	@Column(name = "lang_to_code", nullable = false)
	String toLang;

	@Column(name="cr_by",nullable=false)
	String createdBy;
	
	@Column(name="cr_dtimes",nullable=false)
	LocalDateTime createdDateTime;
	
	@Column(name="upd_by",nullable=false)
	String updatedBy;
	
	@Column(name="upd_dtimes",nullable=false)
	LocalDateTime updatedDateTime;
}
