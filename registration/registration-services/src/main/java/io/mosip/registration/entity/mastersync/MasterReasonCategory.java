package io.mosip.registration.entity.mastersync;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * @author Sreekar chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "reason_category", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class MasterReasonCategory extends MasterSyncBaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;

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

	@OneToMany(mappedBy = "reasonCategory", cascade = CascadeType.ALL)
	private List<MasterReasonList> reasonList = new ArrayList<>();

	public void addReasonList(MasterReasonList list) {
		list.setReasonCategory(this);
		this.reasonList.add(list);
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
	 * @return the reasonList
	 */
	public List<MasterReasonList> getReasonList() {
		return reasonList;
	}

	/**
	 * @param reasonList the reasonList to set
	 */
	public void setReasonList(List<MasterReasonList> reasonList) {
		this.reasonList = reasonList;
	}
	
	

}
