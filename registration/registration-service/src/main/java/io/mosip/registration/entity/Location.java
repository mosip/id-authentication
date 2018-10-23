package io.mosip.registration.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Entity
@Table(schema = "master", name = "location")
@Data
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
	private OffsetDateTime createdDate;
	@Column(name = "upd_by", length = 24, nullable = true)
	private String updatedBy;
	@Column(name = "upd_dtimesz")
	private OffsetDateTime updatedTimesZone;
	@Column(name = "is_deleted", nullable = true)
	private boolean isDeleted;
	@Column(name = "del_dtimesz", nullable = true)
	private OffsetDateTime deletedTimesZone;

}
