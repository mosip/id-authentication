package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

/**
 * This class is to capture the json parsing biometric exception data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class BiometricSequence {
	private List<String> applicant;
	private List<String> introducer;

	public BiometricSequence(List<String> applicant, List<String> introducer) {
		super();
		this.applicant = applicant;
		this.introducer = introducer;
	}

	public List<String> getApplicant() {
		return applicant;
	}

	public void setApplicant(List<String> applicant) {
		this.applicant = applicant;
	}

	public List<String> getIntroducer() {
		return introducer;
	}

	public void setIntroducer(List<String> introducer) {
		this.introducer = introducer;
	}

}