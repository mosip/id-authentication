package io.mosip.registration.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * This Entity Class contains list of document categories[Proof Of Address, Proof Of Identity...] 
 * which will be displayed in UI with respect to language code.
 * The data for this table will come through sync from server master table 
 * 
 * @author Sreekar Chukka
 * @version 1.0
 */
@Entity
@Table(schema = "reg", name = "doc_category")
@IdClass(CodeAndLanguageCodeID.class)
public class DocumentCategory extends RegistrationCommonFields {
	/**
	 * Generated serialization id
	 */
	private static final long serialVersionUID = 1582360946027855765L;
	@Id
	@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code")),
			@AttributeOverride(name = "langCode", column = @Column(name = "lang_code")) })
	@OneToMany(mappedBy = "docCategoryCode")
	private String code;
	@OneToMany(mappedBy = "langCode")
	private String langCode;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
