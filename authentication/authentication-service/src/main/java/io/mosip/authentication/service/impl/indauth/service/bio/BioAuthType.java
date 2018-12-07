package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.env.Environment;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum BioAuthType implements AuthType {

	FGR_MIN("fgrMin",
			setOf(BioMatchType.FGRMIN_LEFT_THUMB, BioMatchType.FGRMIN_LEFT_INDEX, BioMatchType.FGRMIN_LEFT_MIDDLE,
					BioMatchType.FGRMIN_LEFT_RING, BioMatchType.FGRMIN_LEFT_LITTLE, BioMatchType.FGRMIN_RIGHT_THUMB,
					BioMatchType.FGRMIN_RIGHT_INDEX, BioMatchType.FGRMIN_RIGHT_MIDDLE, BioMatchType.FGRMIN_RIGHT_RING,
					BioMatchType.FGRMIN_RIGHT_LITTLE),
			AuthTypeDTO::isBio, "Fingerprint"),
	FGR_IMG("fgrImg",
			setOf(BioMatchType.FGRIMG_LEFT_THUMB, BioMatchType.FGRIMG_LEFT_INDEX, BioMatchType.FGRIMG_LEFT_MIDDLE,
					BioMatchType.FGRIMG_LEFT_RING, BioMatchType.FGRIMG_LEFT_LITTLE, BioMatchType.FGRIMG_RIGHT_THUMB,
					BioMatchType.FGRIMG_RIGHT_INDEX, BioMatchType.FGRIMG_RIGHT_MIDDLE, BioMatchType.FGRIMG_RIGHT_RING,
					BioMatchType.FGRIMG_RIGHT_LITTLE),
			AuthTypeDTO::isBio, "Fingerprint"),
	IRIS_IMG("irisImg", Collections.emptySet(), AuthTypeDTO::isBio, "Iris"),
	FACE_IMG("faceImg", Collections.emptySet(), AuthTypeDTO::isBio, "Face");
	private String type;

	/**  */
	private Set<MatchType> associatedMatchTypes;

	private Predicate<? super AuthTypeDTO> authTypePredicate;

	private String displayName;

	private BioAuthType(String type, Set<MatchType> associatedMatchTypes,
			Predicate<? super AuthTypeDTO> authTypePredicate, String displayName) {
		this.type = type;
		this.authTypePredicate = authTypePredicate;
		this.displayName = displayName;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isAssociatedMatchType(MatchType matchType) {
		return associatedMatchTypes.contains(matchType);
	}

	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq) {
		return Optional.of(authReq).map(AuthRequestDTO::getAuthType).filter(authTypePredicate).isPresent();
	}

	@Override
	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher) {
		return Optional.of(MatchingStrategyType.PARTIAL.getType());
	}

	@Override
	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher, Environment environment) {

		String bioType = getType();
		Integer threshold = null;
		String key = bioType.toLowerCase().concat(".default.match.value");
		String property = environment.getProperty(key);
		if (property != null && !property.isEmpty()) {
			threshold = Integer.parseInt(property);
		}
		return Optional.ofNullable(threshold);
	}

	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
	}

	/**
	 * Sets the of.
	 *
	 * @param supportedMatchTypes
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}

	@Override
	public LanguageType getLangType() {
		return LanguageType.PRIMARY_LANG;
	}

	@Override
	public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
			Function<LanguageType, String> languageInfoFetcher) {
		Map<String, Object> valueMap = new HashMap<>();
		authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
				.forEach(bioinfovalue -> valueMap.put(BioInfo.class.getSimpleName(), bioinfovalue));
		return valueMap;
	}

	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getBioInfo()).flatMap(
				list -> list.stream().filter(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

}
