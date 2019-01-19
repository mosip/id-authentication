package io.mosip.registration.entity;

import java.sql.Timestamp;

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
@Table(schema = "REG", name = "REGISTRATION_TRANSACTION")
public class RegistrationTransaction {

	@Id
	@Column(name = "ID")
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;
	@Column(name = "REG_ID")
	private String regId;
	@Column(name = "PARENT_REGTRN_ID")
	private String parentRegTrnId;
	@Column(name = "TRN_TYPE_CODE")
	private String trnTypeCode;
	@Column(name = "REMARKS")
	private String remarks;
	@Column(name = "STATUS_CODE")
	private String statusCode;
	@Column(name = "LANG_CODE")
	private String langCode;
	@Column(name = "STATUS_COMMENT")
	private String statusComment;
	@Column(name = "CR_BY")
	private String crBy;
	@Column(name = "CR_DTIMES")
	private Timestamp crDtime;
	@Column(name = "UPD_BY")
	private String updBy;
	@Column(name = "UPD_DTIMES")
	private Timestamp updDtimes;
	@Column(name = "IS_DELETED")
	private Boolean isDeleted;
	@Column(name = "DEL_DTIMES")
	private Timestamp delDtime;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the regId
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * @param regId
	 *            the regId to set
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * @return the parentRegTrnId
	 */
	public String getParentRegTrnId() {
		return parentRegTrnId;
	}

	/**
	 * @param parentRegTrnId
	 *            the parentRegTrnId to set
	 */
	public void setParentRegTrnId(String parentRegTrnId) {
		this.parentRegTrnId = parentRegTrnId;
	}

	/**
	 * @return the trnTypeCode
	 */
	public String getTrnTypeCode() {
		return trnTypeCode;
	}

	/**
	 * @param trnTypeCode
	 *            the trnTypeCode to set
	 */
	public void setTrnTypeCode(String trnTypeCode) {
		this.trnTypeCode = trnTypeCode;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks
	 *            the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the langCode
	 */
	public String getLangCode() {
		return langCode;
	}

	/**
	 * @param langCode
	 *            the langCode to set
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	/**
	 * @return the statusComment
	 */
	public String getStatusComment() {
		return statusComment;
	}

	/**
	 * @param statusComment
	 *            the statusComment to set
	 */
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	/**
	 * @return the crBy
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * @param crBy
	 *            the crBy to set
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * @return the crDtime
	 */
	public Timestamp getCrDtime() {
		return crDtime;
	}

	/**
	 * @param crDtime
	 *            the crDtime to set
	 */
	public void setCrDtime(Timestamp crDtime) {
		this.crDtime = crDtime;
	}

	/**
	 * @return the updBy
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * @param updBy
	 *            the updBy to set
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * @return the updDtimes
	 */
	public Timestamp getUpdDtimes() {
		return updDtimes;
	}

	/**
	 * @param updDtimes
	 *            the updDtimes to set
	 */
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}

}
