package io.mosip.registration.device.iris;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Component
public class IrisFacade {

	private static final Logger LOGGER = AppConfig.getLogger(IrisFacade.class);


}
