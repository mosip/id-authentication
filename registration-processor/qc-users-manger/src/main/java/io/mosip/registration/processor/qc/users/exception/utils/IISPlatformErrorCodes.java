package io.mosip.registration.processor.qc.users.exception.utils;

public final class IISPlatformErrorCodes {

	private static final String IIS_REGISTRATION_PROCESSOR_PREFIX = "RPR-";
	private static final String IIS_QUALITY_CHECK_MODULE = "QCV-";
	private static final String ERROR_CODE1 = "001 ";
	private static final String ERROR_CODE2 = "002 ";
	private static final String ERROR_CODE3 = "003 ";

	private static final String IIS_QUALITY_CHECK_ERROR_CODE = IIS_REGISTRATION_PROCESSOR_PREFIX
			+ IIS_QUALITY_CHECK_MODULE;

	public static final String IIS_QCV_RESULT_NOT_FOUND = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE1;

	public static final String IIS_QCV_INVALID_QC_USER_ID = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE2;

	public static final String IIS_QCV_INVALID_REGISTRATION_ID = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE3;

	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

}
