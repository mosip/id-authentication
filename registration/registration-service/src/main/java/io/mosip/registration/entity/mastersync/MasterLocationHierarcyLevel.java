package io.mosip.registration.entity.mastersync;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author Sreekar chukka
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "location", schema = "reg")
public class MasterLocationHierarcyLevel extends MasterSyncBaseEntity implements Serializable {

	private static final long serialVersionUID = -5585825455521742941L;

	@Id
	@OneToOne
	@JoinColumn(name = "code")
	private MasterRegistrationCenter code;

	@Column(name = "name")
	private String name;

	@Column(name = "hierarchy_level")
	private Integer hierarchyLevel;

	@Column(name = "hierarchy_level_name")
	private String hierarchyName;

	@Column(name = "parent_loc_code")
	private String parentLocCode;

	@Column(name = "lang_code")
	private String languageCode;

	/**
	 * @return the code
	 */
	public MasterRegistrationCenter getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(MasterRegistrationCenter code) {
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
	 * @return the hierarchyLevel
	 */
	public Integer getHierarchyLevel() {
		return hierarchyLevel;
	}

	/**
	 * @param hierarchyLevel the hierarchyLevel to set
	 */
	public void setHierarchyLevel(Integer hierarchyLevel) {
		this.hierarchyLevel = hierarchyLevel;
	}

	/**
	 * @return the hierarchyName
	 */
	public String getHierarchyName() {
		return hierarchyName;
	}

	/**
	 * @param hierarchyName the hierarchyName to set
	 */
	public void setHierarchyName(String hierarchyName) {
		this.hierarchyName = hierarchyName;
	}

	/**
	 * @return the parentLocCode
	 */
	public String getParentLocCode() {
		return parentLocCode;
	}

	/**
	 * @param parentLocCode the parentLocCode to set
	 */
	public void setParentLocCode(String parentLocCode) {
		this.parentLocCode = parentLocCode;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	

}

