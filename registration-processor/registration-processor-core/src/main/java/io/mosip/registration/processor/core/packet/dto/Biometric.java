package io.mosip.registration.processor.core.packet.dto;

/**
 * This class contains the attributes to be displayed for Biometric object in
 * PacketMetaInfo JSON.
 * <p>
 * This object contains the biometric details of Applicant and Introducer
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class Biometric {

	private Applicant applicant;
	private Introducer introducer;

	/**
	 * @return the applicant
	 */
	public Applicant getApplicant() {
		return applicant;
	}

	/**
	 * @param applicant
	 *            the applicant to set
	 */
	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	/**
	 * @return the introducer
	 */
	public Introducer getIntroducer() {
		return introducer;
	}

	/**
	 * @param introducer
	 *            the introducer to set
	 */
	public void setIntroducer(Introducer introducer) {
		this.introducer = introducer;
	}

}
