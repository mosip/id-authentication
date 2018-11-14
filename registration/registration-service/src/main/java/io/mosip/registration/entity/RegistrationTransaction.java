package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * The enity class for Registration Transaction
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Entity
@Table(schema="REG", name="REGISTRATION_TRANSACTION")
public class RegistrationTransaction extends RegistrationCommonFields {

	@Id
	@Column(name="ID", length=32, nullable=false, updatable=false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	@Column(name="REG_ID", length=28, nullable=true, updatable=true)
	private String regId;
	@Column(name="PARENT_REGTRN_ID", length=32, nullable=true, updatable=true)
	private String parentRegTrnId;
	@Column(name="TRN_TYPE_CODE", length=64, nullable=false, updatable=true)
	private String trnTypeCode;
	@Column(name="REMARKS", length=1024, nullable=true, updatable=true)
	private String remarks;
	@Column(name="STATUS_CODE", length=64, nullable=false, updatable=true)
	private String statusCode;
	@Column(name="LANG_CODE", length=3, nullable=true, updatable=true)
	private String langCode;
	@Column(name="STATUS_COMMENT", length=1024, nullable=true, updatable=true)
	private String statusComment;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getParentRegTrnId() {
		return parentRegTrnId;
	}
	public void setParentRegTrnId(String parentRegTrnId) {
		this.parentRegTrnId = parentRegTrnId;
	}
	public String getTrnTypeCode() {
		return trnTypeCode;
	}
	public void setTrnTypeCode(String trnTypeCode) {
		this.trnTypeCode = trnTypeCode;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	
}
