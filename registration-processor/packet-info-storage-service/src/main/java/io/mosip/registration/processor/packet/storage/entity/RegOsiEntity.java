	package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The persistent class for the reg_osi database table.
 * 
 * @author Horteppa M1048399
 * @author Girish Yarru
 */
@Entity
@Table(name = "reg_osi", schema = "regprc")
public class RegOsiEntity extends BasePacketEntity<RegOsiPkEntity> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The prereg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preregId;

	/** The officer id. */
	@Column(name = "officer_id", nullable = false)
	private String officerId;

	/** The officer iris image name. */
	@Column(name = "officer_iris_image_name")
	private String officerIrisImageName;

	/** The officerfinger type. */
	@Column(name = "officer_fingerp_typ")
	private String officerfingerType;

	/** The officer iris type. */
	@Column(name = "officer_iris_typ")
	private String officerIrisType;

	/** The officer photo name. */
	@Column(name = "officer_photo_name")
	private String officerPhotoName;

	/** The officer hashed pin. */
	@Column(name = "officer_hashed_pin")
	private String officerHashedPin;

	/** The officer hashed pwd. */
	@Column(name = "officer_hashed_pwd")
	private String officerHashedPwd;

	/** The officer fingerp image name. */
	@Column(name = "officer_fingerp_image_name")
	private String officerFingerpImageName;

	/** The supervisor id. */
	@Column(name = "supervisor_id", nullable = false)
	private String supervisorId;

	/** The supervisor fingerp image name. */
	@Column(name = "supervisor_fingerp_image_name")
	private String supervisorFingerpImageName;

	/** The supervisor iris image name. */
	@Column(name = "supervisor_iris_image_name")
	private String supervisorIrisImageName;

	/** The supervisor finger type. */
	@Column(name = "supervisor_fingerp_typ")
	private String supervisorFingerType;

	/** The supervisor iris type. */
	@Column(name = "supervisor_iris_typ")
	private String supervisorIrisType;

	/** The supervisor hashed pwd. */
	@Column(name = "supervisor_hashed_pwd")
	private String supervisorHashedPwd;

	/** The supervisor hashed pin. */
	@Column(name = "supervisor_hashed_pin")
	private String supervisorHashedPin;

	/** The supervisor photo name. */
	@Column(name = "supervisor_photo_name")
	private String supervisorPhotoName;
	/** The introducer id. */
	@Column(name = "introducer_id")
	private String introducerId;

	/** The introducer typ. */
	@Column(name = "introducer_typ")
	private String introducerTyp;

	/** The introducer reg id. */
	@Column(name = "introducer_reg_id")
	private String introducerRegId;

	/** The introducer iris image name. */
	@Column(name = "introducer_iris_image_name")
	private String introducerIrisImageName;

	/** The introducer fingerp type. */
	@Column(name = "introducer_fingerp_typ")
	private String introducerFingerpType;

	/** The introducer iris type. */
	@Column(name = "introducer_iris_typ")
	private String introducerIrisType;

	/** The introducer fingerp image name. */
	@Column(name = "introducer_fingerp_image_name")
	private String introducerFingerpImageName;
	
	
	/** The introducer face image name. */
	@Column(name = "introducer_photo_name")
	private String introducerPhotoName;

	/** The introducer uin. */
	@Column(name = "introducer_uin")
	private String introducerUin;


	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/** The cr by. */
	@Column(name = "cr_by", nullable = false)
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimesz. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimesz. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/** The del dtimesz. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	/**
	 * Instantiates a new reg osi entity.
	 */
	public RegOsiEntity() {
		super();
	}

	/**
	 * Gets the prereg id.
	 *
	 * @return the prereg id
	 */
	public String getPreregId() {
		return preregId;
	}

	/**
	 * Sets the prereg id.
	 *
	 * @param preregId
	 *            the new prereg id
	 */
	public void setPreregId(String preregId) {
		this.preregId = preregId;
	}

	/**
	 * Gets the officer id.
	 *
	 * @return the officer id
	 */
	public String getOfficerId() {
		return officerId;
	}

	/**
	 * Sets the officer id.
	 *
	 * @param officerId
	 *            the new officer id
	 */
	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	/**
	 * Gets the officer iris image name.
	 *
	 * @return the officer iris image name
	 */
	public String getOfficerIrisImageName() {
		return officerIrisImageName;
	}

	/**
	 * Sets the officer iris image name.
	 *
	 * @param officerIrisImageName
	 *            the new officer iris image name
	 */
	public void setOfficerIrisImageName(String officerIrisImageName) {
		this.officerIrisImageName = officerIrisImageName;
	}

	/**
	 * Gets the officer fingerp image name.
	 *
	 * @return the officer fingerp image name
	 */
	public String getOfficerFingerpImageName() {
		return officerFingerpImageName;
	}

	/**
	 * Sets the officer fingerp image name.
	 *
	 * @param officerFingerpImageName
	 *            the new officer fingerp image name
	 */
	public void setOfficerFingerpImageName(String officerFingerpImageName) {
		this.officerFingerpImageName = officerFingerpImageName;
	}

	/**
	 * Gets the supervisor id.
	 *
	 * @return the supervisor id
	 */
	public String getSupervisorId() {
		return supervisorId;
	}

	/**
	 * Sets the supervisor id.
	 *
	 * @param supervisorId
	 *            the new supervisor id
	 */
	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	/**
	 * Gets the supervisor fingerp image name.
	 *
	 * @return the supervisor fingerp image name
	 */
	public String getSupervisorFingerpImageName() {
		return supervisorFingerpImageName;
	}

	/**
	 * Sets the supervisor fingerp image name.
	 *
	 * @param supervisorFingerpImageName
	 *            the new supervisor fingerp image name
	 */
	public void setSupervisorFingerpImageName(String supervisorFingerpImageName) {
		this.supervisorFingerpImageName = supervisorFingerpImageName;
	}

	/**
	 * Gets the supervisor iris image name.
	 *
	 * @return the supervisor iris image name
	 */
	public String getSupervisorIrisImageName() {
		return supervisorIrisImageName;
	}

	/**
	 * Sets the supervisor iris image name.
	 *
	 * @param supervisorIrisImageName
	 *            the new supervisor iris image name
	 */
	public void setSupervisorIrisImageName(String supervisorIrisImageName) {
		this.supervisorIrisImageName = supervisorIrisImageName;
	}

	/**
	 * Gets the introducer id.
	 *
	 * @return the introducer id
	 */
	public String getIntroducerId() {
		return introducerId;
	}

	/**
	 * Sets the introducer id.
	 *
	 * @param introducerId
	 *            the new introducer id
	 */
	public void setIntroducerId(String introducerId) {
		this.introducerId = introducerId;
	}

	/**
	 * Gets the introducer typ.
	 *
	 * @return the introducer typ
	 */
	public String getIntroducerTyp() {
		return introducerTyp;
	}

	/**
	 * Sets the introducer typ.
	 *
	 * @param introducerTyp
	 *            the new introducer typ
	 */
	public void setIntroducerTyp(String introducerTyp) {
		this.introducerTyp = introducerTyp;
	}

	/**
	 * Gets the introducer reg id.
	 *
	 * @return the introducer reg id
	 */
	public String getIntroducerRegId() {
		return introducerRegId;
	}

	/**
	 * Sets the introducer reg id.
	 *
	 * @param introducerRegId
	 *            the new introducer reg id
	 */
	public void setIntroducerRegId(String introducerRegId) {
		this.introducerRegId = introducerRegId;
	}

	/**
	 * Gets the introducer iris image name.
	 *
	 * @return the introducer iris image name
	 */
	public String getIntroducerIrisImageName() {
		return introducerIrisImageName;
	}

	/**
	 * Sets the introducer iris image name.
	 *
	 * @param introducerIrisImageName
	 *            the new introducer iris image name
	 */
	public void setIntroducerIrisImageName(String introducerIrisImageName) {
		this.introducerIrisImageName = introducerIrisImageName;
	}

	/**
	 * Gets the introducer fingerp image name.
	 *
	 * @return the introducer fingerp image name
	 */
	public String getIntroducerFingerpImageName() {
		return introducerFingerpImageName;
	}

	/**
	 * Sets the introducer fingerp image name.
	 *
	 * @param introducerFingerpImageName
	 *            the new introducer fingerp image name
	 */
	public void setIntroducerFingerpImageName(String introducerFingerpImageName) {
		this.introducerFingerpImageName = introducerFingerpImageName;
	}

	/**
	 * Gets the introducer photo name.
	 *
	 * @return the introducer photo name
	 */
	public String getIntroducerPhotoName() {
		return introducerPhotoName;
	}

	/**
	 * Sets the introducer photo name.
	 *
	 * @param introducerPhotoName the new introducer photo name
	 */
	public void setIntroducerPhotoName(String introducerPhotoName) {
		this.introducerPhotoName = introducerPhotoName;
	}

	/**
	 * Gets the introducer uin.
	 *
	 * @return the introducer uin
	 */
	public String getIntroducerUin() {
		return introducerUin;
	}

	/**
	 * Sets the introducer uin.
	 *
	 * @param introducerUin
	 *            the new introducer uin
	 */
	public void setIntroducerUin(String introducerUin) {
		this.introducerUin = introducerUin;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive
	 *            the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy
	 *            the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimes.
	 *
	 * @return the cr dtimes
	 */
	public LocalDateTime getCrDtimesz() {
		return crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimesz the new cr dtimesz
	 */
	public void setCrDtimesz(LocalDateTime crDtimesz) {
		this.crDtimes = crDtimesz;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy
	 *            the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimes.
	 *
	 * @return the upd dtimes
	 */
	public LocalDateTime getUpdDtimesz() {
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimesz the new upd dtimesz
	 */
	public void setUpdDtimesz(LocalDateTime updDtimesz) {
		this.updDtimes = updDtimesz;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted
	 *            the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * Gets the del dtimes.
	 *
	 * @return the del dtimes
	 */
	public LocalDateTime getDelDtimesz() {
		return delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimesz the new del dtimesz
	 */
	public void setDelDtimesz(LocalDateTime delDtimesz) {
		this.delDtimes = delDtimesz;
	}
	

	/**
	 * Gets the officerfinger type.
	 *
	 * @return the officerfinger type
	 */
	public String getOfficerfingerType() {
		return officerfingerType;
	}

	/**
	 * Sets the officerfinger type.
	 *
	 * @param officerfingerType the new officerfinger type
	 */
	public void setOfficerfingerType(String officerfingerType) {
		this.officerfingerType = officerfingerType;
	}

	/**
	 * Gets the officer iris type.
	 *
	 * @return the officer iris type
	 */
	public String getOfficerIrisType() {
		return officerIrisType;
	}

	/**
	 * Sets the officer iris type.
	 *
	 * @param officerIrisType the new officer iris type
	 */
	public void setOfficerIrisType(String officerIrisType) {
		this.officerIrisType = officerIrisType;
	}

	/**
	 * Gets the officer photo name.
	 *
	 * @return the officer photo name
	 */
	public String getOfficerPhotoName() {
		return officerPhotoName;
	}

	/**
	 * Sets the officer photo name.
	 *
	 * @param officerPhotoName the new officer photo name
	 */
	public void setOfficerPhotoName(String officerPhotoName) {
		this.officerPhotoName = officerPhotoName;
	}

	/**
	 * Gets the officer hashed pin.
	 *
	 * @return the officer hashed pin
	 */
	public String getOfficerHashedPin() {
		return officerHashedPin;
	}

	/**
	 * Sets the officer hashed pin.
	 *
	 * @param officerHashedPin the new officer hashed pin
	 */
	public void setOfficerHashedPin(String officerHashedPin) {
		this.officerHashedPin = officerHashedPin;
	}

	/**
	 * Gets the officer hashed pwd.
	 *
	 * @return the officer hashed pwd
	 */
	public String getOfficerHashedPwd() {
		return officerHashedPwd;
	}

	/**
	 * Sets the officer hashed pwd.
	 *
	 * @param officerHashedPwd the new officer hashed pwd
	 */
	public void setOfficerHashedPwd(String officerHashedPwd) {
		this.officerHashedPwd = officerHashedPwd;
	}

	/**
	 * Gets the supervisor finger type.
	 *
	 * @return the supervisor finger type
	 */
	public String getSupervisorFingerType() {
		return supervisorFingerType;
	}

	/**
	 * Sets the supervisor finger type.
	 *
	 * @param supervisorFingerType the new supervisor finger type
	 */
	public void setSupervisorFingerType(String supervisorFingerType) {
		this.supervisorFingerType = supervisorFingerType;
	}

	/**
	 * Gets the supervisor iris type.
	 *
	 * @return the supervisor iris type
	 */
	public String getSupervisorIrisType() {
		return supervisorIrisType;
	}

	/**
	 * Sets the supervisor iris type.
	 *
	 * @param supervisorTrisType the new supervisor iris type
	 */
	public void setSupervisorIrisType(String supervisorTrisType) {
		this.supervisorIrisType = supervisorTrisType;
	}

	/**
	 * Gets the supervisor hashed pwd.
	 *
	 * @return the supervisor hashed pwd
	 */
	public String getSupervisorHashedPwd() {
		return supervisorHashedPwd;
	}

	/**
	 * Sets the supervisor hashed pwd.
	 *
	 * @param supervisorHashedPwd the new supervisor hashed pwd
	 */
	public void setSupervisorHashedPwd(String supervisorHashedPwd) {
		this.supervisorHashedPwd = supervisorHashedPwd;
	}

	/**
	 * Gets the supervisor hashed pin.
	 *
	 * @return the supervisor hashed pin
	 */
	public String getSupervisorHashedPin() {
		return supervisorHashedPin;
	}

	/**
	 * Sets the supervisor hashed pin.
	 *
	 * @param supervisorHashedPin the new supervisor hashed pin
	 */
	public void setSupervisorHashedPin(String supervisorHashedPin) {
		this.supervisorHashedPin = supervisorHashedPin;
	}

	/**
	 * Gets the supervisor photo name.
	 *
	 * @return the supervisor photo name
	 */
	public String getSupervisorPhotoName() {
		return supervisorPhotoName;
	}

	/**
	 * Sets the supervisor photo name.
	 *
	 * @param supervisorPhotoName the new supervisor photo name
	 */
	public void setSupervisorPhotoName(String supervisorPhotoName) {
		this.supervisorPhotoName = supervisorPhotoName;
	}

	/**
	 * Gets the introducer fingerp type.
	 *
	 * @return the introducer fingerp type
	 */
	public String getIntroducerFingerpType() {
		return introducerFingerpType;
	}

	/**
	 * Sets the introducer fingerp type.
	 *
	 * @param introducerFingerpType the new introducer fingerp type
	 */
	public void setIntroducerFingerpType(String introducerFingerpType) {
		this.introducerFingerpType = introducerFingerpType;
	}

	/**
	 * Gets the introducer iris type.
	 *
	 * @return the introducer iris type
	 */
	public String getIntroducerIrisType() {
		return introducerIrisType;
	}

	/**
	 * Sets the introducer iris type.
	 *
	 * @param introducerIrisType the new introducer iris type
	 */
	public void setIntroducerIrisType(String introducerIrisType) {
		this.introducerIrisType = introducerIrisType;
	}

}