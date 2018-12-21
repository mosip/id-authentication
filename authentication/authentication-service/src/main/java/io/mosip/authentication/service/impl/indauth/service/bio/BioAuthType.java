package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;

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
		 "Fingerprint", 1) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction<String, String, Double> func = getFingerPrintProvider(bioinfovalue)::matchMinutiea;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
					});
			return valueMap;
		}
	},
	FGR_IMG("fgrImg",
			setOf(BioMatchType.FGRIMG_LEFT_THUMB, BioMatchType.FGRIMG_LEFT_INDEX, BioMatchType.FGRIMG_LEFT_MIDDLE,
					BioMatchType.FGRIMG_LEFT_RING, BioMatchType.FGRIMG_LEFT_LITTLE, BioMatchType.FGRIMG_RIGHT_THUMB,
					BioMatchType.FGRIMG_RIGHT_INDEX, BioMatchType.FGRIMG_RIGHT_MIDDLE, BioMatchType.FGRIMG_RIGHT_RING,
					BioMatchType.FGRIMG_RIGHT_LITTLE),
			"Fingerprint", 1) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction<String, String, Double> func = getFingerPrintProvider(bioinfovalue)::matchImage;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
					});
			return valueMap;
		}
	},
	FGR_MIN_MULTI("fgrMin",
			setOf(BioMatchType.FGRIMG_LEFT_THUMB, BioMatchType.FGRIMG_LEFT_INDEX, BioMatchType.FGRIMG_LEFT_MIDDLE,
					BioMatchType.FGRIMG_LEFT_RING, BioMatchType.FGRIMG_LEFT_LITTLE, BioMatchType.FGRIMG_RIGHT_THUMB,
					BioMatchType.FGRIMG_RIGHT_INDEX, BioMatchType.FGRIMG_RIGHT_MIDDLE, BioMatchType.FGRIMG_RIGHT_RING,
					BioMatchType.FGRIMG_RIGHT_LITTLE),
			"Fingerprint", 2) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
				IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction< Map<String, String>,  Map<String, String>, Double> func = getFingerPrintProvider(bioinfovalue)::matchMultiImage;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
					});
			return valueMap;
		}
	},
	IRIS_IMG("irisImg", Collections.emptySet(), "Iris", 1),
	FACE_IMG("faceImg", Collections.emptySet(), "Face", 1);
	private String type;

	/**  */
	private Set<MatchType> associatedMatchTypes;

	

	private String displayName;

	private static MantraFingerprintProvider mantraFingerprintProvider = new MantraFingerprintProvider();

	private static CogentFingerprintProvider cogentFingerprintProvider = new CogentFingerprintProvider();

	private int count;

	private BioAuthType(String type, Set<MatchType> associatedMatchTypes,
			String displayName, int count) {
		this.type = type;
	
		this.displayName = displayName;
		this.count = count;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}

	private Long getFPValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
		Set<MatchType> matchTypes = getAssociatedMatchTypes();
		Long count = matchTypes.stream().filter(matchType -> helper.getIdentityInfo(matchType, reqDTO.getRequest().getIdentity()).size() > 0).count();
		return count;
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
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
		return  authReq.getAuthType().isBio() && getFPValuesCountInIdentity(authReq, helper) == getCount();
	}


	private int getCount() {
		return count;
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
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getBioInfo()).flatMap(
				list -> list.stream().filter(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

	private static FingerprintProvider getFingerPrintProvider(BioInfo bioinfovalue) {
		FingerprintProvider provider = null;
		if (bioinfovalue.getDeviceInfo().getMake().equalsIgnoreCase("mantra")) {
			provider = mantraFingerprintProvider;
		} else if (bioinfovalue.getDeviceInfo().getMake().equalsIgnoreCase("cogent")) {
			provider = cogentFingerprintProvider;
		}

		return provider;
	}

}
