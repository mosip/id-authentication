/*
 * 
 */
package io.mosip.authentication.common.service.impl.match;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
 * Mapping class for IDA.
 *
 * @author Dinesh Karuppiah.T
 */
public enum IdaIdMapping implements IdMapping {

// @formatter:off
	/** The name. */
//PI
	NAME("name", MappingConfig::getName),
	
	/** The dob. */
	DOB("dob", MappingConfig::getDob),
	
	/** The dobtype. */
	DOBTYPE("dobType", MappingConfig::getDobType),
	
	/** The age. */
	AGE("age", MappingConfig::getAge),
	
	/** The gender. */
	GENDER("gender", MappingConfig::getGender),
	
	/** The phone. */
	PHONE("phoneNumber", MappingConfig::getPhoneNumber),
	
	/** The email. */
	EMAIL("emailId", MappingConfig::getEmailId),
	
	/** The addressline1. */
	ADDRESSLINE1("addressLine1", MappingConfig::getAddressLine1),
	
	/** The addressline2. */
	ADDRESSLINE2("addressLine2", MappingConfig::getAddressLine2),
	
	/** The addressline3. */
	ADDRESSLINE3("addressLine3", MappingConfig::getAddressLine3),
	
	/** The location1. */
	LOCATION1("location1", MappingConfig::getLocation1),
	
	/** The location2. */
	LOCATION2("location2", MappingConfig::getLocation2),
	
	/** The location3. */
	LOCATION3("location3", MappingConfig::getLocation3),
	
	/** The pincode. */
	PINCODE("postalCode", MappingConfig::getPostalCode),
	
	/** The fulladdress. */
	FULLADDRESS("fullAddress", MappingConfig::getFullAddress),
	
	/** The otp. */
	//PIN
	OTP("otp", MappingConfig::getOtp),
	
	/** The pin. */
	PIN("pin", MappingConfig::getPin),
	
	//FINGER
	/** The leftindex. */
	//BIO - Finger - Single
	LEFTINDEX(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.INDEX_FINGER.value()), SingleType.FINGER.value()),
	
	/** The leftlittle. */
	LEFTLITTLE(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.LITTLE_FINGER.value()), SingleType.FINGER.value()),
	
	/** The leftmiddle. */
	LEFTMIDDLE(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.MIDDLE_FINGER.value()), SingleType.FINGER.value()),
	
	/** The leftring. */
	LEFTRING(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.RING_FINGER.value()), SingleType.FINGER.value()),
	
	/** The leftthumb. */
	LEFTTHUMB(concatNames(SingleAnySubtypeType.LEFT.value(), SingleAnySubtypeType.THUMB.value()), SingleType.FINGER.value()),
	
	/** The rightindex. */
	RIGHTINDEX(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.INDEX_FINGER.value()), SingleType.FINGER.value()),
	
	/** The rightlittle. */
	RIGHTLITTLE(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.LITTLE_FINGER.value()), SingleType.FINGER.value()),
	
	/** The rightmiddle. */
	RIGHTMIDDLE(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.MIDDLE_FINGER.value()), SingleType.FINGER.value()),
	
	/** The rightring. */
	RIGHTRING(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.RING_FINGER.value()), SingleType.FINGER.value()),
	
	/** The rightthumb. */
	RIGHTTHUMB(concatNames(SingleAnySubtypeType.RIGHT.value(), SingleAnySubtypeType.THUMB.value()), SingleType.FINGER.value()),
	
	/** The unknown finger. */
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
	
	/** The fingerprint. */
	FINGERPRINT("fingerprint", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB, UNKNOWN_FINGER), SingleType.FINGER.value()),
	
	//IRIS
	/** The leftiris. */
	//BIO - Iris - Single
	LEFTIRIS(SingleAnySubtypeType.LEFT.value(), SingleType.IRIS.value()),
	
	/** The rightiris. */
	RIGHTIRIS(SingleAnySubtypeType.RIGHT.value(), SingleType.IRIS.value()),
	
	/** The unknown iris. */
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
	
	/** The iris. */
	IRIS("iris", setOf(RIGHTIRIS, LEFTIRIS, UNKNOWN_IRIS), SingleType.IRIS.value()),
	
	//FACE
	/** The face. */
	//BIO - Face - Single
	FACE( SingleType.FACE.value(), SingleType.FACE.value()),
	
	/** The unknown face. */
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
	
	/** The multi modal biometrics. */
	MULTI_MODAL_BIOMETRICS("biometrics", setOf(LEFTINDEX, LEFTLITTLE, LEFTMIDDLE, LEFTRING, LEFTTHUMB, 
			RIGHTINDEX, RIGHTLITTLE, RIGHTMIDDLE, RIGHTRING, RIGHTTHUMB, UNKNOWN_FINGER,
			RIGHTIRIS, LEFTIRIS, UNKNOWN_IRIS,
			FACE,UNKNOWN_FACE), "DummyType"),
	
	/** The dynamic demographics ID Mapping. */
	DYNAMIC("demographics") {
		
		public BiFunction<MappingConfig, MatchType, List<String>> getMappingFunction() {
			return (mappingConfig, matchType) -> {
				Map<String, List<String>> dynamicAttributes = mappingConfig.getDynamicAttributes();
				return dynamicAttributes.keySet().stream().collect(Collectors.toList());
			};
		}
	}
	
	;


// @formatter:on

	/** The idname. */
private String idname;

	/** The mapping function. */
	private BiFunction<MappingConfig, MatchType, List<String>> mappingFunction;

	/** The sub id mappings. */
	private Set<IdMapping> subIdMappings;

	/** The type. */
	private String type;

	/**
	 * Instantiates a new ida id mapping.
	 *
	 * @param idname the idname
	 * @param mappingFunction the mapping function
	 */
	private IdaIdMapping(String idname, Function<MappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = wrapFunctionToReturnEmptyListForNull((cfg, matchType) -> mappingFunction.apply(cfg));
		this.subIdMappings = Collections.emptySet();
	}

	/**
	 * Instantiates a new ida id mapping.
	 *
	 * @param idname the idname
	 * @param type the type
	 */
	private IdaIdMapping(String idname, String type) {
		this.idname = idname;
		this.type = type;
		this.mappingFunction = wrapFunctionToReturnEmptyListForNull((mappingConfig, matchType) -> getCbeffMapping(matchType));
		this.subIdMappings = Collections.emptySet();
	}

	/**
	 * Instantiates a new ida id mapping.
	 *
	 * @param idname the idname
	 * @param subIdMappings the sub id mappings
	 * @param type the type
	 */
	private IdaIdMapping(String idname, Set<IdMapping> subIdMappings, String type) {
		this.idname = idname;
		this.subIdMappings = subIdMappings;
		this.type = type;
		this.mappingFunction = wrapFunctionToReturnEmptyListForNull((mappingConfig, matchType) -> {
			if (matchType instanceof BioMatchType) {
				List<String> collection = Stream.of(((BioMatchType) matchType).getMatchTypesForSubIdMappings(subIdMappings))
						.flatMap(subMatchType -> subMatchType.getIdMapping().getMappingFunction()
								.apply(mappingConfig, subMatchType).stream())
						.collect(Collectors.toList());
				return collection;
			} else {
				return Collections.emptyList();
			}
		});
	}
	
	/**
	 * Instantiates a new ida id mapping.
	 *
	 * @param idname the idname
	 */
	private IdaIdMapping(String idname) {
		this.idname = idname;
		this.mappingFunction = wrapFunctionToReturnEmptyListForNull((cfg, matchType) -> getMappingFunction().apply(cfg, matchType));
		this.subIdMappings = Collections.emptySet();
	}
	
	/**
	 * Wrap function to return empty list for null.
	 *
	 * @param func the func
	 * @return the bi function
	 */
	private BiFunction<MappingConfig, MatchType, List<String>> wrapFunctionToReturnEmptyListForNull(BiFunction<MappingConfig, MatchType, List<String>> func) {
		return (cfg, matchType) -> {
			List<String> retVal = func.apply(cfg, matchType);
			if(retVal == null) {
				return Collections.emptyList();
			} else {
				return retVal;
			}
		};
	}

	/**
	 * Gets the idname.
	 *
	 * @return the idname
	 */
	public String getIdname() {
		return idname;
	}
	
	/**
	 * Unknown.
	 *
	 * @return the string
	 */
	private static String unknown() {
		return IdAuthCommonConstants.UNKNOWN_BIO;
	}

	/**
	 * Gets the sub id mappings.
	 *
	 * @return the sub id mappings
	 */
	public Set<IdMapping> getSubIdMappings() {
		return subIdMappings;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Gets the sub type.
	 *
	 * @return the sub type
	 */
	public String getSubType() {
		return idname;
	}
	
	/**
	 * Fetch Cbeff Mapping based on Match Type.
	 *
	 * @param matchType the match type
	 * @return the cbeff mapping
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
	 * To get Cbeff mapping based on Single and SubType on Cbeff.
	 *
	 * @param singleType the single type
	 * @param subType the sub type
	 * @param singleSubType the single sub type
	 * @param matchType the match type
	 * @return the cbeff mapping
	 */
	private static List<String> getCbeffMapping(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, BioMatchType matchType) {
		List<String> collection = Stream.of(matchType.getCbeffDocTypes())
						.map(cbeffDocType -> getCbeffMappingForCbeffDocType(singleType, subType, singleSubType, cbeffDocType))
						.collect(Collectors.toList());
		return collection;
	}

	/**
	 * Gets the cbeff mapping for cbeff doc type.
	 *
	 * @param singleType the single type
	 * @param subType the sub type
	 * @param singleSubType the single sub type
	 * @param cbeffDocType the cbeff doc type
	 * @return the cbeff mapping for cbeff doc type
	 */
	private static String getCbeffMappingForCbeffDocType(SingleType singleType, SingleAnySubtypeType subType,
			SingleAnySubtypeType singleSubType, CbeffDocType cbeffDocType) {
		String formatType = String.valueOf(cbeffDocType.getValue());

		String cbeffKey = null;
		if (subType == null && singleSubType == null) {// for FACE
			cbeffKey = singleType.name() + "__" + formatType;
		} else if (subType != null && singleSubType != null) { // for FINGER
			cbeffKey = singleType.name() + "_" + subType.value() + " " + singleSubType.value() + "_" + formatType;
		} else if (subType != null && singleSubType == null) {
			cbeffKey = singleType.name() + "_" + subType.value() + "_" + formatType; // for IRIS
		}
		return cbeffKey;
	}

	/**
	 * Gets the mapping function.
	 *
	 * @return the mapping function
	 */
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
	
	/**
	 * Concat names.
	 *
	 * @param values the values
	 * @return the string
	 */
	public static String concatNames(String... values) {
		return Stream.of(values).collect(Collectors.joining(" "));
	}

}
