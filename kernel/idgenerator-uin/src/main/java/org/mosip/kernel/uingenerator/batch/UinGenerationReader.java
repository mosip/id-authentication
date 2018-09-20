package org.mosip.kernel.uingenerator.batch;

import org.mosip.kernel.uingenerator.constants.UinGeneratorConstants;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

/**
 * Item reader to initialize the uin generator job
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinGenerationReader implements ItemReader<String> {

	/**
	 * Boolean field whether new job should run
	 */
	private boolean shouldRun;

	/**
	 * Constructor to initialize {@link #shouldRun}
	 */
	public UinGenerationReader() {
		super();
		this.shouldRun = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
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