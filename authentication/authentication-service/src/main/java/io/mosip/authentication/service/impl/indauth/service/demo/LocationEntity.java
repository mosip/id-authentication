package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "location", schema = "master")
@IdClass(LocationEntityPK.class)
public class LocationEntity {

	@Id
	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "hierarchy_level")
	private String hierarchylevel;

	@Column(name = "hierarchy_level_name")
	private String hierarchylevelname;

	@Column(name = "parent_loc_code")
	private String parentloccode;

	@Column(name = "lang_code")
	private String langcode;

	@Column(name = "is_active")
	private boolean isActive;

	@Column(name = "cr_by")
	String createdBy;

	@Column(name = "cr_dtimesz")
	Date createdOn;

	@Column(name = "upd_by")
	String updatedBy;

	@Column(name = "upd_dtimesz")
	Date updatedOn;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimesz")
	Date deletedOn;

}
