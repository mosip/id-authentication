package pt.dto.encrypted;

public class Key {

	private String publicKeyCert;
	private String sessionKey;

	public Key() {

	}

	public Key(String publicKeyCert, String sessionKey) {
		super();
		this.publicKeyCert = publicKeyCert;
		this.sessionKey = sessionKey;
	}

	public String getPublicKeyCert() {
		return publicKeyCert;
	}

	public void setPublicKeyCert(String publicKeyCert) {
		this.publicKeyCert = publicKeyCert;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

}
