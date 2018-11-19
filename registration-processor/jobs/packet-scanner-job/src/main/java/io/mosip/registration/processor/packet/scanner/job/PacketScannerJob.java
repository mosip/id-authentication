package io.mosip.registration.processor.packet.scanner.job;

/**
 * The Interface PacketScannerJob.
 *
 * @param <T> the generic type
 */
public interface PacketScannerJob<T> {

	/**
	 * Landing zone scanner job.
	 *
	 * @return the t
	 */
	T landingZoneScannerJob();

	/**
	 * Virus scanner job.
	 *
	 * @return the t
	 */
	T virusScannerJob();

	/**
	 * Ftp scanner job.
	 *
	 * @return the t
	 */
	T ftpScannerJob();

}
