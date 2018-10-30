package io.mosip.registration.processor.quality.check.exception.utils;

public final class IISPlatformErrorCodes {
	
	private static final String IIS_REGISTRATION_PROCESSOR_PREFIX = "RPR_";
	private static final String IIS_QUALITY_CHECK_MODULE = "QCV_";
	private static final String IIS_QUALITY_CHECK_ERROR_CODE = IIS_REGISTRATION_PROCESSOR_PREFIX
			+ IIS_QUALITY_CHECK_MODULE + "001 ";

	private IISPlatformErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	public static final String IIS_QCV_RESULT_NOT_FOUND = IIS_QUALITY_CHECK_ERROR_CODE
			+ "DATA NOT FOUND FOR GIVEN QC_USERID AND RID";

	public static final String IIS_QCV_USER_ID_NOT_FOUND = IIS_QUALITY_CHECK_ERROR_CODE
			+ "DATA SENT FOR QC USER ID IS INCORRECT";

	public static final String IIS_QCV_REGISTRATION_ID_NOT_FOUND = IIS_QUALITY_CHECK_ERROR_CODE
			+ "DATA SENT FOR RID IS INCORRECT";

}
