package io.mosip.authentication.service.impl.indauth.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.indauth.match.IdaIdMapping;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.pin.PinAuthType;

/**
 * The builder class of AuthStatusInfo.
 *
 * @author Loganathan Sekar
 */

public class AuthStatusInfoBuilder {

	/** The built flag. */
	private boolean built;

	/** The auth status info. */
	private AuthStatusInfo authStatusInfo;

	/**
	 * Instantiates a new AuthStatusInfoBuilder.
	 */
	private AuthStatusInfoBuilder() {
		authStatusInfo = new AuthStatusInfo();
	}

	/**
	 * Gets new instance of AuthStatusInfo.
	 *
	 * @return the auth status info builder
	 */
	public static AuthStatusInfoBuilder newInstance() {
		return new AuthStatusInfoBuilder();
	}

	/**
	 * Sets the status.
	 *
	 * @param status the status
	 * @return the auth status info builder
	 */
	public AuthStatusInfoBuilder setStatus(boolean status) {
		assertNotBuilt();
		authStatusInfo.setStatus(status);
		return this;
	}

	/**
	 * Builds the status info.
	 *
	 * @param demoMatched      the demo matched
	 * @param listMatchInputs  the list match inputs
	 * @param listMatchOutputs the list match outputs
	 * @param authTypes        the auth types
	 * @return the auth status info
	 */
	public static AuthStatusInfo buildStatusInfo(boolean demoMatched, List<MatchInput> listMatchInputs,
			List<MatchOutput> listMatchOutputs, AuthType[] authTypes, IDAMappingConfig idMappingConfig) {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		statusInfoBuilder.setStatus(demoMatched);
		prepareErrorList(listMatchOutputs, statusInfoBuilder, idMappingConfig);
		return statusInfoBuilder.build();
	}

	/**
	 * Builds the usage data bits.
	 *
	 * @param listMatchOutputs  the list match outputs
	 * @param statusInfoBuilder the status info builder
	 */
	/**
	 * prepares the list of errors if the authentication status got failed
	 *
	 * @param listMatchOutputs  the list match outputs
	 * @param statusInfoBuilder the status info builder
	 */
	private static void prepareErrorList(List<MatchOutput> listMatchOutputs, AuthStatusInfoBuilder statusInfoBuilder,
			IDAMappingConfig idaMappingConfig) {
		listMatchOutputs.forEach((MatchOutput matchOutput) -> {
			if (!matchOutput.isMatched()) {
				prepareErrorList(matchOutput, statusInfoBuilder, idaMappingConfig);
			}
		});
	}

	/**
	 * @param matchOutput
	 * @param statusInfoBuilder
	 */
	private static void prepareErrorList(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder,
			IDAMappingConfig idMappingConfig) {

		if (matchOutput != null && !matchOutput.isMatched()) {
			String category = matchOutput.getMatchType().getCategory().getType();
			if (category.equalsIgnoreCase(Category.BIO.getType())) {
				constructBioError(matchOutput, statusInfoBuilder);
			} else if (category.equalsIgnoreCase(Category.SPIN.getType())) {
				constructPinError(matchOutput, statusInfoBuilder);
			} else if (category.equalsIgnoreCase(Category.DEMO.getType())) {
				constructDemoError(matchOutput, statusInfoBuilder, idMappingConfig);
			} else if (category.equalsIgnoreCase(Category.OTP.getType())) {
				constructOTPError(matchOutput, statusInfoBuilder);
			}
		}
	}

	private static void constructDemoError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder,
			IDAMappingConfig idMappingConfig) {
		Optional<AuthType> authTypeForMatchType;
		AuthType[] authTypes;
		authTypes = DemoAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);
		if (authTypeForMatchType.isPresent()) {
			AuthError errors = null;
			List<String> mappings = IdaIdMapping.FULLADDRESS.getMappingFunction().apply(idMappingConfig,
					matchOutput.getMatchType());
			IdMapping idMapping = matchOutput.getMatchType().getIdMapping();
			String name = mappings.contains(idMapping.getIdname()) ? "address line item(s)" : idMapping.getIdname();

			if (name.equalsIgnoreCase(IdaIdMapping.PHONE.getIdname())
					|| name.equalsIgnoreCase(IdaIdMapping.EMAIL.getIdname())
					|| name.equalsIgnoreCase(IdaIdMapping.DOB.getIdname())
					|| name.equalsIgnoreCase(IdaIdMapping.DOBTYPE.getIdname())
					|| name.equalsIgnoreCase(IdaIdMapping.AGE.getIdname())) {
				errors = new AuthError(IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH.getErrorMessage(), name));
			} else {
				errors = new AuthError(IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH.getErrorMessage(), name,
								matchOutput.getLanguage()));
			}

			statusInfoBuilder.addErrors(errors);
		}
	}

	private static void constructOTPError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {
		Optional<AuthType> authTypeForMatchType;
		AuthType[] authTypes;
		authTypes = PinAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);

		if (authTypeForMatchType.isPresent()) {
			AuthError errors = new AuthError(IdAuthenticationErrorConstants.INVALID_OTP.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_OTP.getErrorMessage());
			statusInfoBuilder.addErrors(errors);
		}
	}

	/**
	 * Construct pin error.
	 *
	 * @param matchOutput       the match output
	 * @param statusInfoBuilder the status info builder
	 */
	private static void constructPinError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {
		Optional<AuthType> authTypeForMatchType;
		AuthType authType;
		AuthType[] authTypes;
		authTypes = PinAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);

		if (authTypeForMatchType.isPresent()) {
			authType = authTypeForMatchType.get();
			AuthError errors = null;

			if (authType.getDisplayName().equals(PinAuthType.SPIN.getDisplayName())) {
				errors = new AuthError(IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorCode(),
						IdAuthenticationErrorConstants.PIN_MISMATCH.getErrorMessage());
			}
			statusInfoBuilder.addErrors(errors);
		}
	}

	/**
	 * Construct bio error.
	 *
	 * @param matchOutput       the match output
	 * @param statusInfoBuilder the status info builder
	 */
	private static void constructBioError(MatchOutput matchOutput, AuthStatusInfoBuilder statusInfoBuilder) {
		Optional<AuthType> authTypeForMatchType;
		AuthType authType;
		AuthType[] authTypes;
		authTypes = BioAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);

		if (authTypeForMatchType.isPresent()) {
			authType = authTypeForMatchType.get();
			AuthError errors = null;
			IdMapping idMapping = matchOutput.getMatchType().getIdMapping();
			errors = new AuthError(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorCode(), String
					.format(IdAuthenticationErrorConstants.BIO_MISMATCH.getErrorMessage(), idMapping.getIdname()));
			statusInfoBuilder.addErrors(errors);
		}
	}

	/**
	 * Adds the match info to AuthStatusInfo.
	 *
	 * @param authType          the auth type
	 * @param matchingStrategy  the matching strategy
	 * @param matchingThreshold the mt
	 * @param language          the language
	 * @return the auth status info builder
	 */
	/*
	 * public AuthStatusInfoBuilder addMatchInfo(String authType, String
	 * matchingStrategy, Integer matchingThreshold, String language) {
	 * assertNotBuilt(); if (authStatusInfo.getMatchInfos() == null) {
	 * authStatusInfo.setMatchInfos(new ArrayList<>()); }
	 * authStatusInfo.getMatchInfos().add(new MatchInfo(authType, language,
	 * matchingStrategy, matchingThreshold)); return this; }
	 */

	/**
	 * Adds the bio info to AuthStatusInfo.
	 *
	 * @param bioType    the bio type
	 * @param deviceInfo the device info
	 * @return the auth status info builder
	 */
	/*
	 * public AuthStatusInfoBuilder addBioInfo(String bioType,DeviceInfo deviceInfo)
	 * { assertNotBuilt(); if (authStatusInfo.getBioInfos() == null) {
	 * authStatusInfo.setBioInfos(new ArrayList<>()); }
	 * authStatusInfo.getBioInfos().add(new BioInfo(bioType,deviceInfo)); return
	 * this; }
	 */

	/**
	 * Adds the auth usage data bits to AuthStatusInfo.
	 *
	 * @param usageDataBits the usage data bits
	 * @return the auth status info builder
	 */
	/*
	 * public AuthStatusInfoBuilder addAuthUsageDataBits(AuthUsageDataBit...
	 * usageDataBits) { assertNotBuilt(); if (authStatusInfo.getUsageDataBits() ==
	 * null) { authStatusInfo.setUsageDataBits(new ArrayList<>()); }
	 * 
	 * authStatusInfo.getUsageDataBits().addAll(Arrays.asList(usageDataBits));
	 * return this; }
	 */

	/**
	 * Adds the errors to the AuthStatusInfo.
	 *
	 * @param errors the errors
	 * @return the auth status info builder
	 */
	public AuthStatusInfoBuilder addErrors(AuthError... errors) {
		assertNotBuilt();
		if (authStatusInfo.getErr() == null) {
			authStatusInfo.setErr(new ArrayList<>());
		}

		authStatusInfo.getErr().addAll(Arrays.asList(errors));
		return this;
	}

	/**
	 * Builds the AuthStatusInfo.
	 *
	 * @return the AuthStatusInfo instance
	 */
	public AuthStatusInfo build() {
		assertNotBuilt();
		built = true;
		return authStatusInfo;
	}

	/**
	 * Assert that AuthStatusInfo is not built.
	 */
	private void assertNotBuilt() {
		if (built) {
			throw new IllegalStateException();
		}
	}
}
