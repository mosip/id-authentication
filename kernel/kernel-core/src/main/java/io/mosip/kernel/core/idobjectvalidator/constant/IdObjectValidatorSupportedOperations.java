package io.mosip.kernel.core.idobjectvalidator.constant;

/**
 * @author Manoj SP
 *
 */
public enum IdObjectValidatorSupportedOperations {
	
	NEW_REGISTRATION("new-registration"),
	
	CHILD_REGISTRATION("child-registration"),
	
	UPDATE_UIN("update-uin"),
	
	LOST_UIN("lost-uin");
	
	private String operation;

	IdObjectValidatorSupportedOperations(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
	
}
