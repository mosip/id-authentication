package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * Entity for language
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@Table(name = "language", schema = "master")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5585825705521742941L;

	/**
	 * Field for language code
	 */
	@Id
	@Column(name = "code", unique = true, nullable = false, length = 3)
	private String languageCode;

	/**
	 * Field for language name
	 */
	@Column(name = "name", nullable = false, length = 64)
	private String languageName;

	/**
	 * Field for language family
	 */
	@Column(name = "family", length = 64)
	private String languageFamily;

	/**
	 * Field for language native name
	 */
	@Column(name = "native_name", length = 64)
	private String nativeName;

	/**
	 * Field for is active
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;

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
