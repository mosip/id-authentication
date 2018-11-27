package io.mosip.kernel.masterdata.entity;

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
	

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "cr_by")
	private String createdBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime createdtimes;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtimes;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;

}
