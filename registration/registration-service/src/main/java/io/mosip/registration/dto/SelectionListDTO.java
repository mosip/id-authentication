package io.mosip.registration.dto;

public class SelectionListDTO {

	private String uinId;
	private boolean isChild;
	private boolean name;
	private boolean age;
	private boolean gender;
	private boolean address;
	private boolean contactDetails;
	private boolean biometricException;
	private boolean biometricIris;
	private boolean biometricFingerprint;
	private boolean cnieNumber;
	private boolean parentOrGuardianDetails;
	
	
	/**
	 * @return the isChild
	 */
	public boolean isChild() {
		return isChild;
	}
	/**
	 * @param isChild the isChild to set
	 */
	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}
	/**
	 * @return the name
	 */
	public boolean isName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(boolean name) {
		this.name = name;
	}
	/**
	 * @return the age
	 */
	public boolean isAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(boolean age) {
		this.age = age;
	}
	/**
	 * @return the gender
	 */
	public boolean isGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(boolean gender) {
		this.gender = gender;
	}
	/**
	 * @return the address
	 */
	public boolean isAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(boolean address) {
		this.address = address;
	}
	/**
	 * @return the contactDetails
	 */
	public boolean isContactDetails() {
		return contactDetails;
	}
	/**
	 * @param contactDetails the contactDetails to set
	 */
	public void setContactDetails(boolean contactDetails) {
		this.contactDetails = contactDetails;
	}
	/**
	 * @return the biometricException
	 */
	public boolean isBiometricException() {
		return biometricException;
	}
	/**
	 * @param biometricException the biometricException to set
	 */
	public void setBiometricException(boolean biometricException) {
		this.biometricException = biometricException;
	}
	/**
	 * @return the biometricIris
	 */
	public boolean isBiometricIris() {
		return biometricIris;
	}
	/**
	 * @param biometricIris the biometricIris to set
	 */
	public void setBiometricIris(boolean biometricIris) {
		this.biometricIris = biometricIris;
	}
	/**
	 * @return the biometricFingerprint
	 */
	public boolean isBiometricFingerprint() {
		return biometricFingerprint;
	}
	/**
	 * @param biometricFingerprint the biometricFingerprint to set
	 */
	public void setBiometricFingerprint(boolean biometricFingerprint) {
		this.biometricFingerprint = biometricFingerprint;
	}
	/**
	 * @return the cnieNumber
	 */
	public boolean isCnieNumber() {
		return cnieNumber;
	}
	/**
	 * @param cnieNumber the cnieNumber to set
	 */
	public void setCnieNumber(boolean cnieNumber) {
		this.cnieNumber = cnieNumber;
	}
	/**
	 * @return the parentOrGuardianDetails
	 */
	public boolean isParentOrGuardianDetails() {
		return parentOrGuardianDetails;
	}
	/**
	 * @param parentOrGuardianDetails the parentOrGuardianDetails to set
	 */
	public void setParentOrGuardianDetails(boolean parentOrGuardianDetails) {
		this.parentOrGuardianDetails = parentOrGuardianDetails;
	}
	public String getUinId() {
		return uinId;
	}
	public void setUinId(String uinId) {
		this.uinId = uinId;
	}
	
}
