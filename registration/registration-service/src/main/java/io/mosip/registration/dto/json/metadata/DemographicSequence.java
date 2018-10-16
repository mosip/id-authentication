package io.mosip.registration.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class DemographicSequence {
	
	private List<String> applicant;

	public DemographicSequence(List<String> applicant) {
		super();
		this.applicant = applicant;
	}
}