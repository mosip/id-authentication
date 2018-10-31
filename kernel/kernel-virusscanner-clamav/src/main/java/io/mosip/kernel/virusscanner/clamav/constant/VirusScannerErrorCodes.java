package io.mosip.kernel.virusscanner.clamav.constant;

/**
 * Internal Packet virus scan error codes.
 *
 */
public class VirusScannerErrorCodes {
	private static final String IIS_EPP_EPV_PREFIX = "IIS_";

	private VirusScannerErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	private static final String IIS_EPP_EPV_PREFIX_GEN_MODULE = IIS_EPP_EPV_PREFIX + "GEN_";
	public static final String IIS_EPP_EPV_SERVICE_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
			+ "ANTIVIRUS_SERVICE_NOT_ACCESSIBLE";
}
