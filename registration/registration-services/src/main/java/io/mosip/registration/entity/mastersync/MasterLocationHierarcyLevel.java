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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((hierarchyLevel == null) ? 0 : hierarchyLevel.hashCode());
		result = prime * result + ((hierarchyName == null) ? 0 : hierarchyName.hashCode());
		result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentLocCode == null) ? 0 : parentLocCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MasterLocationHierarcyLevel other = (MasterLocationHierarcyLevel) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (hierarchyLevel == null) {
			if (other.hierarchyLevel != null)
				return false;
		} else if (!hierarchyLevel.equals(other.hierarchyLevel))
			return false;
		if (hierarchyName == null) {
			if (other.hierarchyName != null)
				return false;
		} else if (!hierarchyName.equals(other.hierarchyName))
			return false;
		if (languageCode == null) {
			if (other.languageCode != null)
				return false;
		} else if (!languageCode.equals(other.languageCode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentLocCode == null) {
			if (other.parentLocCode != null)
				return false;
		} else if (!parentLocCode.equals(other.parentLocCode))
			return false;
		return true;
	}
}

