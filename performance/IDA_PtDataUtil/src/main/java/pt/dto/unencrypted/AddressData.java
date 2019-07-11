package pt.dto.unencrypted;

public class AddressData {

	private String language;
	private String value;

	public AddressData() {
	}

	public AddressData(String language, String value) {
		this.language = language;
		this.value = value;
	}

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

	@Override
	public String toString() {
		return "AddressData [language=" + language + ", value=" + value + "]";
	}

}
