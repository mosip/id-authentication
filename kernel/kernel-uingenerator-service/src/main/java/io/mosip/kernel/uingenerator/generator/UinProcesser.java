package io.mosip.kernel.uingenerator.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.UinGenerator;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.repository.UinRepository;

@Component
public class UinProcesser {
	private static final Logger LOGGER = LoggerFactory.getLogger(UinProcesser.class);

	/**
	 * Field for uinRepository
	 */
	@Autowired
	private UinRepository uinRepository;

	/**
	 * Field for uinGeneratorImpl
	 */
	@Autowired
	private UinGenerator<Set<UinEntity>> uinGeneratorImpl;

	/**
	 * Long field for uin threshold count
	 */
	@Value("${mosip.kernel.uin.min-unused-threshold}")
	private long thresholdUinCount;
	
	/**
	 * String field for uin status unused
	 */
	@Value("${mosip.kernel.uin.status.unused}")
	private String unused;
	
	/**
	 * Check whether to generate uin or not
	 * 
	 * @return true, if needs to generate uin
	 */
	public boolean shouldGenerateUins() {
		LOGGER.info("Uin threshold is {}", thresholdUinCount);
		long freeUinsCount = uinRepository.countByStatus(unused);
		LOGGER.info("Number of free UINs in database is {}", freeUinsCount);
		return freeUinsCount < thresholdUinCount;
	}

	/**
	 * Create list of uins
	 */
	public List<UinEntity> generateUins() {
		return new ArrayList<>(uinGeneratorImpl.generateId());
	}

}
