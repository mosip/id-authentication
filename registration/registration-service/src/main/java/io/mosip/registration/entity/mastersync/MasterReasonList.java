package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeLangCodeAndRsnCatCodeID;

/**
 * @author Sreekar chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "reason_list", schema = "reg")
@IdClass(CodeLangCodeAndRsnCatCodeID.class)
public class MasterReasonList extends MasterSyncBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -572990183711593868L;

	@Id
	@Column(name = "rsncat_code")
	private String rsnCatCode;
	@Id
	@Column(name = "code")
	private String code;
	@Id
	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;

	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "rsncat_code", referencedColumnName = "code", insertable = false, updatable = false),
			@JoinColumn(name = "lang_code", referencedColumnName = "lang_code", insertable = false, updatable = false) })
	private MasterReasonCategory reasonCategory;

	/**
	 * @return the rsnCatCode
	 */
	public String getRsnCatCode() {
		return rsnCatCode;
	}

	/**
	 * @param rsnCatCode the rsnCatCode to set
	 */
	public void setRsnCatCode(String rsnCatCode) {
		this.rsnCatCode = rsnCatCode;
	}

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

	/**
	 * @return the reasonCategory
	 */
	public MasterReasonCategory getReasonCategory() {
		return reasonCategory;
	}

	/**
	 * @param reasonCategory the reasonCategory to set
	 */
	public void setReasonCategory(MasterReasonCategory reasonCategory) {
		this.reasonCategory = reasonCategory;
	}
	
	

}
