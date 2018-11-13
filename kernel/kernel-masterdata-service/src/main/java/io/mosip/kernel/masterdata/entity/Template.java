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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "template", schema = "master")
public class Template implements Serializable {

	/**
	 * Generated serialization id
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, unique = true, length = 36)
	private String id;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@Column(name = "descr", length = 256)
	private String description;

	@Column(name = "file_format_code", length = 36)
	private String fileFormatCode;

	@Column(name = "model", length = 128)
	private String model;

	@Column(name = "file_txt", length = 4086)
	private String fileText;

	@Column(name = "module_id", length = 36)
	private String moduleId;

	@Column(name = "module_name", length = 128)
	private String moduleName;

	@Column(name = "template_typ_code", length = 36)
	private String templateTypeCode;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String languageCode;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 32)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdTimestamp;

	@Column(name = "upd_by", length = 32)
	private String updatedBy;

	@Column(name = "upd_dtimes", length = 32)
	private LocalDateTime updatedTimestamp;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedTimestamp;
}
