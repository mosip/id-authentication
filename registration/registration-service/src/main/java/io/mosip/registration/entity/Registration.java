package io.mosip.registration.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Registration entity details
 * 
 * @author Mahesh Kumar
 * @since 1.0.0
 */
@Entity
@Table(schema = "REG", name = "REGISTRATION")
public class Registration extends RegistrationCommonFields {
	@Id
	@Column(name = "ID")
	private String id;
	@Column(name = "REG_TYPE")
	private String regType;
	@Column(name = "REF_REG_ID")
	private String refRegId;
	@Column(name = "STATUS_CODE")
	private String statusCode;
	@Column(name = "LANG_CODE")
	private String langCode;
	@Column(name = "STATUS_COMMENT")
	private String statusComment;
	@Column(name = "STATUS_DTIMES")
	private Timestamp statusTimestamp;
	@Column(name = "ACK_FILENAME")
	private String ackFilename;
	@Column(name = "CLIENT_STATUS_CODE")
	private String clientStatusCode;
	@Column(name = "SERVER_STATUS_CODE")
	private String serverStatusCode;
	@Column(name = "CLIENT_STATUS_DTIME")
	private Timestamp clientStatusTimestamp;
	@Column(name = "SERVER_STATUS_DTIME")
	private Timestamp serverStatusTimestamp;
	@Column(name = "CLIENT_STATUS_COMMENT")
	private String clientStatusComments;
	@Column(name = "SERVER_STATUS_COMMENT")
	private String serverStatusComments;
	@Column(name = "INDV_NAME")
	private String individualName;
	@Column(name = "REG_USR_ID")
	private String regUsrId;
	@Column(name = "REGCNTR_ID")
	private String regCntrId;
	@Column(name = "APPROVER_USR_ID")
	private String approverUsrId;
	@Column(name = "APPROVER_ROLE_CODE")
	private String approverRoleCode;
	@Column(name = "FILE_UPLOAD_STATUS")
	private String fileUploadStatus;
	@Column(name = "UPLOAD_COUNT")
	private Short uploadCount;
	@Column(name = "UPLOAD_DTIMES")
	private Timestamp uploadTimestamp;
	@Column(name = "LATEST_REGTRN_ID")
	private String latestRegTrnId;
	@Column(name = "LATEST_TRN_TYPE_CODE")
	private String latestTrnTypeCode;
	@Column(name = "LATEST_TRN_STATUS_CODE")
	private String latestTrnStatusCode;
	@Column(name = "LATEST_TRN_LANG_CODE")
	private String latestTrnLangCode;
	@Column(name = "LATEST_REGTRN_DTIMES")
	private Timestamp latestRegTrnTimestamp;

	@ManyToOne
	@JoinColumn(name = "CR_BY", referencedColumnName = "id", insertable = false, updatable = false)
	private RegistrationUserDetail userdetail;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "REG_ID")
	private List<RegistrationTransaction> registrationTransaction;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getRefRegId() {
		return refRegId;
	}

	public void setRefRegId(String refRegId) {
		this.refRegId = refRegId;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public Timestamp getStatusTimestamp() {
		return statusTimestamp;
	}

	public void setStatusTimestamp(Timestamp statusTimestamp) {
		this.statusTimestamp = statusTimestamp;
	}

	public String getAckFilename() {
		return ackFilename;
	}

	public void setAckFilename(String ackFilename) {
		this.ackFilename = ackFilename;
	}

	public String getClientStatusCode() {
		return clientStatusCode;
	}

	public void setClientStatusCode(String clientStatusCode) {
		this.clientStatusCode = clientStatusCode;
	}

	public String getServerStatusCode() {
		return serverStatusCode;
	}

	public void setServerStatusCode(String serverStatusCode) {
		this.serverStatusCode = serverStatusCode;
	}

	public Timestamp getClientStatusTimestamp() {
		return clientStatusTimestamp;
	}

	public void setClientStatusTimestamp(Timestamp clientStatusTimestamp) {
		this.clientStatusTimestamp = clientStatusTimestamp;
	}

	public Timestamp getServerStatusTimestamp() {
		return serverStatusTimestamp;
	}

	public void setServerStatusTimestamp(Timestamp serverStatusTimestamp) {
		this.serverStatusTimestamp = serverStatusTimestamp;
	}

	public String getClientStatusComments() {
		return clientStatusComments;
	}

	public void setClientStatusComments(String clientStatusComments) {
		this.clientStatusComments = clientStatusComments;
	}

	public String getServerStatusComments() {
		return serverStatusComments;
	}

	public void setServerStatusComments(String serverStatusComments) {
		this.serverStatusComments = serverStatusComments;
	}

	public String getIndividualName() {
		return individualName;
	}

	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}

	public String getRegUsrId() {
		return regUsrId;
	}

	public void setRegUsrId(String regUsrId) {
		this.regUsrId = regUsrId;
	}

	public String getRegCntrId() {
		return regCntrId;
	}

	public void setRegCntrId(String regCntrId) {
		this.regCntrId = regCntrId;
	}

	public String getApproverUsrId() {
		return approverUsrId;
	}

	public void setApproverUsrId(String approverUsrId) {
		this.approverUsrId = approverUsrId;
	}

	public String getApproverRoleCode() {
		return approverRoleCode;
	}

	public void setApproverRoleCode(String approverRoleCode) {
		this.approverRoleCode = approverRoleCode;
	}

	public String getFileUploadStatus() {
		return fileUploadStatus;
	}

	public void setFileUploadStatus(String fileUploadStatus) {
		this.fileUploadStatus = fileUploadStatus;
	}

	public Short getUploadCount() {
		return uploadCount;
	}

	public void setUploadCount(Short uploadCount) {
		this.uploadCount = uploadCount;
	}

	public Timestamp getUploadTimestamp() {
		return uploadTimestamp;
	}

	public void setUploadTimestamp(Timestamp uploadTimestamp) {
		this.uploadTimestamp = uploadTimestamp;
	}

	public String getLatestRegTrnId() {
		return latestRegTrnId;
	}

	public void setLatestRegTrnId(String latestRegTrnId) {
		this.latestRegTrnId = latestRegTrnId;
	}

	public String getLatestTrnTypeCode() {
		return latestTrnTypeCode;
	}

	public void setLatestTrnTypeCode(String latestTrnTypeCode) {
		this.latestTrnTypeCode = latestTrnTypeCode;
	}

	public String getLatestTrnStatusCode() {
		return latestTrnStatusCode;
	}

	public void setLatestTrnStatusCode(String latestTrnStatusCode) {
		this.latestTrnStatusCode = latestTrnStatusCode;
	}

	public String getLatestTrnLangCode() {
		return latestTrnLangCode;
	}

	public void setLatestTrnLangCode(String latestTrnLangCode) {
		this.latestTrnLangCode = latestTrnLangCode;
	}

	public Timestamp getLatestRegTrnTimestamp() {
		return latestRegTrnTimestamp;
	}

	public void setLatestRegTrnTimestamp(Timestamp latestRegTrnTimestamp) {
		this.latestRegTrnTimestamp = latestRegTrnTimestamp;
	}

	public RegistrationUserDetail getUserdetail() {
		return userdetail;
	}

	public void setUserdetail(RegistrationUserDetail userdetail) {
		this.userdetail = userdetail;
	}

	public List<RegistrationTransaction> getRegistrationTransaction() {
		return registrationTransaction;
	}

	public void setRegistrationTransaction(List<RegistrationTransaction> registrationTransaction) {
		this.registrationTransaction = registrationTransaction;
	}
	
	
}
