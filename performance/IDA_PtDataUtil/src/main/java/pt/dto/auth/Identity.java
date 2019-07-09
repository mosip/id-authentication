package pt.dto.auth;

import java.util.List;

public class Identity {

	private float IDSchemaVersion;

	private List<Data> fullName;
	private String dateOfBirth;

	private Integer age;

	private List<Data> gender;

	private List<Data> addressLine1;
	private List<Data> addressLine2;
	private List<Data> addressLine3;
	private List<Data> region;
	private List<Data> province;
	private List<Data> city;

	private String postalCode;
	private String phone;
	private String email;

	public Identity() {

	}

	public float getIDSchemaVersion() {
		return IDSchemaVersion;
	}

	public void setIDSchemaVersion(float iDSchemaVersion) {
		IDSchemaVersion = iDSchemaVersion;
	}

	public List<Data> getFullName() {
		return fullName;
	}

	public void setFullName(List<Data> fullName) {
		this.fullName = fullName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<Data> getGender() {
		return gender;
	}

	public void setGender(List<Data> gender) {
		this.gender = gender;
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

	public List<Data> getRegion() {
		return region;
	}

	public void setRegion(List<Data> region) {
		this.region = region;
	}

	public List<Data> getProvince() {
		return province;
	}

	public void setProvince(List<Data> province) {
		this.province = province;
	}

	public List<Data> getCity() {
		return city;
	}

	public void setCity(List<Data> city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
