package io.mosip.registrationProcessor.perf.regPacket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentData {

	private String value;
	private String type;
	private String format;

}
