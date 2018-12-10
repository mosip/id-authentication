package io.mosip.registration.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pre Registration entity
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "pre_registration_list", schema = "reg")
public class PreRegistrationList extends RegistrationCommonFields{
	@Id
	@Column(name = "id", length = 36, nullable = false)	
	private String id;
	@Column(name = "prereg_id", length = 64, nullable = false)	
	private String preRegId;
	@Column(name = "prereg_type", length = 64, nullable = true)
	private String preRegType;
	@Column(name = "parent_prereg_id", length = 64, nullable = true)
	private String parentPreRegId;
	@Column(name = "appointment_date",nullable = true)
	private Date appointmentDate;
	@Column(name = "packet_symmetric_key", length = 256, nullable = true)
	private String packetSymmetricKey;
	@Column(name = "status_code", length = 36, nullable = false)
	private String statusCode;
	@Column(name = "status_comment", length = 256, nullable = true)
	private String statusComment;
	@Column(name = "packet_path", length = 256, nullable = true)
	private String packetPath;
	@Column(name = "sjob_id", length = 36, nullable = true)
	private String sJobId;
	@Column(name = "synctrn_id", length = 36, nullable = true)
	private String synctrnId;
	
	@Column(name = "lang_code", length = 3, nullable = false)
	private String langCode;
	@Column(name = "is_deleted", nullable = true)
	private Boolean isDeleted;
	@Column(name = "del_dtimes", nullable = true)
	private String delDtimes;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPreRegId() {
		return preRegId;
	}
	public void setPreRegId(String preRegId) {
		this.preRegId = preRegId;
	}
	public String getPreRegType() {
		return preRegType;
	}
	public void setPreRegType(String preRegType) {
		this.preRegType = preRegType;
	}
	public String getParentPreRegId() {
		return parentPreRegId;
	}
	public void setParentPreRegId(String parentPreRegId) {
		this.parentPreRegId = parentPreRegId;
	}
	public Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getPacketSymmetricKey() {
		return packetSymmetricKey;
	}
	public void setPacketSymmetricKey(String packetSymmetricKey) {
		this.packetSymmetricKey = packetSymmetricKey;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusComment() {
		return statusComment;
	}
	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}
	public String getPacketPath() {
		return packetPath;
	}
	public void setPacketPath(String packetPath) {
		this.packetPath = packetPath;
	}
	public String getsJobId() {
		return sJobId;
	}
	public void setsJobId(String sJobId) {
		this.sJobId = sJobId;
	}
	public String getSynctrnId() {
		return synctrnId;
	}
	public void setSynctrnId(String synctrnId) {
		this.synctrnId = synctrnId;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getDelDtimes() {
		return delDtimes;
	}
	public void setDelDtimes(String delDtimes) {
		this.delDtimes = delDtimes;
	}

}
