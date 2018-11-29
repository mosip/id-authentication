package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

/**
 * 
 * @author Brahmananda Reddy
 *
 */

@MappedSuperclass
public class MasterCommonFields {
	@Column(name = "lang_code", length = 3, nullable = false)
	private String languageCode;
	@Column(name = "cr_by", length = 24, nullable = false)
	private String createdBy;
	@Column(name = "cr_dtimesz", nullable = false)
	private Timestamp createdTimesZone;
	@Column(name = "upd_by", length = 24, nullable = true)
	private String updatedBy;
	@Column(name = "upd_dtimesz", nullable = true)
	private Timestamp updatedTimesZone;
	@Column(name = "is_deleted")
	@Type(type = "true_false")
	private Boolean isDeleted;
	@Column(name = "del_dtimesz")
	private Timestamp deletedTimesZone;

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
	 * @return the createdTimesZone
	 */
	public Timestamp getCreatedTimesZone() {
		return createdTimesZone;
	}

	/**
	 * @param createdTimesZone
	 *            the createdTimesZone to set
	 */
	public void setCreatedTimesZone(Timestamp createdTimesZone) {
		this.createdTimesZone = createdTimesZone;
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
		result = prime * result + ((createdTimesZone == null) ? 0 : createdTimesZone.hashCode());
		result = prime * result + ((deletedTimesZone == null) ? 0 : deletedTimesZone.hashCode());
		result = prime * result + (isDeleted ? 1231 : 1237);
		result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
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
		MasterCommonFields other = (MasterCommonFields) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdTimesZone == null) {
			if (other.createdTimesZone != null)
				return false;
		} else if (!createdTimesZone.equals(other.createdTimesZone))
			return false;
		if (deletedTimesZone == null) {
			if (other.deletedTimesZone != null)
				return false;
		} else if (!deletedTimesZone.equals(other.deletedTimesZone))
			return false;
		if (isDeleted != other.isDeleted)
			return false;
		if (languageCode == null) {
			if (other.languageCode != null)
				return false;
		} else if (!languageCode.equals(other.languageCode))
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
