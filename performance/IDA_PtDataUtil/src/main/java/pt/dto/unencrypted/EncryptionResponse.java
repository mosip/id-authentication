package pt.dto.unencrypted;

public class EncryptionResponse {

	private String encryptedSessionKey;
	private String encryptedIdentity;

	public EncryptionResponse() {

	}

	public String getEncryptedSessionKey() {
		return encryptedSessionKey;
	}

	public void setEncryptedSessionKey(String encryptedSessionKey) {
		this.encryptedSessionKey = encryptedSessionKey;
	}

	public String getEncryptedIdentity() {
		return encryptedIdentity;
	}

	public void setEncryptedIdentity(String encryptedIdentity) {
		this.encryptedIdentity = encryptedIdentity;
	}

}
