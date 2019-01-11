package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * Entity class mapping title to master data
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
@NamedNativeQueries({
	@NamedNativeQuery(name = "MasterTitle.getThroughLanguageCode", query = "select code, name, descr , lang_code , is_active , cr_by , cr_dtimes , upd_by , upd_dtimes ,is_deleted , del_dtimes from master.title where lang_code = ?1 and is_deleted = 'f' or is_deleted IS NULL ", resultClass = MasterTitle.class) })
@Entity
@Table(name = "title", schema = "reg")
public class MasterTitle extends MasterSyncBaseEntity implements Serializable {

	private static final long serialVersionUID = 1323331283383315822L;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "code", column = @Column(name = "code")),
			@AttributeOverride(name = "lang_code", column = @Column(name = "lang_code")) })
	
	@Column(name = "code")
	private CodeAndLanguageCodeID id;

	@Column(name = "name")
	private String titleName;

	@Column(name = "descr")
	private String titleDescription;

	/**
	 * @return the id
	 */
	public CodeAndLanguageCodeID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(CodeAndLanguageCodeID id) {
		this.id = id;
	}

	/**
	 * @return the titleName
	 */
	public String getTitleName() {
		return titleName;
	}

	/**
	 * @param titleName the titleName to set
	 */
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	/**
	 * @return the titleDescription
	 */
	public String getTitleDescription() {
		return titleDescription;
	}

	/**
	 * @param titleDescription the titleDescription to set
	 */
	public void setTitleDescription(String titleDescription) {
		this.titleDescription = titleDescription;
	}

	

	
	

}
