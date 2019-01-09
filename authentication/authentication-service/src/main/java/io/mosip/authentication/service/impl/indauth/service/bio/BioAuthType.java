package io.mosip.authentication.service.impl.indauth.service.bio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.core.spi.fingerprintauth.provider.FingerprintProvider;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.irisauth.provider.IrisProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.iris.CogentIrisProvider;
import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;

/**
 * The Enum BioAuthType.
 *
 * @author Dinesh Karuppiah.T
 */

public enum BioAuthType implements AuthType {

	FGR_MIN("fgrMin",
			setOf(BioMatchType.FGRMIN_LEFT_THUMB, BioMatchType.FGRMIN_LEFT_INDEX, BioMatchType.FGRMIN_LEFT_MIDDLE,
					BioMatchType.FGRMIN_LEFT_RING, BioMatchType.FGRMIN_LEFT_LITTLE, BioMatchType.FGRMIN_RIGHT_THUMB,
					BioMatchType.FGRMIN_RIGHT_INDEX, BioMatchType.FGRMIN_RIGHT_MIDDLE, BioMatchType.FGRMIN_RIGHT_RING,
					BioMatchType.FGRMIN_RIGHT_LITTLE),
		 getFingerprint(), 1) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction<String, String, Double> func =
								idInfoFetcher
								.getFingerPrintProvider(
										bioinfovalue)::matchMinutiae;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
					});
			return valueMap;
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper);
		}
	},
	FGR_IMG("fgrImg",
			setOf(BioMatchType.FGRIMG_LEFT_THUMB, BioMatchType.FGRIMG_LEFT_INDEX, BioMatchType.FGRIMG_LEFT_MIDDLE,
					BioMatchType.FGRIMG_LEFT_RING, BioMatchType.FGRIMG_LEFT_LITTLE, BioMatchType.FGRIMG_RIGHT_THUMB,
					BioMatchType.FGRIMG_RIGHT_INDEX, BioMatchType.FGRIMG_RIGHT_MIDDLE, BioMatchType.FGRIMG_RIGHT_RING,
					BioMatchType.FGRIMG_RIGHT_LITTLE),
			getFingerprint(), 1) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction<String, String, Double> func = idInfoFetcher.getFingerPrintProvider(bioinfovalue)::matchImage;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
					});
			return valueMap;
		}
		
		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper);
		}
	},
	FGR_MIN_MULTI("fgrMin",
			setOf(BioMatchType.FGRMIN_MULTI),
			getFingerprint(), 2) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
				IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction< Map<String, String>,  Map<String, String>, Double> func = idInfoFetcher.getFingerPrintProvider(bioinfovalue)::matchMultiMinutae;
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
					});
			return valueMap;
		}
		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
				Function<LanguageType, String> languageInfoFetcher, Environment environment) {

			String bioType = getType();
			Integer threshold = null;
			String key = bioType.toLowerCase().concat(".multi.default.match.value");
			String property = environment.getProperty(key);
			if (property != null && !property.isEmpty()) {
				threshold = Integer.parseInt(property);
			}
			return Optional.ofNullable(threshold);
		}
		
		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper);
		}
	},
	IRIS_COMP_IMG("irisImg",
			setOf(BioMatchType.IRIS_COMP),
			"Iris", 2) {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
				IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
					.forEach((BioInfo bioinfovalue) -> {
						BiFunction< Map<String, String>,  Map<String, String>, Double> func = idInfoFetcher.getFingerPrintProvider(bioinfovalue)::matchMultiImage;//TODO add provider
						valueMap.put(FingerprintProvider.class.getSimpleName(), func);
					});
			valueMap.put("idvid", authRequestDTO.getIdvId());
			return valueMap;
		}
			
		
		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}
		
	
	  
	  
	},
	IRIS_IMG("irisImg",setOf(BioMatchType.RIGHT_IRIS,BioMatchType.LEFT_IRIS), "Iris", 1){

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO,
				IdInfoFetcher idInfoFetcher) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getBioInfo().stream().filter(bioinfo -> bioinfo.getBioType().equals(this.getType()))
			.forEach((BioInfo bioinfovalue) -> {
				BiFunction< Map<String, String>,  Map<String, String>, Double> func =idInfoFetcher.getIrisProvider(bioinfovalue)::matchImage;//TODO add provider
				valueMap.put(IrisProvider.class.getSimpleName(), func);
			});
			valueMap.put("idvid", authRequestDTO.getIdvId());
			return valueMap;
		}
	 
		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}
	 },
	FACE_IMG("faceImg", Collections.emptySet(), "Face", 1) {
		 @Override
			protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			 //TODO
				return 0L;
		}
	 };

    
    private static final String FINGERPRINT = "Fingerprint"; 
	
	/** The type. */
	private String type;

	/** The associated match types. */
	private Set<MatchType> associatedMatchTypes;

	

	/** The display name. */
	private String displayName;



	/** The count. */
	private int count;

	/**
	 * Instantiates a new bio auth type.
	 *
	 * @param type the type
	 * @param associatedMatchTypes the associated match types
	 * @param displayName the display name
	 * @param count the count
	 */
	private BioAuthType(String type, Set<MatchType> associatedMatchTypes,
			String displayName, int count) {
		this.type = type;
	
		this.displayName = displayName;
		this.count = count;
		this.associatedMatchTypes = Collections.unmodifiableSet(associatedMatchTypes);
	}
	
	protected abstract Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper);


	/**
	 * Gets the FP values count in identity.
	 *
	 * @param reqDTO the req DTO
	 * @param helper the helper
	 * @return the FP values count in identity
	 */
	private static Long getFPValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
		Long count = (long) helper.getIdentityInfo(BioMatchType.FGRMIN_MULTI, reqDTO.getRequest().getIdentity()).size();
		return count;
	}
	
	private  static  Long getIrisValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
		Long count = (long) helper.getIdentityInfo(BioMatchType.IRIS_COMP, reqDTO.getRequest().getIdentity()).size();
		return count;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAssociatedMatchType(io.mosip.authentication.core.spi.indauth.match.MatchType)
	 */
	@Override
	public boolean isAssociatedMatchType(MatchType matchType) {
		return associatedMatchTypes.contains(matchType);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeEnabled(io.mosip.authentication.core.dto.indauth.AuthRequestDTO, io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
		return  authReq.getAuthType().isBio() && getBioIdentityValuesCount(authReq, helper) == getCount();
	}
	
	


	/**
	 * Gets the count.
	 *
	 * @return the count
	 */
	private int getCount() {
		return count;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchingStrategy(io.mosip.authentication.core.dto.indauth.AuthRequestDTO, java.util.function.Function)
	 */
	@Override
	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher) {
		return Optional.of(MatchingStrategyType.PARTIAL.getType());
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getMatchingThreshold(io.mosip.authentication.core.dto.indauth.AuthRequestDTO, java.util.function.Function, org.springframework.core.env.Environment)
	 */
	@Override
	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq,
			Function<LanguageType, String> languageInfoFetcher, Environment environment) {

		String bioType = getType();
		Integer threshold = null;
		String key = bioType.toLowerCase().concat(".min.match.value");
		String property = environment.getProperty(key);
		if (property != null && !property.isEmpty()) {
			threshold = Integer.parseInt(property);
		}
		return Optional.ofNullable(threshold);
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getAssociatedMatchTypes()
	 */
	@Override
	public Set<MatchType> getAssociatedMatchTypes() {
		return Collections.unmodifiableSet(associatedMatchTypes);
	}

	/**
	 * Sets the of.
	 *
	 * @param supportedMatchTypes the supported match types
	 * @return the sets the
	 */
	public static Set<MatchType> setOf(MatchType... supportedMatchTypes) {
		return Stream.of(supportedMatchTypes).collect(Collectors.toSet());
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#getLangType()
	 */
	@Override
	public LanguageType getLangType() {
		return LanguageType.PRIMARY_LANG;
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeInfoAvailable(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getBioInfo()).flatMap(
				list -> list.stream().filter(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

	public static String getFingerprint() {
		return FINGERPRINT;
	}


}
