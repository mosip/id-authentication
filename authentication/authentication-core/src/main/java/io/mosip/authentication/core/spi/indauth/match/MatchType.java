
package io.mosip.authentication.core.spi.indauth.match;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;

/**
 * Base interface for the match type.
 *
 * @author Loganathan Sekar
 * @author Dinesh Karuppiah.T
 */
public interface MatchType {

	/**
	 * The Category Enum
	 */
	public enum Category {

		/** Demo category */
		DEMO("demo"),
		/** OTP category */
		OTP("otp"),
		/** Bio category */
		BIO("bio"),
		/** s-pin category. */
		SPIN("pin"),
		/** Token category */
		KBT("kbt");

		/** The type. */
		String type;

		/**
		 * Instantiates a Category.
		 *
		 * @param type the type
		 */
		private Category(String type) {
			this.type = type;
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
		 * Get the category for the type.
		 *
		 * @param type the type
		 * @return Optional of category
		 */
		public static Optional<Category> getCategory(String type) {
			return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();
		}

	}

	/**
	 * Gets the IDMapping.
	 *
	 * @return ID Mapping
	 */
	public IdMapping getIdMapping();

	/**
	 * Gets the allowed matching strategy for the MatchingStrategyType value.
	 *
	 * @param matchStrategyType the match strategy type
	 * @return the allowed matching strategy
	 */
	Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType);

	/**
	 * Get the Identity Info Function.
	 *
	 * @return the Identity Info Function
	 */
	public Function<RequestDTO, Map<String, List<IdentityInfoDTO>>> getIdentityInfoFunction();

	/**
	 * Get the Identity Info Function.
	 *
	 * @return the reqest info function
	 */
	public default Function<AuthRequestDTO, Map<String, String>> getReqestInfoFunction() {
		return req -> Collections.emptyMap();
	}

	/**
	 * Get the IdentityInfoDTO list out of the identity block for this MatchType.
	 *
	 * @param identity the IdentityDTO
	 * @return the list of IdentityInfoDTO
	 */
	public default List<IdentityInfoDTO> getIdentityInfoList(RequestDTO identity) {
		return getIdentityInfoFunction().apply(identity).values().stream().filter(Objects::nonNull)
				.flatMap(List::stream).collect(Collectors.toList());
	}

	/**
	 * Gets the Entity info mapper function.
	 *
	 * @return the Entity info mapper function
	 */
	public BiFunction<Map<String, String>, Map<String, Object>, Map<String, String>> getEntityInfoMapper();

	/**
	 * Get the category of this MatchType.
	 *
	 * @return the category
	 */
	public Category getCategory();

	/**
	 * Flag to fetch Identity Info.
	 *
	 * @return boolean value true or false
	 */
	public default boolean hasIdEntityInfo() {
		return true;
	}

	/**
	 * Flag to fetch Request Entity Info.
	 *
	 * @return the flag
	 */
	public default boolean hasRequestEntityInfo() {
		return false;
	}

	/**
	 * Flag to check MultiLanguage.
	 *
	 * @return the flag
	 */
	public default boolean isMultiLanguage() {
		return false;
	}

	public default boolean isMultiLanguage(String propName, Map<String, List<IdentityInfoDTO>> identityEntity, MappingConfig mappingConfig) {
		return isMultiLanguage();
	}
	/**
	 * Returns the set of given matching strategies.
	 *
	 * @param items the matching strategies
	 * @return the sets the
	 */
	public static <T>Set<T> setOf(T... items) {
		return Stream.of(items).collect(Collectors.toSet());

	}

	/**
	 * To fetch Map Entity Info.
	 *
	 * @param idEntity the id entity
	 * @param idInfoHelper the id info helper
	 * @return the map
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public default Map<String, Entry<String, List<IdentityInfoDTO>>> mapEntityInfo(
			Map<String, List<IdentityInfoDTO>> idEntity, IdInfoFetcher idInfoHelper)
			throws IdAuthenticationBusinessException {
		return idEntity.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> new SimpleEntry<>(entry.getKey(), entry.getValue())));
	}

	/**
	 * Check if the mapped property is of multi-language type.
	 *
	 * @param propName mapped property name
	 * @param cfg mapping
	 * @return true, if is prop multi lang
	 */
	public default boolean isPropMultiLang(String propName, MappingConfig cfg) {
		return false;
	}
	
	public default boolean isDynamic() {
		return false;
	}

}

