package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;

public interface MappingConfig {

	public List<String> getName();

	public List<String> getDob();

	public List<String> getDobType();

	public List<String> getAge();

	public List<String> getGender();

	public List<String> getPhoneNumber();

	public List<String> getEmailId();

	public List<String> getAddressLine1();

	public List<String> getAddressLine2();

	public List<String> getAddressLine3();

	public List<String> getLocation1();

	public List<String> getLocation2();

	public List<String> getLocation3();

	public List<String> getPinCode();

	public List<String> getFullAddress();

	public List<String> getOtp();

	public List<String> getPin();

	public List<String> getIris();

	public List<String> getLeftIndex();

	public List<String> getLeftLittle();

	public List<String> getLeftMiddle();

	public List<String> getLeftRing();

	public List<String> getLeftThumb();

	public List<String> getRightIndex();

	public List<String> getRightLittle();

	public List<String> getRightMiddle();

	public List<String> getRightRing();

	public List<String> getRightThumb();
	
	public List<String> getFace();

}
