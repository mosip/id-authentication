package io.mosip.authentication.common.service.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoAuthType;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdMapping;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;

/**
 * The builder class of AuthStatusInfo.
 *
 * @author Loganathan Sekar
 */

public class AuthStatusInfoBuilder {

	private static final String ADDRESS_LINE_ITEMS = "address line item(s)";

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
		boolean multiLanguage = matchOutput.getMatchType().isMultiLanguage();

		Optional<AuthType> authTypeForMatchType;
		AuthType[] authTypes;
		authTypes = DemoAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);
		if (authTypeForMatchType.isPresent()) {
			AuthError errors = null;
			List<String> mappings = IdaIdMapping.FULLADDRESS.getMappingFunction().apply(idMappingConfig,
					matchOutput.getMatchType());
			IdMapping idMapping = matchOutput.getMatchType().getIdMapping();
			String name = mappings.contains(idMapping.getIdname()) ? ADDRESS_LINE_ITEMS : idMapping.getIdname();

			if (!multiLanguage) {
				errors = createActionableAuthError(IdAuthenticationErrorConstants.DEMO_DATA_MISMATCH, name);
			} else {
				errors = createActionableAuthError(IdAuthenticationErrorConstants.DEMOGRAPHIC_DATA_MISMATCH_LANG, name,
						matchOutput.getLanguage());
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
			AuthError errors = createActionableAuthError(IdAuthenticationErrorConstants.INVALID_OTP, "");
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
			if (authType.getDisplayName().equals(PinAuthType.SPIN.getDisplayName())) {
				AuthError errors = createActionableAuthError(IdAuthenticationErrorConstants.PIN_MISMATCH, "");
				statusInfoBuilder.addErrors(errors);
			}
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
		AuthType[] authTypes;
		authTypes = BioAuthType.values();
		authTypeForMatchType = AuthType.getAuthTypeForMatchType(matchOutput.getMatchType(), authTypes);
		if (authTypeForMatchType.isPresent()) {
			AuthError errors = createActionableAuthError(IdAuthenticationErrorConstants.BIO_MISMATCH,
					authTypeForMatchType.get().getType());
			statusInfoBuilder.addErrors(errors);
		}
	}

	/**
	 * Construct Actionable Auth errors.
	 * 
	 * @param idAuthenticationErrorConstants
	 * @param paramName
	 * @return
	 */
	private static AuthError createActionableAuthError(IdAuthenticationErrorConstants idAuthenticationErrorConstants,
			Object... params) {
		return new ActionableAuthError(idAuthenticationErrorConstants.getErrorCode(),
				String.format(idAuthenticationErrorConstants.getErrorMessage(), params),
				idAuthenticationErrorConstants.getActionMessage() != null
						? String.format(idAuthenticationErrorConstants.getActionMessage(), params)
						: null);
	}

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
