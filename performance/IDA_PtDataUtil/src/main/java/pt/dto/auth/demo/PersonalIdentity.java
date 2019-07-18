package pt.dto.auth.demo;

import java.util.List;

public class PersonalIdentity {

	private List<Data> fullName;

	public PersonalIdentity() {
	}

	public List<Data> getFullName() {
		return fullName;
	}

	public void setFullName(List<Data> fullName) {
		this.fullName = fullName;
	}
}
