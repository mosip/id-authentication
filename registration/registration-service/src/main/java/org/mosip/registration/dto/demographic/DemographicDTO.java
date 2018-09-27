package org.mosip.registration.dto.demographic;

import org.mosip.registration.dto.BaseDTO;

import lombok.Data;

/**
 * Demographic details for Applicant, HOF and Introducer
 * @author M1047595
 *
 */
@Data
public class DemographicDTO extends BaseDTO{
	private ApplicantDocumentDTO applicantDocumentDTO;
	private String hofEnrollmentID;
	private String hofUIN;
	private String introducerUIN;
	private DemographicInfoDTO demoInLocalLang;
	private DemographicInfoDTO demoInUserLang;
}
