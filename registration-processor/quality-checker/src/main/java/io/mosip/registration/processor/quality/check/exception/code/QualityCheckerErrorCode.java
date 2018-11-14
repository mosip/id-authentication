package io.mosip.registration.processor.quality.check.exception.code;
public final class QualityCheckerErrorCode {


	private QualityCheckerErrorCode() {
		throw new IllegalStateException("Failed fetch Quality checker data");
	}
	
	public static final String TABLE_NOT_ACCESSIBLE = "File not fount in DFS Location";
}