package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;
	
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Instantiates a new registration processor identity.
 */
@Data
@Component
public class RegistrationProcessorIdentity {
	
	/** The identity. */
	Identity identity;
}
