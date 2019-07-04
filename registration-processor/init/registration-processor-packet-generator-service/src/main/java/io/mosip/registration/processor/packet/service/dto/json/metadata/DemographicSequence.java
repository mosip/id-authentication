package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

import lombok.Data;

/**
 * This class is to capture the json parsing demographic sequence data
 * 
 * @author Sowmya
 * @since 1.0.0
 *
 */
@Data
public class DemographicSequence {

	private List<String> applicant;

	public DemographicSequence(List<String> applicant) {
		super();
		this.applicant = applicant;
	}

}