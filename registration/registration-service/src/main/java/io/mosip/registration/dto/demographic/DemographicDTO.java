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
	private String introducerRID;
	private String introducerUIN;
	private DemographicInfoDTO demographicInfoDTO;

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
	 * @return the introducerRID
	 */
	public String getIntroducerRID() {
		return introducerRID;
	}

	/**
	 * @param introducerRID
	 *            the introducerRID to set
	 */
	public void setIntroducerRID(String introducerRID) {
		this.introducerRID = introducerRID;
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
	 * @return the demographicInfoDTO
	 */
	public DemographicInfoDTO getDemographicInfoDTO() {
		return demographicInfoDTO;
	}

	/**
	 * @param demographicInfoDTO
	 *            the demographicInfoDTO to set
	 */
	public void setDemographicInfoDTO(DemographicInfoDTO demographicInfoDTO) {
		this.demographicInfoDTO = demographicInfoDTO;
	}

}
