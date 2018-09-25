package org.mosip.kernel.uingenerator.batch;

import java.util.ArrayList;
import java.util.List;

import org.mosip.kernel.uingenerator.generator.UinGenerator;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Generates a list of uins using {@link UinGenerator}
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerationProcessor implements ItemProcessor<Object, List<UinBean>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGenerationProcessor.class);

	/**
	 * Field for {@link UinGenerator} instance
	 */
	@Autowired
	private UinGenerator generator;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public List<UinBean> process(Object arg0) throws Exception {

		LOGGER.info("Uin generation processor called");
		return new ArrayList<>(generator.generateId());
	}
}
