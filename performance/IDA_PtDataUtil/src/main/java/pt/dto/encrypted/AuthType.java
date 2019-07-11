package pt.dto.encrypted;

public class AuthType {

	private boolean address;
	private boolean bio;
	private boolean fullAddress;
	private boolean otp;
	private boolean personalIdentity;
	private boolean pin;

	public AuthType() {

	}

	public AuthType(boolean address, boolean bio, boolean fullAddress, boolean otp, boolean personalIdentity,
			boolean pin) {
		super();
		this.address = address;
		this.bio = bio;
		this.fullAddress = fullAddress;
		this.otp = otp;
		this.personalIdentity = personalIdentity;
		this.pin = pin;
	}

	public boolean isAddress() {
		return address;
	}

	public void setAddress(boolean address) {
		this.address = address;
	}

	public boolean isBio() {
		return bio;
	}

	public void setBio(boolean bio) {
		this.bio = bio;
	}

	public boolean isFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(boolean fullAddress) {
		this.fullAddress = fullAddress;
	}

	public boolean isOtp() {
		return otp;
	}

	public void setOtp(boolean otp) {
		this.otp = otp;
	}

	public boolean isPersonalIdentity() {
		return personalIdentity;
	}

	public void setPersonalIdentity(boolean personalIdentity) {
		this.personalIdentity = personalIdentity;
	}

	public boolean isPin() {
		return pin;
	}

	public void setPin(boolean pin) {
		this.pin = pin;
	}

}
