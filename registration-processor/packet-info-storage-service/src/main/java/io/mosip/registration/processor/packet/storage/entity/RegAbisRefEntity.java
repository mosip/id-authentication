package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "regid_abisrefid", schema = "regprc")
public class RegAbisRefEntity extends BasePacketEntity<RegAbisRefPkEntity> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	@Column(name = "cr_by", nullable = false)
	private String updatedBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime updateDtimes;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

}
