package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class UinGenerationReader implements ItemReader<String> {

	private boolean shouldRun;

	public UinGenerationReader() {
		super();
		this.shouldRun = true;
	}

	@Override
	public String read() {
		if (shouldRun) {
			shouldRun = false;
			return UinGeneratorConstants.EMPTY_STRING;
		} else {
			return null;
		}
	}
}