package io.mosip.registration.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * The Entity class for SyncControl.
 * @author Mahesh Kumar
 *
 */
@Entity
@Table(schema="REG", name="SYNC_CONTROL")
@Data
public class SyncControl {

	@Id
	@Column(name="SJOB_ID", length=32, nullable=false, updatable=false)
	private String sJobId;
	@Column(name="MACHN_ID", length=64, nullable=true, updatable=true)
	private String machnId;
	@Column(name="CNTR_ID", length=32, nullable=true, updatable=true)
	private String cntrId;
	@Column(name="LAST_SYNC_DTIMEZ", nullable=false, updatable=false)
	private OffsetDateTime lastSyncDtimez;
	@Column(name="SYNCT_ID", length=32, nullable=false, updatable=false)
	private String synctId;
	@Column(name="LANG_CODE", length=3, nullable=true, updatable=true)
	private String langCode;
	@Column(name="IS_ACTIVE", nullable=false, updatable=true)
	@Type(type= "true_false")
	private boolean isActive;
	@Column(name="CR_BY", length=24, nullable=false, updatable=true)
	private String crBy;
	@Column(name="CR_DTIMESZ", nullable=false, updatable=true)
	private OffsetDateTime crDtime;
	@Column(name="UPD_BY", length=24, nullable=true, updatable=true)
	private String updBy;
	@Column(name="UPD_DTIMESZ", nullable=true, updatable=true)
	private OffsetDateTime updDtime;
	@Column(name="IS_DELETED", nullable=true, updatable=true)
	private boolean isDeleted;
	@Column(name="DEL_DTIMESZ", nullable=true, updatable=true)
	private OffsetDateTime delDtime;
}
