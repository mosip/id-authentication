package io.mosip.admin.securitypolicy.constant;
/**
 * Security Policy Error Constant
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public enum SecurityPolicyErrorConstant {
	
	ERROR_FETCHING_USER_ROLE("ADM-SEC-001","Error while fetching user details"),
	NO_POLICY_FOUND("ADM-SEC-002","No Policy Found for the role :"),
	NO_AUTH_TYPE_FOUND("ADM-SEC-003","No auth type found");
	
	private final String errorCode;
	private final String errorMessage;
	
	private SecurityPolicyErrorConstant(String errorCode,String errorMessage) {
		this.errorCode=errorCode;
		this.errorMessage=errorMessage;
	}
	
	public String errorMessage() {
		return this.errorMessage;
	}
	
	public String errorCode() {
		return this.errorCode;
	}

}
