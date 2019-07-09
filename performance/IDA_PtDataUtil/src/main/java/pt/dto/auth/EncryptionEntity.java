package pt.dto.auth;

public class EncryptionEntity {

	private IdentityRequest identityRequest;
	private String tspID;

	public EncryptionEntity() {

	}

	public IdentityRequest getIdentityRequest() {
		return identityRequest;
	}

	public void setIdentityRequest(IdentityRequest identityRequest) {
		this.identityRequest = identityRequest;
	}

	public String getTspID() {
		return tspID;
	}

	public void setTspID(String tspID) {
		this.tspID = tspID;
	}

}
