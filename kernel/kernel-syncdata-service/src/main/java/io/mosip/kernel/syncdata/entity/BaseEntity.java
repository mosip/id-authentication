package io.mosip.kernel.syncdata.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "cr_by", nullable = false, length = 256)
	private String createdBy;

	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdDateTime;

	@Column(name = "upd_by", length = 256)
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

}
