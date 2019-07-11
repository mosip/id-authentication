package pt.dto.auth.demo;

public class PersonalIdentityRequest {

	private NameBasedPersonalIdentity identity;

	public PersonalIdentityRequest() {
	}

	public NameBasedPersonalIdentity getPersonalIdentity() {
		return identity;
	}

	public void setPersonalIdentity(NameBasedPersonalIdentity identity) {
		this.identity = identity;
	}
}
