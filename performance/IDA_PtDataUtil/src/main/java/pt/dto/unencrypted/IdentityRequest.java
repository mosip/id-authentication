package pt.dto.unencrypted;

public class IdentityRequest {

	private Identity identity;

	public IdentityRequest() {

	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Override
	public String toString() {
		return "IdentityRequest [identity=" + identity + "]";
	}

}
