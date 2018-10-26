package io.mosip.authentication.core.dto.indauth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PinInfo {

	/** PIN Value */
	private String value;

	/** PIN type */
	private String type;
}
