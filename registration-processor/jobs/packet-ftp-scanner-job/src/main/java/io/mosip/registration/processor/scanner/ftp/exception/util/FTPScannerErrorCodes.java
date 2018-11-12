package io.mosip.registration.processor.scanner.ftp.exception.util;

public class FTPScannerErrorCodes {
	private static final String IIS_EPP_EPV_PREFIX = "IIS_";

	private FTPScannerErrorCodes() {
		throw new IllegalStateException("Utility class");
	}
	
	// Generic
		private static final String IIS_EPP_EPV_PREFIX_GEN_MODULE = IIS_EPP_EPV_PREFIX + "GEN_";
		
		public static final String IIS_EPP_EPV_REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
				+ "REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE";
		public static final String IIS_EPP_EPV_FTP_FOLDER_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE
				+ "FTP_FOLDER_NOT_ACCESSIBLE";
}
