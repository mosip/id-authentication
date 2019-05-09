package io.mosip.idrepository.vid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The Entity for UinSalt.
 * 
 * @author Prem Kumar
 *
 */
@Data
@Table(name = "uin_hash_salt", schema = "idmap")
@Entity
public class UinSalt {

	/** The Id Value */
	@Id
	private Long id;

	/** The value to hold Salt value */
	private String salt;

	public UinSalt(Long id, String salt, String createdBy, LocalDateTime createdDTimes, String updatedBy,
			LocalDateTime updatedDTimes) {
		super();
		this.id = id;
		this.salt = salt;
		this.createdBy = createdBy;
		this.createdDTimes = createdDTimes;
		this.updatedBy = updatedBy;
		this.updatedDTimes = updatedDTimes;
	}

	/** The value to hold created By */
	@Column(name = "cr_by")
	private String createdBy;

	/** The value to hold created DTimes */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDTimes;

	/** The value to hold updated By */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The value to hold updated Times */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDTimes;

}
