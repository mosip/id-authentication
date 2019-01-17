/* 
 * Copyright
 * 
 
package io.mosip.preregistration.transliteration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

*//**
 * This entity class defines the database table details for Transliteration application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 *//*
@Entity
@Table(name="Language_Id",schema="prereg")
@Getter
@Setter
@NoArgsConstructor
public class LanguageIdEntity implements Serializable {
	
	
	*//** The Constant serialVersionUID. *//*
	private static final long serialVersionUID = 3287888371458080052L;

	*//** The Language Id. *//*
	@Column(name = "Language_id", nullable = false)
	@Id
	String languageId;
	
	*//** The from language. *//*
	@Column(name = "From_Language", nullable = false)
	String fromLang;
	
	*//** The to language. *//*
	@Column(name = "To_Language", nullable = false)
	String toLang;

}
*/