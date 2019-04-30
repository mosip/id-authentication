package io.mosip.idrepository.vid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
@Table(name = "vid", schema = "idmap")
@Entity
public class Vid {

	/** The Id value */
	@Id
	private String id;

	/** The vid value */
	private String vid;

	public Vid(String id, String vid, String uinHash, String vidTypeCode, LocalDateTime generatedDTimes,
			LocalDateTime expiryDTimes, String statusCode, String createdBy, LocalDateTime createdDTimes,
			String updatedBy, LocalDateTime updatedDTimes, boolean isDeleted, LocalDateTime deletedDTimes) {
		super();
		this.id = id;
		this.vid = vid;
		this.uinHash = uinHash;
		this.vidTypeCode = vidTypeCode;
		this.generatedDTimes = generatedDTimes;
		this.expiryDTimes = expiryDTimes;
		this.statusCode = statusCode;
		this.createdBy = createdBy;
		this.createdDTimes = createdDTimes;
		this.updatedBy = updatedBy;
		this.updatedDTimes = updatedDTimes;
		this.isDeleted = isDeleted;
		this.deletedDTimes = deletedDTimes;
	}

	/** The uin Hash value */
	private String uinHash;

	/** The vid Type Code */
	@Column(name = "vidtyp_code")
	private String vidTypeCode;

	/** The generated DTimes */
	@Column(name = "generated_dtimes")
	private LocalDateTime generatedDTimes;

	/** The expiry DTimes */
	@Column(name = "expiry_dtimes")
	private LocalDateTime expiryDTimes;

	/** The status Code */
	private String statusCode;

	/** The created By */
	@Column(name = "cr_by")
	private String createdBy;

	/** The created DTimes */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDTimes;

	/** The updated By */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The updated Time */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDTimes;

	/** The boolean of isDeleted */
	private boolean isDeleted;

	/** The deleted DTimes */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDTimes;

}
