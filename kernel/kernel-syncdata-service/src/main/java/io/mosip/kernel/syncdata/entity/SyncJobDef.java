package io.mosip.kernel.syncdata.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#toString()
 */
@Data

/*
 * (non-Javadoc)
 * 
 * @see io.mosip.kernel.syncdata.entity.BaseEntity#hashCode()
 */
@EqualsAndHashCode(callSuper = false)

/**
 * Instantiates a new sync job def.
 */
@NoArgsConstructor

/**
 * Instantiates a new sync job def.
 *
 * @param id
 *            the id
 * @param name
 *            the name
 * @param apiName
 *            the api name
 * @param parentSyncJobId
 *            the parent sync job id
 * @param syncFreq
 *            the sync freq
 * @param lockDuration
 *            the lock duration
 * @param langCode
 *            the lang code
 */
@AllArgsConstructor

/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
//@Entity
//@Table(schema = "kernel", name = "sync_job_def")
public class SyncJobDef extends BaseEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6067466052839963371L;

	/** The id. */
	@Id
	@Column(name = "id", length = 36, nullable = false)
	private String id;

	/** The name. */
	@Column(name = "name", length = 64, nullable = false)
	private String name;

	/** The api name. */
	@Column(name = "api_name", length = 64)
	private String apiName;

	/** The parent sync job id. */
	@Column(name = "parent_syncjob_id", length = 36)
	private String parentSyncJobId;

	/** The sync freq. */
	@Column(name = "sync_freq", length = 64)
	private String syncFreq;

	/** The lock duration. */
	@Column(name = "lock_duration", length = 36)
	private String lockDuration;

	/** The lang code. */
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;

}
