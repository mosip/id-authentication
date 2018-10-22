package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

@Data
public class IrisData {
	private List<Iris> iris;
	private int numRetry;
	private List<ExceptionIris> exceptionIris;

}
