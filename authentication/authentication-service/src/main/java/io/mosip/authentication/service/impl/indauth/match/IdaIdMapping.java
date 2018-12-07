package io.mosip.authentication.service.impl.indauth.match;

import java.util.List;
import java.util.function.Function;

import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum IdaIdMapping implements IdMapping{

	NAME("name", MappingConfig::getName), 
	DOB("dob", MappingConfig::getDob),
	DOBTYPE("dobType", MappingConfig::getDobType),
	AGE("age", MappingConfig::getAge),
	GENDER("gender", MappingConfig::getGender), 
	PHONE("phoneNumber", MappingConfig::getPhoneNumber),
	EMAIL("emailId", MappingConfig::getEmailId), 
	ADDRESSLINE1("addressLine1", MappingConfig::getAddressLine1),
	ADDRESSLINE2("addressLine2", MappingConfig::getAddressLine2),
	ADDRESSLINE3("addressLine3", MappingConfig::getAddressLine3),
	LOCATION1("location1", MappingConfig::getLocation1), 
	LOCATION2("location2", MappingConfig::getLocation2), 
	LOCATION3("location3", MappingConfig::getLocation3), 
	PINCODE("pinCode", MappingConfig::getPinCode),
	FULLADDRESS("fullAddress", MappingConfig::getFullAddress),
	OTP("otp", MappingConfig::getOtp),
	PIN("pin", MappingConfig::getPin),
	IRIS("iris", MappingConfig::getIris),
	FINGERPRINT("fingerprint", MappingConfig::getFingerprint)
	;

	private String idname;

	private Function<MappingConfig, List<String>> mappingFunction;

	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = mappingFunction;
	}

	public String getIdname() {
		return idname;
	}

	public Function<MappingConfig, List<String>> getMappingFunction() {
		return mappingFunction;
	}
	

}
