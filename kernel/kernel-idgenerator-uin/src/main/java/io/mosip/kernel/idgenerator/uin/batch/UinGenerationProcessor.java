package io.mosip.kernel.idgenerator.uin.batch;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.idgenerator.uin.entity.UinEntity;
import io.mosip.kernel.idgenerator.uin.impl.UinGeneratorImpl;

/**
 * Generates a list of uins using {@link UinGeneratorImpl}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerationProcessor implements ItemProcessor<Object, List<UinEntity>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGenerationProcessor.class);

	/**
	 * Field for {@link UinGeneratorImpl} instance
	 */
	@Autowired
	private UinGeneratorImpl generator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public List<UinEntity> process(Object arg0) throws Exception {

		LOGGER.info("Uin generation processor called");
		return new ArrayList<>(generator.generateId());
	}
}
