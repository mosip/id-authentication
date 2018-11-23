package io.mosip.kernel.masterdata.constant;

public enum IdTypeErrorCode {
	ID_TYPE_FETCH_EXCEPTION("KER-MSD-II1",
			"Error occured while fetching id types"), 
	ID_TYPE_MAPPING_EXCEPTION("KER-MSD-II2",
					"Error occured while mapping id types"),
    ID_TYPE_NOT_FOUND("KER-MSD-II3",
							"No id types found");

	private final String errorCode;
	private final String errorMessage;

	private IdTypeErrorCode(final String errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
