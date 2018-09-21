package org.mosip.registration.processor.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class IrisData {
	private List<Iris> iris;
	private int numRetry;
	private List<ExceptionIris> exceptionIris;

}
