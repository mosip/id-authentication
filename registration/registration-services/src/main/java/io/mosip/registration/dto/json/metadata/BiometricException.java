package io.mosip.registration.dto.json.metadata;

import lombok.Data;

/**
 * This contains the attributes of BiometricException object to be displayed in
 * PacketMetaInfo JSON
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
public class BiometricException {

	private String type;
	private String missingBiometric;
	private String reason;
	private String exceptionType;
	private String individualType;

}
