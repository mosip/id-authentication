package io.mosip.registration.entity;

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

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Registration entity details
 * 
 * @author Mahesh Kumar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(schema = "REG", name = "REGISTRATION")
public class Registration extends RegistrationCommonFields {
	@Id
	@Column(name = "ID", length = 28, nullable = false, updatable = false)
	private String id;
	@Column(name = "REG_TYPE", length = 64, nullable = false, updatable = true)
	private String regType;
	@Column(name = "REF_REG_ID", length = 28, nullable = true, updatable = true)
	private String refRegId;
	@Column(name = "STATUS_CODE", length = 64, nullable = false, updatable = true)
	private String statusCode;
	@Column(name = "LANG_CODE", length = 3, nullable = false, updatable = true)
	private String langCode;
	@Column(name = "STATUS_COMMENT", length = 256, nullable = true, updatable = true)
	private String statusComment;
	@Column(name = "STATUS_DTIMES", nullable = true, updatable = true)
	private OffsetDateTime statusTimestamp;
	@Column(name = "ACK_FILENAME", length = 128, nullable = true, updatable = true)
	private String ackFilename;
	@Column(name = "CLIENT_STATUS_CODE", length = 64, nullable = true, updatable = true)
	private String clientStatusCode;
	@Column(name = "SERVER_STATUS_CODE", length = 64, nullable = true, updatable = true)
	private String serverStatusCode;
	@Column(name = "CLIENT_STATUS_DTIME", nullable = true, updatable = true)
	private OffsetDateTime clientStatusTimestamp;
	@Column(name = "SERVER_STATUS_DTIME", nullable = true, updatable = true)
	private OffsetDateTime serverStatusTimestamp;
	@Column(name = "CLIENT_STATUS_COMMENT", length = 256, nullable = true, updatable = true)
	private String clientStatusComments;
	@Column(name = "SERVER_STATUS_COMMENT", length = 256, nullable = true, updatable = true)
	private String serverStatusComments;
	@Column(name = "INDV_NAME", length = 128, nullable = false, updatable = true)
	private String individualName;
	@Column(name = "REG_USR_ID", length = 28, nullable = true, updatable = true)
	private String regUsrId;
	@Column(name = "REG_CNTR_ID", length = 28, nullable = true, updatable = true)
	private String regCntrId;
	@Column(name = "APPROVER_USR_ID", length = 28, nullable = true, updatable = true)
	private String approverUsrId;
	@Column(name = "APPROVER_ROLE_CODE", length = 32, nullable = true, updatable = true)
	private String approverRoleCode;
	@Column(name = "FILE_UPLOAD_STATUS", length = 64, nullable = true, updatable = true)
	private String fileUploadStatus;
	@Column(name = "UPLOAD_COUNT", nullable = true, updatable = true)
	private Short uploadCount;
	@Column(name = "UPLOAD_DTIMES", nullable = true, updatable = true)
	private OffsetDateTime uploadTimestamp;
	@Column(name = "LATEST_REGTRN_ID", length = 64, nullable = true, updatable = true)
	private String latestRegTrnId;
	@Column(name = "LATEST_TRN_TYPE_CODE", length = 64, nullable = true, updatable = true)
	private String latestTrnTypeCode;
	@Column(name = "LATEST_TRN_STATUS_CODE", length = 64, nullable = true, updatable = true)
	private String latestTrnStatusCode;
	@Column(name = "LATEST_TRN_LANG_CODE", length = 3, nullable = true, updatable = true)
	private String latestTrnLangCode;
	@Column(name = "LATEST_REGTRN_DTIMES", nullable = true, updatable = true)
	private OffsetDateTime latestRegTrnTimestamp;

	@ManyToOne
	@JoinColumn(name = "CR_BY", referencedColumnName = "id", insertable = false, updatable = false)
	private RegistrationUserDetail userdetail;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "REG_ID")
	private List<RegistrationTransaction> registrationTransaction;
}
