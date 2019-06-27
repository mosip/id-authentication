package io.mosip.util;

import lombok.Data;

/**
 * Instantiates a new json value.
 */
@Data	
public class JsonValue {

	/** The language. */
	private String language;
	
	/** The value. */
	private String value;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
