package io.mosip.registration.device.face;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Component
public class FaceFacade {

	private static final Logger LOGGER = AppConfig.getLogger(FaceFacade.class);

}
