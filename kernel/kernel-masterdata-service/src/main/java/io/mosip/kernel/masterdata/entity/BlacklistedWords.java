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

@Entity
@Table(name = "blacklisted_words", schema = "master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedWords implements Serializable {

	private static final long serialVersionUID = -402658536057675404L;

	@Id
	@Column(name = "word", nullable = false, length = 128)
	private String word;

	@Column(name = "descr", length = 256)
	private String description;

	@Column(name = "lang_code", nullable = false, length = 3)
	private String langCode;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by", length = 24)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

}
