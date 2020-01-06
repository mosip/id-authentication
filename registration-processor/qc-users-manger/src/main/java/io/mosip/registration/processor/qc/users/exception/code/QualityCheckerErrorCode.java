package io.mosip.registration.processor.qc.users.exception.code;
public final class QualityCheckerErrorCode {


	private QualityCheckerErrorCode() {
		throw new IllegalStateException("Failed fetch QC User data");
	}
	
	public static final String TABLE_NOT_ACCESSIBLE = "mosip_master database not accessible.";
}