package io.mosip.kernel.masterdata.validator;

public enum FilterTypeEnum {
	CONTAINS("contains"), STARTSWITH("startsWith"), BETWEEN("between"), EQUALS("equals");

	private String type;

	private FilterTypeEnum(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
