package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Entity
@Table(schema = "master", name = "location")
public class Location implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1862979882831303893L;
	@EmbeddedId
	private GenericId locationId;
	@Column(name = "name", length = 128, nullable = false)
	private String name;
	@Column(name = "heirarchy_level", nullable = false)
	private int heirarchyLevel;
	@Column(name = "heirarchy_level_name", length = 64, nullable = false)
	private String heirarchyLevelName;
	@Column(name = "parent_loc_code", length = 32, nullable = true)
	private String parentLocationCode;
	@Column(name = "lang_code", length = 3, nullable = false)
	private String languageCode;
	@Column(name = "cr_by", length = 24, nullable = false)
	private String createdBy;
	@Column(name = "cr_dtimesz", nullable = false)
	private Timestamp createdDate;
	@Column(name = "upd_by", length = 24, nullable = true)
	private String updatedBy;
	@Column(name = "upd_dtimesz")
	private Timestamp updatedTimesZone;
	@Column(name = "is_deleted", nullable = true)
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimesz", nullable = true)
	private Timestamp deletedTimesZone;

	/**
	 * @return the locationId
	 */
	public GenericId getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId
	 *            the locationId to set
	 */
	public void setLocationId(GenericId locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the heirarchyLevel
	 */
	public int getHeirarchyLevel() {
		return heirarchyLevel;
	}

	/**
	 * @param heirarchyLevel
	 *            the heirarchyLevel to set
	 */
	public void setHeirarchyLevel(int heirarchyLevel) {
		this.heirarchyLevel = heirarchyLevel;
	}

	/**
	 * @return the heirarchyLevelName
	 */
	public String getHeirarchyLevelName() {
		return heirarchyLevelName;
	}

	/**
	 * @param heirarchyLevelName
	 *            the heirarchyLevelName to set
	 */
	public void setHeirarchyLevelName(String heirarchyLevelName) {
		this.heirarchyLevelName = heirarchyLevelName;
	}

	/**
	 * @return the parentLocationCode
	 */
	public String getParentLocationCode() {
		return parentLocationCode;
	}

	/**
	 * @param parentLocationCode
	 *            the parentLocationCode to set
	 */
	public void setParentLocationCode(String parentLocationCode) {
		this.parentLocationCode = parentLocationCode;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode
	 *            the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Timestamp getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedTimesZone
	 */
	public Timestamp getUpdatedTimesZone() {
		return updatedTimesZone;
	}

	/**
	 * @param updatedTimesZone
	 *            the updatedTimesZone to set
	 */
	public void setUpdatedTimesZone(Timestamp updatedTimesZone) {
		this.updatedTimesZone = updatedTimesZone;
	}

	/**
	 * @return the isDeleted
	 */
	public Boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedTimesZone
	 */
	public Timestamp getDeletedTimesZone() {
		return deletedTimesZone;
	}

	/**
	 * @param deletedTimesZone
	 *            the deletedTimesZone to set
	 */
	public void setDeletedTimesZone(Timestamp deletedTimesZone) {
		this.deletedTimesZone = deletedTimesZone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result + ((deletedTimesZone == null) ? 0 : deletedTimesZone.hashCode());
		result = prime * result + heirarchyLevel;
		result = prime * result + ((heirarchyLevelName == null) ? 0 : heirarchyLevelName.hashCode());
		result = prime * result + (isDeleted ? 1231 : 1237);
		result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentLocationCode == null) ? 0 : parentLocationCode.hashCode());
		result = prime * result + ((updatedBy == null) ? 0 : updatedBy.hashCode());
		result = prime * result + ((updatedTimesZone == null) ? 0 : updatedTimesZone.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		if (deletedTimesZone == null) {
			if (other.deletedTimesZone != null)
				return false;
		} else if (!deletedTimesZone.equals(other.deletedTimesZone))
			return false;
		if (heirarchyLevel != other.heirarchyLevel)
			return false;
		if (heirarchyLevelName == null) {
			if (other.heirarchyLevelName != null)
				return false;
		} else if (!heirarchyLevelName.equals(other.heirarchyLevelName))
			return false;
		if (isDeleted != other.isDeleted)
			return false;
		if (languageCode == null) {
			if (other.languageCode != null)
				return false;
		} else if (!languageCode.equals(other.languageCode))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentLocationCode == null) {
			if (other.parentLocationCode != null)
				return false;
		} else if (!parentLocationCode.equals(other.parentLocationCode))
			return false;
		if (updatedBy == null) {
			if (other.updatedBy != null)
				return false;
		} else if (!updatedBy.equals(other.updatedBy))
			return false;
		if (updatedTimesZone == null) {
			if (other.updatedTimesZone != null)
				return false;
		} else if (!updatedTimesZone.equals(other.updatedTimesZone))
			return false;
		return true;
	}

}
