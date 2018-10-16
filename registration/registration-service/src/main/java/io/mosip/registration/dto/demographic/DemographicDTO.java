package io.mosip.registration.dto.demographic;

import org.springframework.stereotype.Component;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicDTO extends BaseDTO {
	private ApplicantDocumentDTO applicantDocumentDTO;
	private String introducerRID;
	private String introducerUIN;
	private DemographicInfoDTO demoInLocalLang;
	private DemographicInfoDTO demoInUserLang;
}
