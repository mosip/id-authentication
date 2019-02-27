package io.mosip.authentication.service.impl.indauth.match;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.hibernate.mapping.Collection;

import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.service.impl.indauth.service.bio.BioMatchType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum IdaIdMapping implements IdMapping {

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
	PINCODE("postalCode", MappingConfig::getPostalCode), 
	FULLADDRESS("fullAddress", MappingConfig::getFullAddress),
	OTP("otp", MappingConfig::getOtp),
	PIN("pin", MappingConfig::getPin),
	LEFTINDEX("leftIndex"),
	LEFTLITTLE("leftLittle"),
	LEFTMIDDLE("leftMiddle"),
	LEFTRING("leftRing"),
	LEFTTHUMB("leftThumb"),
	RIGHTINDEX("rightIndex"),
	RIGHTLITTLE("rightLittle"),
	RIGHTMIDDLE("rightMiddle"),
	RIGHTRING("rightRing"),
	RIGHTTHUMB("rightThumb"),
	FINGERPRINT("fingerprint", MappingConfig::getFingerprint), 
	IRIS("iris", MappingConfig::getIris),
	RIGHTEYE("rightEye"),
	LEFTEYE("leftEye"),
	FACE("face");

	private String idname;

	private BiFunction<MappingConfig, MatchType, List<String>> mappingFunction;

	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = (cfg, matchType) -> mappingFunction.apply(cfg);
	}

	private IdaIdMapping(String idname) {
		this.idname = idname;
		this.mappingFunction = (mappingConfig, matchType) -> getCbeffMapping(matchType);
	}

	public String getIdname() {
		return idname;
	}
	

	private static List<String> getCbeffMapping(MatchType matchType) {
		if (matchType instanceof BioMatchType) {
			BioMatchType bioMatchType = (BioMatchType) matchType;
			return getCbeffMapping(bioMatchType.getCbeffDocType().getType(), bioMatchType.getSubType(),
					bioMatchType.getSingleSubtype(), bioMatchType);
		}
		return Collections.emptyList();
	}

	private static List<String> getCbeffMapping(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, BioMatchType matchType) {
		String formatType = "";
		CbeffDocType cbeffDocType = ((BioMatchType) matchType).getCbeffDocType();
		formatType = String.valueOf(cbeffDocType.getValue());
		String cbeffKey = singleType.name() + "_" + (subType == null ? "" : subType.value())
				+ (singleSubType == null ? "" : (" " + singleSubType.value())) + "_" + formatType;
		return Arrays.asList(cbeffKey);
	}

	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
		return mappingFunction;
	}

}
