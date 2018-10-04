package io.mosip.registration.dto.demographic;

import io.mosip.registration.dto.BaseDTO;

/**
 * This class used to capture the demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
public class DemographicDTO extends BaseDTO {
	private ApplicantDocumentDTO applicantDocumentDTO;
	private String hofRegistrationId;
	private String hofUIN;
	private String introducerUIN;
	private DemographicInfoDTO demoInLocalLang;
	private DemographicInfoDTO demoInUserLang;

	/**
	 * @return the applicantDocumentDTO
	 */
	public ApplicantDocumentDTO getApplicantDocumentDTO() {
		return applicantDocumentDTO;
	}

	/**
	 * @param applicantDocumentDTO
	 *            the applicantDocumentDTO to set
	 */
	public void setApplicantDocumentDTO(ApplicantDocumentDTO applicantDocumentDTO) {
		this.applicantDocumentDTO = applicantDocumentDTO;
	}

	/**
	 * @return the hofRegistrationId
	 */
	public String getHOFRegistrationId() {
		return hofRegistrationId;
	}

	/**
	 * @param hofRegistrationId
	 *            the hofRegistrationId to set
	 */
	public void setHOFRegistrationId(String hofRegistrationId) {
		this.hofRegistrationId = hofRegistrationId;
	}

	/**
	 * @return the hofUIN
	 */
	public String getHOFUIN() {
		return hofUIN;
	}

	/**
	 * @param hofUIN
	 *            the hofUIN to set
	 */
	public void setHOFUIN(String hofUIN) {
		this.hofUIN = hofUIN;
	}

	/**
	 * @return the introducerUIN
	 */
	public String getIntroducerUIN() {
		return introducerUIN;
	}

	/**
	 * @param introducerUIN
	 *            the introducerUIN to set
	 */
	public void setIntroducerUIN(String introducerUIN) {
		this.introducerUIN = introducerUIN;
	}

	/**
	 * @return the demoInLocalLang
	 */
	public DemographicInfoDTO getDemoInLocalLang() {
		return demoInLocalLang;
	}

	/**
	 * @param demoInLocalLang
	 *            the demoInLocalLang to set
	 */
	public void setDemoInLocalLang(DemographicInfoDTO demoInLocalLang) {
		this.demoInLocalLang = demoInLocalLang;
	}

	/**
	 * @return the demoInUserLang
	 */
	public DemographicInfoDTO getDemoInUserLang() {
		return demoInUserLang;
	}

	/**
	 * @param demoInUserLang
	 *            the demoInUserLang to set
	 */
	public void setDemoInUserLang(DemographicInfoDTO demoInUserLang) {
		this.demoInUserLang = demoInUserLang;
	}
}
