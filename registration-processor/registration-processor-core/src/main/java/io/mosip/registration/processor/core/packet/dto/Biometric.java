/**
 * 
 */
package io.mosip.registration.processor.core.packet.dto;

/**
 * @author M1022006
 *
 */
public class Biometric {

	private Applicant applicant;
	private Introducer introducer;

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public Introducer getIntroducer() {
		return introducer;
	}

	public void setIntroducer(Introducer introducer) {
		this.introducer = introducer;
	}

}
