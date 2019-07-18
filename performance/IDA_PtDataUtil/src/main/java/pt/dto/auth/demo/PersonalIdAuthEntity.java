package pt.dto.auth.demo;

public class PersonalIdAuthEntity {

	private PersonalIdentityRequest identityRequest;
	private String tspID;

	public PersonalIdAuthEntity() {
	}

	public PersonalIdentityRequest getIdentityRequest() {
		return identityRequest;
	}

	public void setIdentityRequest(PersonalIdentityRequest identityRequest) {
		this.identityRequest = identityRequest;
	}

	public String getTspID() {
		return tspID;
	}

	public void setTspID(String tspID) {
		this.tspID = tspID;
	}

}
