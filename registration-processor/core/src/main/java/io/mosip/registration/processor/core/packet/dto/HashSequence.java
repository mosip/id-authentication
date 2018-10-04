package io.mosip.registration.processor.core.packet.dto;

import java.util.LinkedList;

import lombok.Data;

@Data
public class HashSequence {

	private  LinkedList<String> applicant;
	private LinkedList<String> hof;
	private LinkedList<String> introducer;
}
