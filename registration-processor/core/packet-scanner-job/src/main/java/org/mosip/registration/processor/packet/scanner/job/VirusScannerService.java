package org.mosip.registration.processor.packet.scanner.job;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class VirusScannerService {

	private static final Random random = new Random();

	public boolean result(String filePath) {
		return random.nextBoolean();
	}
}
