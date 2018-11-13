package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import io.mosip.authentication.service.config.IDAMappingConfig;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum IdMapping {

	NAME("name", IDAMappingConfig::getName), 
	DOB("dob", IDAMappingConfig::getDob),
	DOBTYPE("dobType", IDAMappingConfig::getDobType),
	AGE("age", IDAMappingConfig::getAge),
	GENDER("gender", IDAMappingConfig::getGender), 
	PHONE("phoneNumber", IDAMappingConfig::getPhoneNumber),
	EMAIL("emailId", IDAMappingConfig::getEmailId), 
	ADDRESSLINE1("addressLine1", IDAMappingConfig::getAddressLine1),
	ADDRESSLINE2("addressLine2", IDAMappingConfig::getAddressLine2),
	ADDRESSLINE3("addressLine3", IDAMappingConfig::getAddressLine3),
	LOCATION1("location1", IDAMappingConfig::getLocation1), 
	LOCATION2("location2", IDAMappingConfig::getLocation2), 
	LOCATION3("location3", IDAMappingConfig::getLocation3), 
	PINCODE("pinCode", IDAMappingConfig::getPinCode),
	FULLADDRESS("fullAddress", IDAMappingConfig::getFullAddress),
	OTP("otp", IDAMappingConfig::getOtp),
	PIN("pin", IDAMappingConfig::getPin),
	IRIS("iris", IDAMappingConfig::getIris),
	FINGERPRINT("fingerprint", IDAMappingConfig::getFingerprint)
	;

	private String idname;

	private Function<IDAMappingConfig, List<String>> mappingFunction;

	private IdMapping(String idname, Function<IDAMappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = mappingFunction;
	}

	public String getIdname() {
		return idname;
	}

	public Function<IDAMappingConfig, List<String>> getMappingFunction() {
		return mappingFunction;
	}
	
	public static Optional<IdMapping> getIdMapping(String name) {
		return Stream.of(values()).filter(m -> m.getIdname().equals(name)).findAny();
	}

}
