package org.mosip.registration.processor.packet.scanner.job;

public interface PacketScannerJob<T> {

	T landingZoneScannerJob();

	T virusScannerJob();

	T ftpScannerJob();

}
