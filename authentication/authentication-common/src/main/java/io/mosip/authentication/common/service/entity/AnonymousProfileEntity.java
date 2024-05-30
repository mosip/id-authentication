package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Loganathan Sekar
 *
 */
@NoArgsConstructor
@Data
@Table(name = "anonymous_profile", schema = "ida")
@Entity
public class AnonymousProfileEntity {
	
	@Id
	@NotNull
	private String id;
	
	@NotNull
	private String profile;

	@NotNull
	@Column(name = "cr_by")
	private String crBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;
	
}
