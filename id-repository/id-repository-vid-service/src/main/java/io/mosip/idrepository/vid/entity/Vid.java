package io.mosip.idrepository.vid.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The Entity for Vid.
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

	public Vid(String id, String vid, String uinHash, String uin, String vidTypeCode, LocalDateTime generatedDTimes,
			LocalDateTime expiryDTimes, String statusCode, String createdBy, LocalDateTime createdDTimes,
			String updatedBy, LocalDateTime updatedDTimes, boolean isDeleted, LocalDateTime deletedDTimes) {
		this.id = id;
		this.vid = vid;
		this.uinHash = uinHash;
		this.uin = uin;
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
	
	/** The uin value */
	private String uin;

	/** The value to hold vid Type Code */
	@Column(name = "vidtyp_code")
	private String vidTypeCode;

	/** The value to hold generated DTimes */
	@Column(name = "generated_dtimes")
	private LocalDateTime generatedDTimes;

	/** The value to hold expiry DTimes */
	@Column(name = "expiry_dtimes")
	private LocalDateTime expiryDTimes;

	/** The value to hold status Code */
	private String statusCode;

	/** The value to hold created By */
	@Column(name = "cr_by")
	private String createdBy;

	/** The value to hold created DTimes */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDTimes;

	/** The value to hold updated By */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The value to hold updated Time */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDTimes;

	/** The boolean of isDeleted */
	private boolean isDeleted;

	/** The value to hold deleted DTimes */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDTimes;

}
