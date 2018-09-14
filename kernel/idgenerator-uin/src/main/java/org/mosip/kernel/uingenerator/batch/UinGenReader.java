package org.mosip.kernel.uingenerator.batch;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
public class UinGenReader implements ItemReader<String> {

	public UinGenReader() {
		super();
		this.shouldRun = true;
	}

	private boolean shouldRun;

	@Override
	public String read() {
		if (shouldRun) {
			shouldRun = false;
			return "";
		}
		return null;
	}
}