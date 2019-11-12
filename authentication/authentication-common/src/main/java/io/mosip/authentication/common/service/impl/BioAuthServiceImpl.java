package io.mosip.authentication.common.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
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
 * 
 * Bio-service to implement Biometric Authentication
 * 
 * @author Dinesh Karuppiah.T
 * @author Manoj SP
 */

@Service
public class BioAuthServiceImpl implements BioAuthService {

	/**
	 * Id Info helper
	 */
	@Autowired
	private IdInfoHelper idInfoHelper;

	/**
	 * Match Input Builder
	 */
	@Autowired
	private MatchInputBuilder matchInputBuilder;

	/**
	 * The Ida Mapping Config
	 */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Autowired
	private RestHelper restHelper;

	@Autowired
	private RestRequestFactory restBuilder;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * Validate Bio Auth details based on Bio auth request and Biometric Identity
	 * values
	 */
	@Override
	public AuthStatusInfo authenticate(AuthRequestDTO authRequestDTO, String uin,
			Map<String, List<IdentityInfoDTO>> bioIdentity, String partnerId) throws IdAuthenticationBusinessException {
		if (bioIdentity == null || bioIdentity.isEmpty()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR);
		} else {
			verifyBiometricDevice(authRequestDTO.getRequest().getBiometrics());
			List<MatchInput> listMatchInputs = constructMatchInput(authRequestDTO);
			List<MatchOutput> listMatchOutputs = getMatchOutput(listMatchInputs, authRequestDTO, bioIdentity,
					partnerId);
			// Using OR condition on the match output for Bio auth.
			boolean bioMatched = listMatchOutputs.stream().anyMatch(MatchOutput::isMatched);
			return AuthStatusInfoBuilder.buildStatusInfo(bioMatched, listMatchInputs, listMatchOutputs,
					BioAuthType.values(), idMappingConfig);
		}

	}

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
					if (e.getResponseBodyAsString().isPresent()) {
						try {
							ResponseWrapper response = mapper.readValue(e.getResponseBodyAsString().get(),
									ResponseWrapper.class);
							response.getErrors().forEach(error -> {
								if (((ServiceError) error).getErrorCode().equals("ADM-DPM-001")
										|| ((ServiceError) error).getErrorCode().equals("ADM-DPM-002")) {
									throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.INVALID_DEVICEID);
								} else if (((ServiceError) error).getErrorCode().equals("ADM-DPM-003")
										|| ((ServiceError) error).getErrorCode().equals("ADM-DPM-004")) {
									throw new IdAuthUncheckedException(
											IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
											String.format(
													IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER
															.getErrorMessage(),
													"request/biometrics/" + index + "/digitalId/deviceProvider"));

								} else if (((ServiceError) error).getErrorCode().equals("ADM-DPM-005")
										|| ((ServiceError) error).getErrorCode().equals("ADM-DPM-006")) {
									throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.INVALID_MDS);
								} else if (((ServiceError) error).getErrorCode().equals("ADM-DPM-007")
										|| ((ServiceError) error).getErrorCode().equals("ADM-DPM-008")) {
									throw new IdAuthUncheckedException(
											IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
											String.format(
													IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER
															.getErrorMessage(),
													"request/biometrics/" + index + "/deviceServiceVersion"));
								} else {
									throw new IdAuthUncheckedException(
											IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
								}
							});
						} catch (IOException ex) {
							throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, ex);
						}
					} else {
						throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
					}
				} catch (IDDataValidationException e) {
					throw new IdAuthUncheckedException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS);
				}
			});
		} catch (IdAuthUncheckedException e) {
			throw new IdAuthenticationBusinessException(e.getErrorCode(), e.getErrorText());
		}
		return true;
	}

	/**
	 * Constucts Match inputs based on Matched Bio Authtype and Bio Match type
	 * 
	 * @param authRequestDTO
	 * @return
	 */
	private List<MatchInput> constructMatchInput(AuthRequestDTO authRequestDTO) {
		return matchInputBuilder.buildMatchInput(authRequestDTO, BioAuthType.values(), BioMatchType.values());
	}

	private List<MatchOutput> getMatchOutput(List<MatchInput> listMatchInputs, AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> demoEntity, String partnerId) throws IdAuthenticationBusinessException {
		return idInfoHelper.matchIdentityData(authRequestDTO, demoEntity, listMatchInputs, partnerId);
	}

}
