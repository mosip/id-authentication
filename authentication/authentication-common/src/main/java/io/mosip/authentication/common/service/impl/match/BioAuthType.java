package io.mosip.authentication.common.service.impl.match;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.stream.Stream;

import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.impl.AuthTypeImpl;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.BiFunctionWithBusinessException;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.match.MatchingStrategyType;
import io.mosip.kernel.core.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;

/**
 * The Enum BioAuthType.
 *
 * @author Dinesh Karuppiah.T
 */

public enum BioAuthType implements AuthType {

	FGR_MIN("FMR",
			AuthType.setOf(BioMatchType.FGRMIN_LEFT_THUMB, BioMatchType.FGRMIN_LEFT_INDEX,
					BioMatchType.FGRMIN_LEFT_MIDDLE, BioMatchType.FGRMIN_LEFT_RING, BioMatchType.FGRMIN_LEFT_LITTLE,
					BioMatchType.FGRMIN_RIGHT_THUMB, BioMatchType.FGRMIN_RIGHT_INDEX, BioMatchType.FGRMIN_RIGHT_MIDDLE,
					BioMatchType.FGRMIN_RIGHT_RING, BioMatchType.FGRMIN_RIGHT_LITTLE, BioMatchType.FGRMIN_UNKNOWN),
			getFingerprint(), count -> count == 1, "bio-FMR") {
		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE);
					});
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
			getFingerprint(), value -> value == 1, "bio-FIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(BioAuthType.class.getSimpleName(), this);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER);
					});
			return valueMap;
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRIMG_COMPOSITE);
		}
	},
	FGR_MIN_COMPOSITE("FMR", AuthType.setOf(BioMatchType.FGRMIN_COMPOSITE), getFingerprint(), value -> value == 2,
			"bio-FIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchMultiValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE);
					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRMIN_COMPOSITE);
		}
	},

	FGR_IMG_COMPOSITE("FIR", AuthType.setOf(BioMatchType.FGRIMG_COMPOSITE), getFingerprint(), value -> value == 2,
			"bio-FIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchMultiValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER);
					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRIMG_COMPOSITE);
		}
	},

	FGR_MIN_MULTI("FMR", AuthType.setOf(BioMatchType.FGRMIN_MULTI), getFingerprint(),
			value -> value >= 3 && value <= 10, "bio-FMR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchMultiValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER);
					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(MULTI_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRMIN_COMPOSITE);
		}
	},

	FGR_IMG_MULTI("FIR", AuthType.setOf(BioMatchType.FGRIMG_MULTI), getFingerprint(),
			value -> value >= 3 && value <= 10, "bio-FIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchMultiValue;
						valueMap.put(IdaIdMapping.FINGERPRINT.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.FINGER);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FINGER);
					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(MULTI_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getFPValuesCountInIdentity(reqDTO, helper, BioMatchType.FGRIMG_MULTI);
		}
	},

	IRIS_COMP_IMG("IIR", AuthType.setOf(BioMatchType.IRIS_COMP), "Iris", value -> value == 2, "bio-IIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchMultiValue;
						valueMap.put(IdaIdMapping.IRIS.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.IRIS);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_IRIS);
					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(COMPOSITE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}

	},
	IRIS_IMG("IIR", AuthType.setOf(BioMatchType.RIGHT_IRIS, BioMatchType.LEFT_IRIS, BioMatchType.IRIS_UNKNOWN), "Iris",
			value -> value == 1, "bio-IIR") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchValue;
						valueMap.put(IdaIdMapping.IRIS.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.IRIS);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_IRIS);

					});
			return valueMap;
		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(SINGLE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			return BioAuthType.getIrisValuesCountInIdentity(reqDTO, helper);
		}
	},
	FACE_IMG("FID", AuthType.setOf(BioMatchType.FACE, BioMatchType.FACE_UNKNOWN), "face", value -> value == 1,
			"bio-FID") {

		@Override
		public Map<String, Object> getMatchProperties(AuthRequestDTO authRequestDTO, IdInfoFetcher idInfoFetcher,
				String language) {
			Map<String, Object> valueMap = new HashMap<>();
			authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
					.filter(bioinfo -> bioinfo.getBioType().equals(this.getType())).forEach((DataDTO bioinfovalue) -> {
						BiFunctionWithBusinessException<Map<String, String>, Map<String, String>, Double> func = idInfoFetcher
								.getBioMatcherUtil()::matchValue;
						valueMap.put(IdaIdMapping.FACE.getIdname(), func);
						valueMap.put(SingleType.class.getName(), SingleType.FACE);
						valueMap.put(CbeffConstant.class.getName(), CbeffConstant.FORMAT_TYPE_FACE);
					});
			return valueMap;

		}

		@Override
		public Optional<Integer> getMatchingThreshold(AuthRequestDTO authReq, String languageInfoFetcher,
				Environment environment, IdInfoFetcher idInfoFetcher) {
			return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(SINGLE_THRESHOLD));
		}

		@Override
		protected Long getBioIdentityValuesCount(AuthRequestDTO reqDTO, IdInfoFetcher helper) {
			long entries = 0;
			for (MatchType matchType : AuthType.setOf(BioMatchType.FACE, BioMatchType.FACE_UNKNOWN)) {
				entries += (long) helper.getIdentityRequestInfo(matchType, reqDTO.getRequest(), null).size();
			}
			return entries;

		}
	};

	/** The Constant SINGLE_THRESHOLD. */
	private static final String SINGLE_THRESHOLD = ".single.threshold";

	/** The Constant COMPOSITE_THRESHOLD. */
	private static final String COMPOSITE_THRESHOLD = ".composite.threshold";

	/** The Constant MULTI_THRESHOLD. */
	private static final String MULTI_THRESHOLD = ".multi.threshold";

	/** The Constant FINGERPRINT. */
	private static final String FINGERPRINT = "Fingerprint";

	private AuthTypeImpl authTypeImpl;

	private IntPredicate countPredicate;

	private String configKey;

	/**
	 * Instantiates a new bio auth type.
	 *
	 * @param type                 the type
	 * @param associatedMatchTypes the associated match types
	 * @param displayName          the display name
	 * @param count                the count
	 */
	private BioAuthType(String type, Set<MatchType> associatedMatchTypes, String displayName,
			IntPredicate countPredicate, String configKey) {
		authTypeImpl = new AuthTypeImpl(type, associatedMatchTypes, displayName);
		this.countPredicate = countPredicate;
		this.configKey = configKey;
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
				&& countPredicate.test(getBioIdentityValuesCount(authReq, helper).intValue());
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
		return idInfoFetcher.getMatchingThreshold(getType().toLowerCase().concat(SINGLE_THRESHOLD));
	}

	/*
	 * Checks is Authtype information available based on authreqest
	 */
	@Override
	public boolean isAuthTypeInfoAvailable(AuthRequestDTO authRequestDTO) {
		return Optional.ofNullable(authRequestDTO.getRequest().getBiometrics())
				.flatMap(list -> list.stream().map(BioIdentityInfoDTO::getData)
						.filter(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(getType())).findAny())
				.isPresent();
	}

	public static String getFingerprint() {
		return FINGERPRINT;
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

	public static Optional<String> getTypeForConfigKey(String configKey) {
		BioAuthType[] values = BioAuthType.values();
		return Stream.of(values).filter(authtype -> authtype.getConfigKey().equalsIgnoreCase(configKey))
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

	public String getConfigKey() {
		return configKey;
	}

	@Override
	public AuthType getAuthTypeImpl() {
		return authTypeImpl;
	}

}
