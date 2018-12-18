package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.RegistrationCommonFields;

@Entity
@Table(name = "reason_list", schema = "reg")
public class ReasonList extends RegistrationCommonFields implements Serializable {

	private static final long serialVersionUID = -572990183711593868L;
	
	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "rsncat_code")
	private ReasonCategory reasonCategoryCode;

	@Column(name = "lang_code")
	private String langCode;

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

	/**
	 * @return the reasonCategoryCode
	 */
	public ReasonCategory getReasonCategoryCode() {
		return reasonCategoryCode;
	}

	/**
	 * @param reasonCategoryCode the reasonCategoryCode to set
	 */
	public void setReasonCategoryCode(ReasonCategory reasonCategoryCode) {
		this.reasonCategoryCode = reasonCategoryCode;
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

}
