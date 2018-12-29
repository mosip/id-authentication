package io.mosip.preregistration.transliteration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="Language_Id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LanguageIdEntity {
	
	@Column(name = "Language_id", nullable = false)
	@Id
	String languageId;
	
	@Column(name = "From_Language", nullable = false)
	String fromLang;
	
	@Column(name = "To_Language", nullable = false)
	String toLang;

}
