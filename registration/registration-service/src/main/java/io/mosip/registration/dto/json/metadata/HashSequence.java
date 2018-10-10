package io.mosip.registration.dto.json.metadata;

import java.util.LinkedList;

public class HashSequence {

	private LinkedList<String> applicant;
	private LinkedList<String> hof;
	private LinkedList<String> introducer;

	/**
	 * Parameterized Constructor
	 * 
	 * @param applicant
	 * @param hof
	 * @param introducer
	 */
	public HashSequence(LinkedList<String> applicant, LinkedList<String> hof, LinkedList<String> introducer) {
		this.applicant = applicant;
		this.hof = hof;
		this.introducer = introducer;
	}

	/**
	 * @return the applicant
	 */
	public LinkedList<String> getApplicant() {
		return applicant;
	}

	/**
	 * @param applicant
	 *            the applicant to set
	 */
	public void setApplicant(LinkedList<String> applicant) {
		this.applicant = applicant;
	}

	/**
	 * @return the hof
	 */
	public LinkedList<String> getHof() {
		return hof;
	}

	/**
	 * @param hof
	 *            the hof to set
	 */
	public void setHof(LinkedList<String> hof) {
		this.hof = hof;
	}

	/**
	 * @return the introducer
	 */
	public LinkedList<String> getIntroducer() {
		return introducer;
	}

	/**
	 * @param introducer
	 *            the introducer to set
	 */
	public void setIntroducer(LinkedList<String> introducer) {
		this.introducer = introducer;
	}
}
