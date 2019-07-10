package pt.dto.auth;

public class AddressIdentityRequest {

	private AddressIdentity identity;

	public AddressIdentityRequest() {

	}

	public AddressIdentityRequest(AddressIdentity identity) {
		this.identity = identity;
	}

	public AddressIdentity getIdentity() {
		return identity;
	}

	public void setIdentity(AddressIdentity identity) {
		this.identity = identity;
	}

}
