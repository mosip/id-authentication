package org.mosip.kernel.core.util.constant;

public enum MathUtilConstants {
	ARITHMETIC_ERROR_CODE("COK-UTL-MTH-001","Invalid Result Found"), 
	ILLEGALARGUMENT_ERROR_CODE("COK-UTL-MTH-002","Invalid Argument Found"),
	NOT_A_NUMBER_ERROR_CODE("COK-UTL-MTH-003","NaN cannot be use as limit"),
	NOT_FINITE_NUMBER_ERROR_CODE("COK-UTL-MTH-004","Infinite limit cannot be used"),
	NOTPOSITIVE_ERROR_CODE("COK-UTL-MTH-005","Negative argument cannot be used"),
	NUMBER_IS_TOO_LARGE_ERROR_CODE("COK-UTL-MTH-006",""),
	NULL_POINTER_ERROR_CODE("COK-UTL-MTH-007","Lower limit is larger than upper limit");
	
	
	
	/**  Error code. */
	public final String errorCode;
	/**  Exception Message */
	public final String exceptionMessage;
	/**
	 * @param errorCode source Error code to use when no
     * localized code is available
     * @param exceptionMessage source exception message to use when no
     * localized message is available.
	 */
	MathUtilConstants(final String errorCode,final String exceptionMessage){
		this.errorCode=errorCode;
		this.exceptionMessage=exceptionMessage;
	}
	
	  public String getErrorCode() {
	        return errorCode;
	    }
	  public String getEexceptionMessage() {
	        return exceptionMessage;
	    }
}
