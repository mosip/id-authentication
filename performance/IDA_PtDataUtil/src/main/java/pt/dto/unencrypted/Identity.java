package pt.dto.unencrypted;

import java.util.List;

public class Identity {

	private List<AddressData> addressLine1;
	private List<AddressData> addressLine2;
	private List<AddressData> addressLine3;

	public Identity() {
	}

	public List<AddressData> getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(List<AddressData> addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public List<AddressData> getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(List<AddressData> addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public List<AddressData> getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(List<AddressData> addressLine3) {
		this.addressLine3 = addressLine3;
	}

	@Override
	public String toString() {
		return "Identity [addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", addressLine3="
				+ addressLine3 + "]";
	}

}
