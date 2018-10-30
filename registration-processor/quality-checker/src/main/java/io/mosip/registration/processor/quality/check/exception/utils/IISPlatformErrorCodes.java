package io.mosip.registration.processor.quality.check.exception.utils;

public final class IISPlatformErrorCodes {

	private static final String IIS_REGISTRATION_PROCESSOR_PREFIX = "RPR_";
	private static final String IIS_QUALITY_CHECK_MODULE = "QCV_";
	private static final String ERROR_CODE1 = "001 ";
	private static final String ERROR_CODE2 = "002 ";
	private static final String ERROR_CODE3 = "003 ";

	private static final String IIS_QUALITY_CHECK_ERROR_CODE = IIS_REGISTRATION_PROCESSOR_PREFIX
			+ IIS_QUALITY_CHECK_MODULE;

	public static final String IIS_QCV_RESULT_NOT_FOUND = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE1
			+ "- DATA NOT FOUND FOR GIVEN QC_USERID AND RID";

	public static final String IIS_QCV_INVALID_QC_USER_ID = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE2
			+ "- DATA SENT FOR QC USER ID IS INCORRECT";

	public static final String IIS_QCV_INVALID_REGISTRATION_ID = IIS_QUALITY_CHECK_ERROR_CODE + ERROR_CODE3
			+ "- DATA SENT FOR RID IS INCORRECT";

	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

}
