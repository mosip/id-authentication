package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class SelectionListDTO.
 */
@Data
public class SelectionListDTO implements Serializable {

	/** The uin id. */
	private String uinId;

	/** The is child. */
	private boolean isChild;

	/** The name. */
	private boolean name;

	/** The age. */
	private boolean age;

	/** The gender. */
	private boolean gender;

	/** The address. */
	private boolean address;

	/** The contact details. */
	private boolean contactDetails;

	/** The biometric exception. */
	private boolean biometricException;

	/** The biometric iris. */
	private boolean biometricIris;

	/** The biometric fingerprint. */
	private boolean biometricFingerprint;

	/** The cnie number. */
	private boolean cnieNumber;

	/** The parent or guardian details. */
	private boolean parentOrGuardianDetails;

	/** The foreigner. */
	private boolean foreigner;

	/**
	 * Checks if is biometric iris.
	 *
	 * @return the biometricIris
	 */
	public boolean isBiometricIris() {
		return biometricIris;
	}

	/**
	 * Sets the biometric iris.
	 *
	 * @param biometricIris
	 *            the biometricIris to set
	 */
	public void setBiometricIris(boolean biometricIris) {
		this.biometricIris = biometricIris;
	}

	/**
	 * Checks if is biometric fingerprint.
	 *
	 * @return the biometricFingerprint
	 */
	public boolean isBiometricFingerprint() {
		return biometricFingerprint;
	}

	/**
	 * Sets the biometric fingerprint.
	 *
	 * @param biometricFingerprint
	 *            the biometricFingerprint to set
	 */
	public void setBiometricFingerprint(boolean biometricFingerprint) {
		this.biometricFingerprint = biometricFingerprint;
	}

	/**
	 * Checks if is cnie number.
	 *
	 * @return the cnieNumber
	 */
	public boolean isCnieNumber() {
		return cnieNumber;
	}

	/**
	 * Sets the cnie number.
	 *
	 * @param cnieNumber
	 *            the cnieNumber to set
	 */
	public void setCnieNumber(boolean cnieNumber) {
		this.cnieNumber = cnieNumber;
	}

	/**
	 * Checks if is parent or guardian details.
	 *
	 * @return the parentOrGuardianDetails
	 */
	public boolean isParentOrGuardianDetails() {
		return parentOrGuardianDetails;
	}

	/**
	 * Sets the parent or guardian details.
	 *
	 * @param parentOrGuardianDetails
	 *            the parentOrGuardianDetails to set
	 */
	public void setParentOrGuardianDetails(boolean parentOrGuardianDetails) {
		this.parentOrGuardianDetails = parentOrGuardianDetails;
	}

	/**
	 * Checks if is foreigner.
	 *
	 * @return the foreigner
	 */
	public boolean isForeigner() {
		return foreigner;
	}

	/**
	 * Sets the foreigner.
	 *
	 * @param foreigner
	 *            the foreigner to set
	 */
	public void setForeigner(boolean foreigner) {
		this.foreigner = foreigner;
	}

	/**
	 * Gets the uin id.
	 *
	 * @return the uinId
	 */
	public String getUinId() {
		return uinId;
	}

	/**
	 * Sets the uin id.
	 *
	 * @param uinId
	 *            the uinId to set
	 */
	public void setUinId(String uinId) {
		this.uinId = uinId;
	}
}
