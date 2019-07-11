package pt.dto.auth;

import java.util.List;

public class AddressIdentity {

	private List<Data> addressLine1;
	private List<Data> addressLine2;
	private List<Data> addressLine3;

	public AddressIdentity() {

	}

	public List<Data> getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(List<Data> addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public List<Data> getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(List<Data> addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public List<Data> getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(List<Data> addressLine3) {
		this.addressLine3 = addressLine3;
	}

}
