package io.mosip.registration.dto.demographic;

import java.util.Date;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class DemographicInfoDTO extends BaseDTO {

	protected String fullName;
	protected Date dateOfBirth;
	protected String gender;
	protected AddressDTO addressDTO;
	protected String emailId;
	protected String mobileNumber;
		protected String languageCode;
	protected boolean isChild;
	protected String age;
	protected String localAdministrativeAuthority;
	protected String cneOrPINNumber;
	protected String parentOrGuardianName;
	protected String parentOrGuardianRIDOrUIN;

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 *            the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the addressDTO
	 */
	public AddressDTO getAddressDTO() {
		return addressDTO;
	}

	/**
	 * @param addressDTO
	 *            the addressDTO to set
	 */
	public void setAddressDTO(AddressDTO addressDTO) {
		this.addressDTO = addressDTO;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId
	 *            the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobileNumber;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobileNumber = mobile;
	}

	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode
	 *            the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	/**
	 * @return the isChild
	 */
	public boolean isChild() {
		return isChild;
	}

	/**
	 * @param isChild
	 *            the isChild to set
	 */
	public void setChild(boolean isChild) {
		this.isChild = isChild;
	}

	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * @return the localAdministrativeAuthority
	 */
	public String getLocalAdministrativeAuthority() {
		return localAdministrativeAuthority;
	}

	/**
	 * @param localAdministrativeAuthority
	 *            the localAdministrativeAuthority to set
	 */
	public void setLocalAdministrativeAuthority(String localAdministrativeAuthority) {
		this.localAdministrativeAuthority = localAdministrativeAuthority;
	}

	/**
	 * @return the cneOrPINNumber
	 */
	public String getCneOrPINNumber() {
		return cneOrPINNumber;
	}

	/**
	 * @param cneOrPINNumber
	 *            the cneOrPINNumber to set
	 */
	public void setCneOrPINNumber(String cneOrPINNumber) {
		this.cneOrPINNumber = cneOrPINNumber;
	}

	/**
	 * @return the parentOrGuardianName
	 */
	public String getParentOrGuardianName() {
		return parentOrGuardianName;
	}

	/**
	 * @param parentOrGuardianName
	 *            the parentOrGuardianName to set
	 */
	public void setParentOrGuardianName(String parentOrGuardianName) {
		this.parentOrGuardianName = parentOrGuardianName;
	}

	/**
	 * @return the parentOrGuardianRIDOrUIN
	 */
	public String getParentOrGuardianRIDOrUIN() {
		return parentOrGuardianRIDOrUIN;
	}

	/**
	 * @param parentOrGuardianRIDOrUIN
	 *            the parentOrGuardianRIDOrUIN to set
	 */
	public void setParentOrGuardianRIDOrUIN(String parentOrGuardianRIDOrUIN) {
		this.parentOrGuardianRIDOrUIN = parentOrGuardianRIDOrUIN;
	}

}
