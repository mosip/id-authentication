package io.mosip.registration.constants;



public enum MappedCodeForLanguage {
	en("ENG"),ar("ARA");
	private String langCode;

	MappedCodeForLanguage(String langCode) {
		this.langCode = langCode;
	}

	public String getMappedCode() {
		return langCode;
	}

}