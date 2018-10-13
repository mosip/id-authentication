package io.mosip.registration.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class BiometricSequence {
	private List<String> applicant;
	private List<String> introducer;
	
	public BiometricSequence(List<String> applicant, List<String> introducer) {
		super();
		this.applicant = applicant;
		this.introducer = introducer;
	}

}