package io.mosip.kernel.uingenerator.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.UinGenerator;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.repository.UinRepository;

@Component
public class UinProcesser {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(UinProcesser.class);

	/**
	 * Field for uinRepository
	 */
	@Autowired
	private UinRepository uinRepository;

	/**
	 * Field for uinGeneratorImpl
	 */
	@Autowired
	private UinGenerator uinGeneratorImpl;

	/**
	 * Long field for uin threshold count
	 */
	@Value("${mosip.kernel.uin.min-unused-threshold}")
	private long thresholdUinCount;

	/**
	 * Check whether to generate uin or not
	 * 
	 * @return true, if needs to generate uin
	 */
	public boolean shouldGenerateUins() {
		// LOGGER.info("Uin threshold is {}", thresholdUinCount);
		long freeUinsCount = uinRepository.countByStatus(UinGeneratorConstant.UNUSED);
		// LOGGER.info("Number of free UINs in database is {}", freeUinsCount);
		return freeUinsCount < thresholdUinCount;
	}

	/**
	 * Create list of uins
	 */
	public void generateUins() {
		uinGeneratorImpl.generateId();
	}

}
