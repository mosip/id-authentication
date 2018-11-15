package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reason_category", schema = "master")
public class ReasonCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440279821197074364L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name")
	private String name;

	@Column(name = "descr")
	private String description;
	
	@Column(name = "lang_code")
	private String languageCode;

	@Column(name = "is_active")
	private Boolean isActive;

	/**
	 * Field to hold creator name
	 */
	@Column(name = "cr_by")
	private String createdBy;

	/**
	 * Field to hold created dated and time
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdtime;

	/**
	 * Field to hold updater name
	 */
	@Column(name = "upd_by")
	private String updatedBy;

	/**
	 * Field to hold updated name and date
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtime;

	/**
	 * Field to hold true or false for is deleted
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Field to hold deleted date and time
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;

	@OneToMany(mappedBy = "reasonCategoryCode", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReasonList> reasons = new HashSet<>();

}
