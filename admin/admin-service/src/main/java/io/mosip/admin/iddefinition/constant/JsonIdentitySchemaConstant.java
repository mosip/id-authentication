package io.mosip.admin.iddefinition.constant;

/**
 * Identity Json Schema Constants
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
public enum JsonIdentitySchemaConstant {
	IDENTITY("identity"), SCHEMA("$schema"), TITLE("title"), PROPERTIES("properties");

	private final String name;

	private JsonIdentitySchemaConstant(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
