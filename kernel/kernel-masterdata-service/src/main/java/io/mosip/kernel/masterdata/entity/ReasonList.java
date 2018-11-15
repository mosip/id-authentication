package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "reason_list",schema="master")
public class ReasonList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -572990183711593868L;
	@Id
	@Column(name = "code", nullable = false, length = 36)
	private String code;

	@Column(name = "name", nullable = false, length = 64)
	private String name;

	@Column(name = "descr", length = 256)
	private String description;
    
	@Id
	@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "rsncat_code")
	private ReasonCategory reasonCategoryCode;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@Column(name = "is_active")
	private Boolean isActive;

	/**
	 * Field to hold creator name
	 */
	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;

	/**
	 * Field to hold created dated and time
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;

	/**
	 * Field to hold updater name
	 */
	@Column(name = "upd_by", length = 32)
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


}
