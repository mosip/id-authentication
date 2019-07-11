package pt.dto.auth.demo;

public class AddressAuthEntity {

	private AddressIdentityRequest identityRequest;
	private String tspID;

	public AddressAuthEntity() {

	}

	public AddressAuthEntity(AddressIdentityRequest identityRequest, String tspID) {
		super();
		this.identityRequest = identityRequest;
		this.tspID = tspID;
	}

	public AddressIdentityRequest getIdentityRequest() {
		return identityRequest;
	}

	public void setIdentityRequest(AddressIdentityRequest identityRequest) {
		this.identityRequest = identityRequest;
	}

	public String getTspID() {
		return tspID;
	}

	public void setTspID(String tspID) {
		this.tspID = tspID;
	}

}
