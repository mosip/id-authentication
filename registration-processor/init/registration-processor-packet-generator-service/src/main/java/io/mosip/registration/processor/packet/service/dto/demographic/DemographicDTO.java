package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the demographic details of the Individual
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicDTO extends BaseDTO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 215986462161184776L;
	private ApplicantDocumentDTO applicantDocumentDTO;
	private String introducerRID;
	private String introducerUIN;
	private DemographicInfoDTO demographicInfoDTO;

}
