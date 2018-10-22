package io.mosip.kernel.idgenerator.uin.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import io.mosip.kernel.idgenerator.uin.constant.UinGeneratorConstant;

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
			return UinGeneratorConstant.EMPTY_STRING;
		} else {
			return null;
		}
	}
}