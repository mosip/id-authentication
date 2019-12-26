package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.authentication.core.spi.indauth.match.TriFunctionWithBusinessException;

/**
 * The Enum BioAuthType.
 *
 * @author Dinesh Karuppiah.T
 */

public enum BioAuthType implements AuthType {

	//TODO to be removed
	FGR_MIN("FMR",
			AuthType.setOf(BioMatchType.FGRMIN_LEFT_THUMB, BioMatchType.FGRMIN_LEFT_INDEX,
					BioMatchType.FGRMIN_LEFT_MIDDLE, BioMatchType.FGRMIN_LEFT_RING, BioMatchType.FGRMIN_LEFT_LITTLE,
					BioMatchType.FGRMIN_RIGHT_THUMB, BioMatchType.FGRMIN_RIGHT_INDEX, BioMatchType.FGRMIN_RIGHT_MIDDLE,
					BioMatchType.FGRMIN_RIGHT_RING, BioMatchType.FGRMIN_RIGHT_LITTLE, BioMatchType.FGRMIN_UNKNOWN),
			getFingerprintName(), count -> count == 1, "bio-FMR", "fmr") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchValue;
				valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
				valueMap.put(BioAuthType.class.getSimpleName(), this);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRMIN_COMPOSITE);
		}
	},
	FGR_IMG("FIR",
			AuthType.setOf(BioMatchType.FGRIMG_LEFT_THUMB, BioMatchType.FGRIMG_LEFT_INDEX,
					BioMatchType.FGRIMG_LEFT_MIDDLE, BioMatchType.FGRIMG_LEFT_RING, BioMatchType.FGRIMG_LEFT_LITTLE,
					BioMatchType.FGRIMG_RIGHT_THUMB, BioMatchType.FGRIMG_RIGHT_INDEX, BioMatchType.FGRIMG_RIGHT_MIDDLE,
					BioMatchType.FGRIMG_RIGHT_RING, BioMatchType.FGRIMG_RIGHT_LITTLE, BioMatchType.FGRIMG_UNKNOWN),
			getFingerprintName(), value -> value == 1, "bio-FIR", "fir") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchValue;
				valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
				valueMap.put(BioAuthType.class.getSimpleName(), this);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRIMG_COMPOSITE);
		}
	},
	//TODO to be removed
	FGR_MIN_COMPOSITE("FMR", AuthType.setOf(BioMatchType.FGRMIN_COMPOSITE), getFingerprintName(), value -> value == 2,
			"bio-FIR", "bio") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchMultiValue;
				valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRMIN_COMPOSITE);
		}
	},

	FGR_IMG_COMPOSITE("FIR", AuthType.setOf(BioMatchType.FGRIMG_COMPOSITE), getFingerprintName(), 
			value -> value >= 2 && value <= 10,
			"bio-FIR", "bio") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchMultiValue;
				valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRIMG_COMPOSITE);
		}
	},

	IRIS_COMP_IMG("IIR", AuthType.setOf(BioMatchType.IRIS_COMP), getIrisName(), value -> value == 2, "bio-IIR", "bio") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchMultiValue;
				valueMap.put(IdaIdMapping.IRIS.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}

	},
	IRIS_IMG("IIR", AuthType.setOf(BioMatchType.RIGHT_IRIS, BioMatchType.LEFT_IRIS, BioMatchType.IRIS_UNKNOWN), getIrisName(),
			value -> value == 1, "bio-IIR", "iir") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchValue;
				valueMap.put(IdaIdMapping.IRIS.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values());
			}
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(SINGLE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}
	},
	FACE_IMG("FACE", AuthType.setOf(BioMatchType.FACE, BioMatchType.FACE_UNKNOWN), getFaceName(), value -> value == 1,
			"bio-FACE", "fid") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchValue;
				valueMap.put(IdaIdMapping.FACE.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values()); 
			}
			return valueMap;

		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(SINGLE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return getFaceValuesCountInIdentity(reqDTO, helper);
		}
	},
	
	MULTI_MODAL("bio", AuthType.setOf(BioMatchType.MULTI_MODAL), getBiometricsName(), null, "bio-composite-dummyConfigKey", "bio") {

		@Override
		public String[] getTypes() {
			return new String[] {FGR_IMG.getType(), IRIS_IMG.getType(), FACE_IMG.getType()};
		}
		
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			if(isAuthTypeInfoAvailable(authRequestDTO)) {
				TriFunctionWithBusinessException<Map<String, String>, Map<String, String>, Map<String, Object>, Double> func = idInfoFetcher
						.getBioMatcherUtil()::matchMultiValue;
				valueMap.put(IdaIdMapping.MULTI_MODAL_BIOMETRICS.getIdname(), func);
				valueMap.put(IdMapping.class.getSimpleName(), IdaIdMapping.values()); 
			}
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
			return authReq.getRequestedAuth().isBio() && hasMultiModalBiometrics(authReq, helper);
		}
		
		@Override
		public String getDisplayName(AuthRequestDTO authReq, IdInfoFetcher helper) {
			return getMultiModalBiometrics(authReq, helper)
					.stream()
					.map(BioAuthType::getDisplayName)
					.collect(Collectors.joining(","));
		}

		private boolean hasMultiModalBiometrics(AuthRequestDTO authReq, IdInfoFetcher helper) {
			return getMultiModalBiometrics(authReq, helper).size() > 1;
		}
		
		private Set<BioAuthType> getMultiModalBiometrics(AuthRequestDTO authReq, IdInfoFetcher helper) {
			boolean hasFingerType = BioAuthType.getFPValuesCountInIdentity(authReq, helper, BioMatchType.FGRIMG_COMPOSITE) > 0;
			boolean hasIrisType = BioAuthType.getIrisValuesCountInIdentity(authReq, helper) > 0;
			boolean hasFaceType = BioAuthType.getFaceValuesCountInIdentity(authReq, helper) > 0;
			Set<BioAuthType> authTypes = new LinkedHashSet<>();
			if(hasFingerType) authTypes.add(FGR_IMG);
			if(hasIrisType) authTypes.add(IRIS_IMG);
			if(hasFaceType) authTypes.add(FACE_IMG);
			return authTypes;
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			// Dummy implementation. This is not required as the isAuthTypeEnabled method is
			// overridden.
			return -1L;
		}
		
	};

	private static final String FACE = "Face";

	private static final String BIOMETRICS = "biometrics";

	private static final String IRIS = "Iris";

	/** The Constant SINGLE_THRESHOLD. */
	private static final String SINGLE_THRESHOLD = ".single.threshold";

	/** The Constant COMPOSITE_THRESHOLD. */
	private static final String COMPOSITE_THRESHOLD = ".composite.threshold";

	/** The Constant FINGERPRINT. */
	private static final String FINGERPRINT = "Fingerprint";

	private AuthTypeImpl authTypeImpl;

	private IntPredicate countPredicate;

	private String configNameValue;
	
	private String thresholdConfigKey;

	/**
	 * Instantiates a new bio auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param displayName          the display name
	 * @param count                the count
	 */
	private BioAuthType(String type, Set<MatchType> associatedMatchTypes, String displayName,
			IntPredicate countPredicate, String configNameValue, String thresholdConfigKey) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, displayName);
		this.countPredicate = countPredicate;
		this.configNameValue = configNameValue;
		this.thresholdConfigKey = thresholdConfigKey;
	}

	protected abstract Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper);

	/**
	 * Gets the FP values count in identity.
	 *
	 * @param reqDTO           the req DTO
	 * @param helper           the helper
	 * @param fpMultiMatchType
	 * @return the FP values count in identity
	 */
	private static Long getFPValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper,
			MatchType fpMultiMatchType) {
		return (long) helper.getIdentityRequestInfo(fpMultiMatchType, reqDTO.getRequest(), null).size();
	}

	/**
	 * Gets the iris values count in identity.
	 *
	 * @param reqDTO the req DTO
	 * @param helper the helper
	 * @return the iris values count in identity
	 */
	private static Long getIrisValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
		return (long) helper.getIdentityRequestInfo(BioMatchType.IRIS_COMP, reqDTO.getRequest(), null).size();
	}
	
	private static Long getFaceValuesCountInIdentity(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
		long entries = 0;
		for (MatchType matchType : AuthType.setOf(BioMatchType.FACE, BioMatchType.FACE_UNKNOWN)) {
			entries += (long) helper.getIdentityRequestInfo(matchType, reqDTO.getRequest(), null).size();
		}
		return entries;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.authentication.core.spi.indauth.match.AuthType#isAuthTypeEnabled(io.
	 * mosip.authentication.core.dto.indauth.AuthRequestDTO,
	 * io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher)
	 */
	@Override
	public boolean isAuthTypeEnabled(AuthRequestDTO authReq, IdInfoFetcher helper) {
		return authReq.getRequestedAuth().isBio()
				&& countPredicate.test(getBioIdentityValuesCount(authReq, helper).intValue())
				// here, it is assumed that MULTI_MODAL.isAuthTypeEnabled has been overridden,
				// otherwise will result in recursion
				&& !MULTI_MODAL.isAuthTypeEnabled(authReq, helper);
	}

	/**
	 * To Get Matching Strategy
	 */
	@Override
	public Optional<String> getMatchingStrategy(AuthRequestDTO authReq, String languageInfoFetcher) {
		return Optional.of(MatchingStrategyType.PARTIAL.getType());
	}

	/**
	 * Get Matching Threshold
	 */
	@Override
	public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
			Environment environment, IdInfoFetcher idInfoFetcher) {
		return idInfoFetcher.getMatchingThreshold(getThresholdConfigKey().toLowerCase().concat(SINGLE_THRESHOLD));
	}

	/*
	 * Checks is Authtype information available based on authreqest
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		Set<String> typesLower = Stream.of(getTypes()).map(String::toLowerCase)
			.collect(Collectors.toSet());
		return Optional.ofNullable(authRequestDTO.getRequest().getBiometrics())
				.flatMap(list -> list.stream().map(BioIdentityInfoDTO::getData)
						.filter(bioInfo -> typesLower.contains(bioInfo.getBioType().toLowerCase())).findAny())
				.isPresent();
	}

	private static String getFingerprintName() {
		return FINGERPRINT;
	}
	
	private static String getIrisName() {
		return IRIS;
	}
	
	private static String getFaceName() {
		return FACE;
	}
	
	private static String getBiometricsName() {
		return BIOMETRICS;
	}
	

	/**
	 * This method accepts the bioType and it will return Optional of BioAuthType
	 * only when the count is single.
	 *
	 * @param type the type
	 * @return the single bio auth type for type
	 */
	public static Optional<BioAuthType> getSingleBioAuthTypeForType(String type) {
		BioAuthType[] values = BioAuthType.values();
		return Stream.of(values).filter(authType -> {
			int singleBioCount = 1;
			return authType.getType().equalsIgnoreCase(type) && authType.getCountPredicate().test(singleBioCount);
		}).findAny();
	}

	public static Optional<String> getTypeForConfigNameValue(String configNameValue) {
		BioAuthType[] values = BioAuthType.values();
		return Stream.of(values).filter(authtype -> authtype.getConfigNameValue().equalsIgnoreCase(configNameValue))
				.map(BioAuthType::getType).findAny();
	}

	/**
	 * Gets the count predicate.
	 *
	 * @return the count predicate
	 */
	public IntPredicate getCountPredicate() {
		return countPredicate;
	}

	public String getConfigNameValue() {
		return configNameValue;
	}
	
	protected String getThresholdConfigKey() {
		return thresholdConfigKey;
	}

	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}
	
}
