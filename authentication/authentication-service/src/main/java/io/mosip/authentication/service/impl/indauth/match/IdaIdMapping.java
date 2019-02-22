package io.mosip.authentication.service.impl.indauth.match;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
	LEFTINDEX("leftIndex",		(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.LEFT,SingleAnySubtypeType.INDEX_FINGER	, matchType)),
	LEFTLITTLE("leftLittle", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.LEFT,SingleAnySubtypeType.LITTLE_FINGER	, matchType)),
	LEFTMIDDLE("leftMiddle", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER,SingleAnySubtypeType.LEFT, SingleAnySubtypeType.MIDDLE_FINGER	, matchType)),
	LEFTRING("leftRing", 		(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.LEFT,SingleAnySubtypeType.RING_FINGER	, matchType)),
	LEFTTHUMB("leftThumb", 		(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.LEFT,SingleAnySubtypeType.THUMB			, matchType)),
	RIGHTINDEX("rightIndex", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.INDEX_FINGER	, matchType)),
	RIGHTLITTLE("rightLittle", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.LITTLE_FINGER, matchType)),
	RIGHTMIDDLE("rightMiddle", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.MIDDLE_FINGER, matchType)), 
	RIGHTRING("rightRing", 		(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.RING_FINGER	, matchType)),
	RIGHTTHUMB("rightThumb", 	(mappingConfig, matchType) -> getCbeffMapping(SingleType.FINGER, SingleAnySubtypeType.RIGHT,SingleAnySubtypeType.THUMB		, matchType)),
	FINGERPRINT("fingerprint", MappingConfig::getFingerprint),
	IRIS("iris", MappingConfig::getIris),
	RIGHTEYE("rightEye", (mappingConfig, matchType) -> getCbeffMapping(SingleType.IRIS, SingleAnySubtypeType.RIGHT, null, matchType)),
	LEFTEYE("leftEye", (mappingConfig, matchType) -> getCbeffMapping(SingleType.IRIS, SingleAnySubtypeType.LEFT, null, matchType)), 
	FACE("face", MappingConfig::getFace);

	private static final String FORMAT_TYPE = "${formatType}";

	private String idname;

	private BiFunction<MappingConfig, MatchType, List<String>> mappingFunction;

	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = (cfg, matchType) -> mappingFunction.apply(cfg);
	}
	
	private IdaIdMapping(String idname, BiFunction<MappingConfig, MatchType, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = mappingFunction;
	}

	public String getIdname() {
		return idname;
	}

	public static List<String> getCbeffMapping(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, MatchType matchType) {
		String formatType="";
		if (matchType instanceof BioMatchType) {
			CbeffDocType cbeffDocType = ((BioMatchType) matchType).getCbeffDocType();
			formatType = String.valueOf(cbeffDocType.getValue());
		}
		String cbeffKey = singleType.name() + "_"+subType.value()+(singleSubType == null ? "" : (" " +singleSubType.value())) + "_" + formatType;
		return Arrays.asList(cbeffKey);
	}
	

	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
		return mappingFunction;
	}

}
