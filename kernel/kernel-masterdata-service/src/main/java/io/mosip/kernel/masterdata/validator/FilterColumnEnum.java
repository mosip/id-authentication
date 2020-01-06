package io.mosip.kernel.masterdata.validator;

/**
 * ENUM that provides with the permissible values for the Filter Column Type.
 * 
 * @author Sagar Mahapatra
 * @since 1.0
 *
 */
public enum FilterColumnEnum {
	UNIQUE("unique"), ALL("all"), EMPTY("");

	private String filterColumn;

	private FilterColumnEnum(String filterColumn) {
		this.filterColumn = filterColumn;
	}

	@Override
	public String toString() {
		return filterColumn;
	}

}
