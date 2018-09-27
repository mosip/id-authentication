package org.mosip.registration.processor.packet.scanner.job;

import org.springframework.batch.core.Job;

public interface PacketScannerJob<T> {

	T landingZoneScannerJob();

	T virusScannerJob();

	T ftpScannerJob();

}
