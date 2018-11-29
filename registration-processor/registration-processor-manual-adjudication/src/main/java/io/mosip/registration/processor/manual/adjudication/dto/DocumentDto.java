package io.mosip.registration.processor.manual.adjudication.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DocumentDto {
	private byte[] poa;
	private byte[] poi;
}
