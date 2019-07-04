package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.mosip.registration.entity.id.CodeAndLanguageCodeID;

/**
 * This Entity Class contains list of reason categories[Client Rejection, Manual Adjudication...] 
 * with respect to language code.
 * The data for this table will come through sync from server master table.
 *
 * @author Sreekar chukka
 * @since 1.0.0
 */

@Entity
@Table(name = "reason_category", schema = "reg")
@IdClass(CodeAndLanguageCodeID.class)
public class ReasonCategory extends RegistrationCommonFields implements Serializable {

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
	
	@Column(name="is_deleted")
	protected Boolean isDeleted;
	
	@Column(name="del_dtimes")
	protected Timestamp delDtimes;

	@OneToMany(mappedBy = "reasonCategory", cascade = CascadeType.ALL)
	private List<ReasonList> reasonList = new ArrayList<>();

	public void addReasonList(ReasonList list) {
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
	public List<ReasonList> getReasonList() {
		return reasonList;
	}

	/**
	 * @param reasonList the reasonList to set
	 */
	public void setReasonList(List<ReasonList> reasonList) {
		this.reasonList = reasonList;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Timestamp getDelDtimes() {
		return delDtimes;
	}

	public void setDelDtimes(Timestamp delDtimes) {
		this.delDtimes = delDtimes;
	}
	
	

}
