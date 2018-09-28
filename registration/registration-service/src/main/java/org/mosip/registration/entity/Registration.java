package org.mosip.registration.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * Registration entity details
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
@Entity
@Table(schema="REG", name = "REGISTRATION")
public class Registration implements Serializable {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 2163134932981912223L;
	
	@Id
	@Column(name="ID", length=28, nullable=false, updatable=false)
	private String id;
	@Column(name="REG_TYPE", length=64, nullable=false, updatable=true)
	private String regType;
	@Column(name="REF_REG_ID", length=28, nullable=true, updatable=true)
	private String refRegId;
	@Column(name="INDV_NAME", length=128, nullable=false, updatable=true)
	private String individualName;
	@Column(name="STATUS_CODE", length=64, nullable=false, updatable=true)
	private String statusCode;
	@Column(name="LANG_CODE", length=3, nullable=false, updatable=true)
	private String langCode;
	@Column(name="STATUS_COMMENT", length=256, nullable=true, updatable=true)
	private String statusComment;
	@Column(name="STATUS_DTIMES", nullable=true, updatable=true)
	private OffsetDateTime statusTimestamp;
	@Column(name="ACK_FILENAME", length=128, nullable=true, updatable=true)
	private String ackFilename;
	@Column(name="CLIENT_STATUS_CODE", length=64, nullable=true, updatable=true)
	private String clientStatusCode;
	@Column(name="SERVER_STATUS_CODE", length=64, nullable=true, updatable=true)
	private String serverStatusCode;
	@Column(name="STATUS_COMMENTS", length=256, nullable=true, updatable=true)
	private String statusComments;
	@Column(name="REG_USR_ID", length=128, nullable=true, updatable=true)
	private String regUsrId;
	@Column(name="REG_CNTR_ID", length=28, nullable=true, updatable=true)
	private String regCntrId;
	@Column(name="APPROVER_USR_ID", length=28, nullable=true, updatable=true)
	private String approverUsrId;
	@Column(name="APPROVER_ROLE_CODE", length=32, nullable=true, updatable=true)
	private String approverRoleCode;
	@Column(name="FILE_SYNC_STATUS", length=64, nullable=true, updatable=true)
	private String fileSyncStatus;
	@Column(name="SYNC_COUNT", nullable=true, updatable=true)
	private Short syncCount;
	@Column(name="SYNC_DTIMES", nullable=true, updatable=true)
	private OffsetDateTime syncTimestamp;
	@Column(name="LATEST_REGTRN_ID", length=64, nullable=true, updatable=true)
	private String latestRegTrnId;
	@Column(name="LATEST_TRN_TYPE_CODE", length=64, nullable=true, updatable=true)
	private String latestTrnTypeCode;
	@Column(name="LATEST_TRN_STATUS_CODE", length=64, nullable=true, updatable=true)
	private String latestTrnStatusCode;
	@Column(name="LATEST_REGTRN_DTIMES", nullable=true, updatable=true)
	private OffsetDateTime latestRegTrnTimestamp;
	@Column(name="IS_ACTIVE", nullable=false, updatable=true)
	@Type(type= "true_false")
	private Boolean isActive;
	@Column(name="CR_BY", length=32, nullable=false, updatable=true)
	private String crBy;
	@Column(name="CR_DTIMES", nullable=false, updatable=true)
	private OffsetDateTime crDtime;
	@Column(name="UPD_BY", length=32, nullable=true, updatable=true)
	private String updBy;
	@Column(name="UPD_DTIMES", nullable=true, updatable=false)
	private OffsetDateTime updDtimes;
	
	@ManyToOne
	@JoinColumn(name = "CR_BY", referencedColumnName = "Id",insertable=false,updatable=false)
	private RegistrationUserDetail userdetail;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="REG_ID")
	List<RegistrationTransaction> registrationTransaction;
	
}
