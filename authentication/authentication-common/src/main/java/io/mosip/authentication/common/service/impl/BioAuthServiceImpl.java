package io.mosip.authentication.common.service.impl;

import static io.mosip.authentication.core.constant.IdAuthCommonConstants.BIO_PATH;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEVICE_DOES_NOT_EXIST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEVICE_PROVIDER_INACTIVE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEVICE_PROVIDER_NOT_EXIST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.DEVICE_REVOKED_OR_RETIRED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.FIELD_VALIDATION_FAILED;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MDS_DOES_NOT_EXIST;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.MDS_INACTIVE_STATE;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.SW_ID_VERIFICATION_FAILED;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.DEVICE_VERIFICATION_FAILED;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.MDS_VERIFICATION_FAILED;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.SERVER_ERROR;
import static io.mosip.authentication.core.constant.IdAuthenticationErrorConstants.UNABLE_TO_PROCESS;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.core.constant.RestServicesConstants;
import io.mosip.authentication.core.dto.DigitalIdDTO;
import io.mosip.authentication.core.dto.ValidateDeviceDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RestServiceException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.indauth.match.MatchInput;
import io.mosip.authentication.core.spi.indauth.match.MatchOutput;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * Bio-service to implement Biometric Authentication.
 *
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	/** Id Info helper. */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/** Match Input Builder. */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/** The Ida Mapping Config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The rest helper. */
	@Autowired
	private RestHelper restHelper;

	/** The rest builder. */
	@Autowired
	private RestRequestFactory restBuilder;

	/**
	 * Validate Bio Auth details based on Bio auth request and Biometric Identity
	 * values.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin the uin
	 * @param bioIdentity the bio identity
	 * @param partnerId the partner id
	 * @return the auth status info
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> bioIdentity, String partnerId, boolean isAuth) throws IdAuthenticationBusinessException {
		if (bioIdentity == null || bioIdentity.isEmpty()) {
			throw new IdAuthenticationBusinessException(SERVER_ERROR);
		} else {
			if (isAuth) {
				verifyBiometricDevice(authRequestDTO.getRequest().getBiometrics());
			}
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO, bioIdentity,
					partnerId);
			// Using OR condition on the match output for Bio auth.
			boolean bioMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
			return AuthStatusInfoBuilder.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs,
					BioAuthType.values(), idMappingConfig);
		}

	}

	/**
	 * Verify biometric device.
	 *
	 * @param bioRequest the bio request
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean verifyBiometricDevice(List<BioIdentityInfoDTO> bioRequest)
			throws IdAuthenticationBusinessException {
		try {
			IntStream.range(0, bioRequest.size()).forEach(index -> {
				try {
					DataDTO data = bioRequest.get(index).getData();
					ValidateDeviceDTO request = new ValidateDeviceDTO();
					DigitalId digitalId = data.getDigitalId();
					request.setDeviceCode(data.getDeviceCode());
					request.setDigitalId(new DigitalIdDTO(digitalId.getSerialNo(), digitalId.getMake(),
							digitalId.getModel(), digitalId.getType(), digitalId.getDeviceProvider(),
							digitalId.getDeviceProviderId(), digitalId.getDateTime()));
					request.setDeviceServiceVersion(data.getDeviceServiceVersion());
					RequestWrapper<ValidateDeviceDTO> requestWrapper = new RequestWrapper<>();
					requestWrapper.setRequest(request);
					restHelper.requestSync(restBuilder.buildRequest(RestServicesConstants.DEVICE_VERIFICATION_SERVICE,
							requestWrapper, ResponseWrapper.class));
				} catch (RestServiceException e) {
					if (e.getResponseBody().isPresent()) {
						ResponseWrapper response = (ResponseWrapper) e.getResponseBody().get();
						response.getErrors().forEach(error -> {
							ServiceError serviceError = (ServiceError) error;
							if (serviceError.getErrorCode().equals(DEVICE_DOES_NOT_EXIST)
									|| serviceError.getErrorCode().equals(DEVICE_REVOKED_OR_RETIRED)
									|| serviceError.getErrorCode().equals(DEVICE_PROVIDER_NOT_EXIST)
									|| serviceError.getErrorCode().equals(DEVICE_PROVIDER_INACTIVE)) {
								throw new IdAuthUncheckedException(DEVICE_VERIFICATION_FAILED);
							} else if (serviceError.getErrorCode().equals(MDS_DOES_NOT_EXIST)
									|| serviceError.getErrorCode().equals(MDS_INACTIVE_STATE)
									|| serviceError.getErrorCode().equals(SW_ID_VERIFICATION_FAILED)) {
								throw new IdAuthUncheckedException(MDS_VERIFICATION_FAILED);
							} else if (serviceError.getErrorCode().equals(FIELD_VALIDATION_FAILED)) {
								throw new IdAuthUncheckedException(INVALID_INPUT_PARAMETER.getErrorCode(),
										String.format(INVALID_INPUT_PARAMETER.getErrorMessage(),
												String.format(BIO_PATH, index, "digitalId/" + serviceError.getMessage()
														.substring(serviceError.getMessage().lastIndexOf(' ') + 1))));
							} else {
								throw new IdAuthUncheckedException(UNABLE_TO_PROCESS);
							}
						});
					} else {
						throw new IdAuthUncheckedException(UNABLE_TO_PROCESS);
					}
				} catch (IDDataValidationException e) {
					throw new IdAuthUncheckedException(UNABLE_TO_PROCESS, e);
				}
			});
		} catch (IdAuthUncheckedException e) {
			throw new IdAuthenticationBusinessException(e.getErrorCode(), e.getErrorText());
		}
		return true;
	}

	/**
	 * Constucts Match inputs based on Matched Bio Authtype and Bio Match type.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the list
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values());
	}

	/**
	 * Gets the match output.
	 *
	 * @param listMatchInputs the list match inputs
	 * @param authRequestDTO the auth request DTO
	 * @param demoEntity the demo entity
	 * @param partnerId the partner id
	 * @return the match output
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoEntity, String partnerId) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, demoEntity, listMatchInputs, partnerId);
	}

}
