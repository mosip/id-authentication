package org.mosip.registration.processor.packet.scanner.job;

import java.util.Random;

public class VirusScannerService {

	private static final Random random = new Random();

	public boolean result(String filePath) {
		return random.nextBoolean();
	}
}
