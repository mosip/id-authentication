/*
 * 
 */
package io.mosip.authentication.common.service.impl.match;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

/**
 * 
 * Mapping class for IDA
 * 
 * @author Dinesh Karuppiah.T
 */
public enum IdaIdMapping implements IdMapping {

// @formatter:off
	//PI
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
	
	//PIN
	OTP("otp", MappingConfig::getOtp),
	PIN("pin", MappingConfig::getPin),
	
	//FINGER
	//BIO - Finger - Single
	LEFTINDEX("LEFT_INDEX"),
	LEFTLITTLE("LEFT_LITTLE"),
	LEFTMIDDLE("LEFT_MIDDLE"),
	LEFTRING("LEFT_RING"),
	LEFTTHUMB("LEFT_THUMB"),
	RIGHTINDEX("RIGHT_INDEX"),
	RIGHTLITTLE("RIGHT_LITTLE"),
	RIGHTMIDDLE("RIGHT_MIDDLE"),
	RIGHTRING("RIGHT_RING"),
	RIGHTTHUMB("RIGHT_THUMB"),
	//BIO - Finger - Multi or Unknown
	UNKNOWN_FINGER("UNKNOWN", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB)),
	FINGERPRINT("fingerprint", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB, UNKNOWN_FINGER)),
	
	//IRIS
	//BIO - Iris - Single
	LEFTIRIS("LEFT"),
	RIGHTIRIS("RIGHT"),
	//BIO - Iris - Multi or Unknown
	UNKNOWN_IRIS("UNKNOWN", setOf(RIGHTIRIS, LEFTIRIS)),
	IRIS("iris", setOf(RIGHTIRIS, LEFTIRIS, UNKNOWN_IRIS)),
	
	//FACE
	//BIO - Face - Single
	FACE("FACE"),
	//BIO - Face - Unknown
	UNKNOWN_FACE("UNKNOWN", setOf(FACE));

// @formatter:on

	private String idname;

	private BiFunction<MappingConfig, MatchType, List<String>> mappingFunction;

	private Set<IdMapping> subIdMappings;

	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = (cfg, matchType) -> mappingFunction.apply(cfg);
		this.subIdMappings = Collections.emptySet();
	}

	private IdaIdMapping(String idname) {
		this.idname = idname;
		this.mappingFunction = (mappingConfig, matchType) -> getCbeffMapping(matchType);
		this.subIdMappings = Collections.emptySet();
	}

	private IdaIdMapping(String idname, Set<IdMapping> subIdMappings) {
		this.idname = idname;
		this.subIdMappings = subIdMappings;
		this.mappingFunction = (mappingConfig, matchType) -> {
			if (matchType instanceof BioMatchType) {
				return Stream.of(((BioMatchType) matchType).getMatchTypesForSubIdMappings(subIdMappings))
						.flatMap(subMatchType -> subMatchType.getIdMapping().getMappingFunction()
								.apply(mappingConfig, subMatchType).stream())
						.collect(Collectors.toList());
			} else {
				return Collections.emptyList();
			}
		};
	}

	public String getIdname() {
		return idname;
	}

	public Set<IdMapping> getSubIdMappings() {
		return subIdMappings;
	}

	/**
	 * Fetch Cbeff Mapping based on Match Type
	 * 
	 * @param matchType
	 * @return
	 */
	private static List<String> getCbeffMapping(MatchType matchType) {
		if (matchType instanceof BioMatchType) {
			BioMatchType bioMatchType = (BioMatchType) matchType;
			return getCbeffMapping(bioMatchType.getCbeffDocType().getType(), bioMatchType.getSubType(),
					bioMatchType.getSingleAnySubtype(), bioMatchType);
		}
		return Collections.emptyList();
	}

	/**
	 * To get Cbeff mapping based on Single and SubType on Cbeff
	 * 
	 * @param singleType
	 * @param subType
	 * @param singleSubType
	 * @param matchType
	 * @return
	 */
	private static List<String> getCbeffMapping(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, BioMatchType matchType) {
		String formatType = "";
		CbeffDocType cbeffDocType = ((BioMatchType) matchType).getCbeffDocType();
		formatType = String.valueOf(cbeffDocType.getValue());
//		String cbeffKey1 = singleType.name() + "_" + (subType == null ? "" : subType.value())
//				+ (singleSubType == null ? "" : (" " + singleSubType.value())) + "_" + formatType;

		String cbeffKey = null;
		if (subType == null && singleSubType == null) {// for FACE
			cbeffKey = singleType.name() + "__" + formatType;
		} else if (subType != null && singleSubType != null) { // for FINGER
			cbeffKey = singleType.name() + "_" + subType.value() + " " + singleSubType.value() + "_" + formatType;
		} else if (subType != null && singleSubType == null) {
			cbeffKey = singleType.name() + "_" + subType.value() + "_" + formatType; // for IRIS
		}

		return Arrays.asList(cbeffKey);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.IdMapping#getMappingFunction()
	 */
	public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
		return mappingFunction;
	}

	/**
	 * Sets the of.
	 *
	 * @param idMapping the id mapping
	 * @return the sets the
	 */
	public static Set<IdMapping> setOf(IdMapping... idMapping) {
		return Stream.of(idMapping).collect(Collectors.toSet());

	}

	/**
	 * Fetch Id name for Mapping.
	 *
	 * @param mappingName the mapping name
	 * @param mappingConfig the mapping config
	 * @return the id name for mapping
	 */
	public static Optional<String> getIdNameForMapping(String mappingName, MappingConfig mappingConfig) {
		return Stream.of(IdaIdMapping.values()).filter(mapping -> mapping.getSubIdMappings().isEmpty())
				.filter(mapping -> mapping.getMappingFunction().apply(mappingConfig, null).contains(mappingName))
				.findFirst().map(IdaIdMapping::getIdname);
	}

}
