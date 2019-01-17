package io.mosip.authentication.core.spi.indauth.match;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;

/**
 * Base interface for the match type.
 *
 * @author Loganathan Sekar
 */
public interface MatchType {

	public static enum Category {

		DEMO("demo"), OTP("otp"), BIO("bio");

		/** The type. */
		String type;

		/**
		 * Instantiates a new internal auth type.
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

		public static Optional<Category> getCategory(String type) {
			return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();
		}

	}

	public IdMapping getIdMapping();

	Optional<MatchingStrategy> getAllowedMatchingStrategy(MatchingStrategyType matchStrategyType);

	public Function<IdentityDTO, Map<String,List<IdentityInfoDTO>>> getIdentityInfoFunction();
	
	public default List<IdentityInfoDTO> getIdentityInfoList(IdentityDTO identity) {
		return getIdentityInfoFunction().apply(identity)
				.values()
				.stream()
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}

	public default LanguageType getLanguageType() {
		return LanguageType.PRIMARY_LANG;
	}

	public AuthUsageDataBit getUsedBit();

	public AuthUsageDataBit getMatchedBit();

	public Function<Map<String, String>, Map<String, String>> getEntityInfoMapper();

	public Category getCategory();

}
