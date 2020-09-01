/*
 * 
 */
package io.mosip.authentication.common.service.impl.match;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
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
	LEFTINDEX(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.INDEX_FINGER.value()), SingleType.FINGER.value()),
	LEFTLITTLE(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.LITTLE_FINGER.value()), SingleType.FINGER.value()),
	LEFTMIDDLE(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.MIDDLE_FINGER.value()), SingleType.FINGER.value()),
	LEFTRING(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.RING_FINGER.value()), SingleType.FINGER.value()),
	LEFTTHUMB(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.THUMB.value()), SingleType.FINGER.value()),
	RIGHTINDEX(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.INDEX_FINGER.value()), SingleType.FINGER.value()),
	RIGHTLITTLE(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.LITTLE_FINGER.value()), SingleType.FINGER.value()),
	RIGHTMIDDLE(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.MIDDLE_FINGER.value()), SingleType.FINGER.value()),
	RIGHTRING(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.RING_FINGER.value()), SingleType.FINGER.value()),
	RIGHTTHUMB(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()), SingleType.FINGER.value()),
	//BIO - Finger - Multi or Unknown
	UNKNOWN_FINGER(
			concatNames(unknown() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER,
					SingleType.FINGER.value() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER),
			setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB,			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB), SingleType.FINGER.value()) {
		@Override
		public String getSubType() {
			return unknown();
		}
	},
	
	FINGERPRINT("fingerprint", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB, UNKNOWN_FINGER), SingleType.FINGER.value()),
	
	//IRIS
	//BIO - Iris - Single
	LEFTIRIS(SingleAnySubtypeType.LEFT.value(), SingleType.IRIS.value()),
	RIGHTIRIS(SingleAnySubtypeType.RIGHT.value(), SingleType.IRIS.value()),
	//BIO - Iris - Multi or Unknown
	UNKNOWN_IRIS(
			concatNames(unknown() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER,
					SingleType.IRIS.value() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER),
			setOf(RIGHTIRIS, LEFTIRIS), SingleType.IRIS.value()) {
		@Override
		public String getSubType() {
			return unknown();
		}
	},
	IRIS("iris", setOf(RIGHTIRIS, LEFTIRIS, UNKNOWN_IRIS), SingleType.IRIS.value()),
	
	//FACE
	//BIO - Face - Single
	FACE( SingleType.FACE.value(), SingleType.FACE.value()),
	//BIO - Face - Unknown
	UNKNOWN_FACE(
			concatNames(unknown() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER,
					SingleType.FACE.value() + IdAuthCommonConstants.UNKNOWN_COUNT_PLACEHOLDER),
			setOf(FACE), SingleType.FACE.value()) {
		@Override
		public String getSubType() {
			return unknown();
		}
	}, 
	
	MULTI_MODAL_BIOMETRICS("biometrics", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB, UNKNOWN_FINGER,
			RIGHTIRIS, LEFTIRIS, UNKNOWN_IRIS,
			FACE,UNKNOWN_FACE), "DummyType");


// @formatter:on

	private String idname;

	private BiFunction<MappingConfig, MatchType, List<String>> mappingFunction;

	private Set<IdMapping> subIdMappings;

	private String type;

	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = (cfg, matchType) -> mappingFunction.apply(cfg);
		this.subIdMappings = Collections.emptySet();
	}

	private IdaIdMapping(String idname, String type) {
		this.idname = idname;
		this.type = type;
		this.mappingFunction = (mappingConfig, matchType) -> getCbeffMapping(matchType);
		this.subIdMappings = Collections.emptySet();
	}

	private IdaIdMapping(String idname, Set<IdMapping> subIdMappings, String type) {
		this.idname = idname;
		this.subIdMappings = subIdMappings;
		this.type = type;
		this.mappingFunction = (mappingConfig, matchType) -> {
			if (matchType instanceof BioMatchType) {
				List<String> collection = Stream.of(((BioMatchType) matchType).getMatchTypesForSubIdMappings(subIdMappings))
						.flatMap(subMatchType -> subMatchType.getIdMapping().getMappingFunction()
								.apply(mappingConfig, subMatchType).stream())
						.collect(Collectors.toList());
				return collection;
			} else {
				return Collections.emptyList();
			}
		};
	}

	public String getIdname() {
		return idname;
	}
	
	private static String unknown() {
		return IdAuthCommonConstants.UNKNOWN_BIO;
	}

	public Set<IdMapping> getSubIdMappings() {
		return subIdMappings;
	}
	
	public String getType() {
		return type;
	}
	
	public String getSubType() {
		return idname;
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
			 List<String> collection = Stream.of(bioMatchType.getCbeffDocTypes())
					.flatMap(cbeffDocType -> getCbeffMapping(cbeffDocType.getType(), bioMatchType.getSubType(),
							bioMatchType.getSingleAnySubtype(), bioMatchType).stream())
					.collect(Collectors.toList());
			return collection;
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
		List<String> collection = Stream.of(matchType.getCbeffDocTypes())
						.map(cbeffDocType -> getCbeffMappingForCbeffDocType(singleType, subType, singleSubType, cbeffDocType))
						.collect(Collectors.toList());
		return collection;
	}

	private static String getCbeffMappingForCbeffDocType(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, CbeffDocType cbeffDocType) {
		//String formatType = String.valueOf(cbeffDocType.getValue());

		String cbeffKey = null;
		if (subType == null && singleSubType == null) {// for FACE
			cbeffKey = singleType.name();// + "__" + formatType;
		} else if (subType != null && singleSubType != null) { // for FINGER
			cbeffKey = singleType.name() + "_" + subType.value() + " " + singleSubType.value();// + "_" + formatType;
		} else if (subType != null && singleSubType == null) {
			cbeffKey = singleType.name() + "_" + subType.value();// + "_" + formatType; // for IRIS
		}
		return cbeffKey;
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
	
	public static String concatNames(String... values) {
		return Stream.of(values).collect(Collectors.joining(" "));
	}

}
