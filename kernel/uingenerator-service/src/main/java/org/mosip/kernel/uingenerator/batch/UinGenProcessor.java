package org.mosip.kernel.uingenerator.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mosip.kernel.uingenerator.generator.UinGenerator;
import org.mosip.kernel.uingenerator.model.UinBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UinGenProcessor implements ItemProcessor<Object, List<UinBean>> {
	private static final Logger log = LoggerFactory.getLogger(UinGenProcessor.class);

	@Autowired
	private UinGenerator generator;

	@Override
	public List<UinBean> process(Object arg0) throws Exception {
		log.info("UIN Gen tasklet called");
		Set<UinBean> uins = generator.generate();
		return new ArrayList<>(uins);
	}
}
