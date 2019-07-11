package io.mosip.registrationProcessor.perf.regPacket.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BiometricData {

	private String format;
	private Float version;
	private String value;

}
