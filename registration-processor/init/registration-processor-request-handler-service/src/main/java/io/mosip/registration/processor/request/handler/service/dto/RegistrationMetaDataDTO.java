package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * This class contains the meta-information of the Registration.
 *
 * @author Sowmya
 */

/**
 * Instantiates a new registration meta data DTO.
 */
@Data
public class RegistrationMetaDataDTO extends BaseDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3536354184046165658L;

	/** The geo latitude loc. */
	private double geoLatitudeLoc;

	/** The geo longitude loc. */
	private double geoLongitudeLoc;

	/** The application type. */
	// New , update , correction, lost UIN
	private String applicationType;

	/** The registration category. */
	// Document Based or Introducer Based
	private String registrationCategory;

	/** The machine id. */
	private String machineId;

	/** The center id. */
	private String centerId;

	/** The previous RID. */
	private String previousRID;

	/** The uin. */
	private String uin;

	/** The consent of applicant. */
	private String consentOfApplicant;

	/** The parent or guardian UIN or RID. */
	private String parentOrGuardianUINOrRID;

	/** The device id. */
	private String deviceId;

	/** The applicant type code. */
	private String applicantTypeCode;

	/** The vid. */
	private String vid;

	/** The card type. */
	private String cardType;

	/**
	 * Gets the consent of applicant.
	 *
	 * @return the consentOfApplicant
	 */
	public String getConsentOfApplicant() {
		return consentOfApplicant;
	}

	/**
	 * Sets the consent of applicant.
	 *
	 * @param consentOfApplicant
	 *            the consentOfApplicant to set
	 */
	public void setConsentOfApplicant(String consentOfApplicant) {
		this.consentOfApplicant = consentOfApplicant;
	}

	/**
	 * Gets the geo latitude loc.
	 *
	 * @return the geoLatitudeLoc
	 */
	public double getGeoLatitudeLoc() {
		return geoLatitudeLoc;
	}

	/**
	 * Sets the geo latitude loc.
	 *
	 * @param geoLatitudeLoc
	 *            the geoLatitudeLoc to set
	 */
	public void setGeoLatitudeLoc(double geoLatitudeLoc) {
		this.geoLatitudeLoc = geoLatitudeLoc;
	}

	/**
	 * Gets the geo longitude loc.
	 *
	 * @return the geoLongitudeLoc
	 */
	public double getGeoLongitudeLoc() {
		return geoLongitudeLoc;
	}

	/**
	 * Sets the geo longitude loc.
	 *
	 * @param geoLongitudeLoc
	 *            the geoLongitudeLoc to set
	 */
	public void setGeoLongitudeLoc(double geoLongitudeLoc) {
		this.geoLongitudeLoc = geoLongitudeLoc;
	}

	/**
	 * Gets the application type.
	 *
	 * @return the applicationType
	 */
	public String getApplicationType() {
		return applicationType;
	}

	/**
	 * Sets the application type.
	 *
	 * @param applicationType
	 *            the applicationType to set
	 */
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	/**
	 * Gets the registration category.
	 *
	 * @return the registrationCategory
	 */
	public String getRegistrationCategory() {
		return registrationCategory;
	}

	/**
	 * Sets the registration category.
	 *
	 * @param registrationCategory
	 *            the registrationCategory to set
	 */
	public void setRegistrationCategory(String registrationCategory) {
		this.registrationCategory = registrationCategory;
	}

	/**
	 * Gets the machine id.
	 *
	 * @return the machineId
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * Sets the machine id.
	 *
	 * @param machineId
	 *            the machineId to set
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

	/**
	 * Gets the center id.
	 *
	 * @return the centerId
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * Sets the center id.
	 *
	 * @param centerId
	 *            the centerId to set
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * Gets the previous RID.
	 *
	 * @return the previousRID
	 */
	public String getPreviousRID() {
		return previousRID;
	}

	/**
	 * Sets the previous RID.
	 *
	 * @param previousRID
	 *            the previousRID to set
	 */
	public void setPreviousRID(String previousRID) {
		this.previousRID = previousRID;
	}

	/**
	 * Gets the uin.
	 *
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * Sets the uin.
	 *
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

	/**
	 * Gets the parent or guardian UIN or RID.
	 *
	 * @return the parentOrGuardianUINOrRID
	 */
	public String getParentOrGuardianUINOrRID() {
		return parentOrGuardianUINOrRID;
	}

	/**
	 * Sets the parent or guardian UIN or RID.
	 *
	 * @param parentOrGuardianUINOrRID
	 *            the parentOrGuardianUINOrRID to set
	 */
	public void setParentOrGuardianUINOrRID(String parentOrGuardianUINOrRID) {
		this.parentOrGuardianUINOrRID = parentOrGuardianUINOrRID;
	}

	/**
	 * Gets the device id.
	 *
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Sets the device id.
	 *
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * Gets the applicant type code.
	 *
	 * @return the applicantTypeCode
	 */
	public String getApplicantTypeCode() {
		return applicantTypeCode;
	}

	/**
	 * Sets the applicant type code.
	 *
	 * @param applicantTypeCode
	 *            the applicantTypeCode to set
	 */
	public void setApplicantTypeCode(String applicantTypeCode) {
		this.applicantTypeCode = applicantTypeCode;
	}

	/**
	 * Gets the vid.
	 *
	 * @return the vid
	 */
	public String getVid() {
		return vid;
	}

	/**
	 * Sets the vid.
	 *
	 * @param vid
	 *            the new vid
	 */
	public void setVid(String vid) {
		this.vid = vid;
	}

	/**
	 * Gets the card type.
	 *
	 * @return the card type
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * Sets the card type.
	 *
	 * @param cardType
	 *            the new card type
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
}
