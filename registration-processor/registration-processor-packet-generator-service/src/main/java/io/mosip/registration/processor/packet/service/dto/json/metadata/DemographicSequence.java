package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

/**
 * This class is to capture the json parsing demographic sequence data
 * 
 * @author Sowmya
 * @since 1.0.0
 *
 */
public class DemographicSequence {

	private List<String> applicant;

	public DemographicSequence(List<String> applicant) {
		super();
		this.applicant = applicant;
	}

	public List<String> getApplicant() {
		return applicant;
	}

	public void setApplicant(List<String> applicant) {
		this.applicant = applicant;
	}

}