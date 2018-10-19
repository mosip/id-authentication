package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

@Data
public class BiometricSequence {

	private List<String> applicant;

	private List<String> introducer;
}
