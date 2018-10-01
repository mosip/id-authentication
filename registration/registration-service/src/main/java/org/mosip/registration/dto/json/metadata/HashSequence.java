package org.mosip.registration.dto.json.metadata;

import java.util.LinkedList;

import lombok.Data;

@Data
public class HashSequence {

	private  LinkedList<String> applicant;
	private LinkedList<String> hof;
	private LinkedList<String> introducer;
}
