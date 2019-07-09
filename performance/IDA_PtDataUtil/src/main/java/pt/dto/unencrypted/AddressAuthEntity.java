package pt.dto.unencrypted;

public class AddressAuthEntity {

	private IdentityRequest identityRequest;
	private String tspID;

	public AddressAuthEntity() {
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

	@Override
	public String toString() {
		return "AddressAuthEntity [identityRequest=" + identityRequest + ", tspID=" + tspID + "]";
	}

}
