package io.mosip.registration.dto.json.metadata;

import java.util.LinkedList;

import lombok.Data;

@Data
public class DemographicSequence {

	public DemographicSequence(LinkedList<String> applicant, LinkedList<String> hof, LinkedList<String> introducer) {
		super();
		this.applicant = applicant;
		this.hof = hof;
		this.introducer = introducer;
	}
	private LinkedList<String> applicant;
	private LinkedList<String> hof;
	private LinkedList<String> introducer;
}