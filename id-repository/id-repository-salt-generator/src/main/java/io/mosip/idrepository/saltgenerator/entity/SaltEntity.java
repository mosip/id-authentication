package io.mosip.idrepository.saltgenerator.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Manoj SP
 *
 */
@Entity
@Data
@Table
public class SaltEntity {

	@Id
	@Column(updatable = false, nullable = false, unique = true)
	private Long id;

	@Column(updatable = false, nullable = false, unique = true)
	private String salt;

	@Column(name = "cr_by", updatable = true, nullable = false, unique = false)
	private String createdBy;

	@Column(name = "cr_dtimes", updatable = true, nullable = false, unique = true)
	private LocalDateTime createDtimes;

	@Column(name = "upd_by", updatable = true, nullable = false, unique = false)
	private String updatedBy;

	@Column(name = "upd_dtimes", updatable = true, nullable = false, unique = true)
	private LocalDateTime updatedDtimes;

}
