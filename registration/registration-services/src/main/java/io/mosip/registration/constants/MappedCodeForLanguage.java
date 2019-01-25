package io.mosip.registration.constants;



public enum MappedCodeForLanguage {
	en("eng"),ar("ara");
	private String langCode;

	MappedCodeForLanguage(String langCode) {
		this.langCode = langCode;
	}

	public String getMappedCode() {
		return langCode;
	}

}