package org.mosip.registration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mosip.kernel.core.utils.JsonUtil;
import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.kernel.core.utils.exception.MosipJsonMappingException;
import org.mosip.kernel.core.utils.exception.MosipJsonParseException;
import org.mosip.registration.constants.RegConstants;
import org.mosip.registration.dto.ErrorResponseDTO;
import org.mosip.registration.dto.OtpGeneratorRequestDto;
import org.mosip.registration.dto.OtpGeneratorResponseDto;
import org.mosip.registration.dto.OtpValidatorResponseDto;
import org.mosip.registration.dto.RegistrationCenterDetailDTO;
import org.mosip.registration.dto.ResponseDTO;
import org.mosip.registration.dto.SuccessResponseDTO;
import org.mosip.registration.entity.RegistrationAppLoginMethod;
import org.mosip.registration.entity.RegistrationCenter;
import org.mosip.registration.entity.RegistrationUserDetail;
import org.mosip.registration.entity.RegistrationUserPassword;
import org.mosip.registration.entity.RegistrationUserPasswordID;
import org.mosip.registration.entity.RegistrationUserRole;
import org.mosip.registration.entity.RegistrationUserRoleID;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.repositories.RegistrationAppLoginRepository;
import org.mosip.registration.repositories.RegistrationCenterRepository;
import org.mosip.registration.repositories.RegistrationUserDetailRepository;
import org.mosip.registration.repositories.RegistrationUserPasswordRepository;
import org.mosip.registration.repositories.RegistrationUserRoleRepository;
import org.mosip.registration.util.restclient.ServiceDelegateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.val;

@Service
public class LoginServiceImpl implements LoginService {

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;
	@Autowired
	private RegistrationAppLoginRepository registrationAppLoginRepository;
	@Autowired
	private RegistrationUserPasswordRepository registrationUserPasswordRepository;
	@Autowired
	private RegistrationUserDetailRepository registrationUserDetailRepository;
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;
	@Autowired
	private RegistrationUserRoleRepository registrationUserRoleRepository;

	@Override
	public Map<String, Object> getModesOfLogin() {
		List<RegistrationAppLoginMethod> loginList = registrationAppLoginRepository
				.findByIsActiveTrueOrderByMethodSeq();

		Map<String, Object> loginModes = new LinkedHashMap<>();

		for (RegistrationAppLoginMethod registrationAppLoginMethod : loginList) {
			loginModes.put(String.valueOf(registrationAppLoginMethod.getMethodSeq()),
					registrationAppLoginMethod.getPk_applm_usr_id().getLoginMethod());
		}
		return loginModes;
	}

	@Override
	public boolean validateUserPassword(String userId, String hashPassword) {
		RegistrationUserPasswordID registrationUserPasswordID = new RegistrationUserPasswordID();
		registrationUserPasswordID.setUsrId(userId);
		registrationUserPasswordID.setPwd(hashPassword);
		List<RegistrationUserPassword> registrationUserPassword = registrationUserPasswordRepository
				.findByRegistrationUserPasswordID(registrationUserPasswordID);

		return (hashPassword.equals(registrationUserPassword.get(0).getRegistrationUserPasswordID().getPwd()));
		
	}

	@Override
	public Map<String, String> getUserDetail(String userId) {
		List<RegistrationUserDetail> registrationUserDetail = registrationUserDetailRepository
				.findByIdAndIsActiveTrue(userId);
		LinkedHashMap<String, String> userDetails = new LinkedHashMap<>();
		if (!registrationUserDetail.isEmpty()) {
			userDetails.put("name", registrationUserDetail.get(0).getName());
			userDetails.put("centerId", registrationUserDetail.get(0).getCntrId());
		}
		return userDetails;
	}

	@Override
	public String getCenterName(String centerId) {
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId);
		return registrationCenter.isPresent() ? registrationCenter.get().getName() : RegConstants.EMPTY;
	}

	@Override
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId) {
		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository.findById(centerId);
		
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		if (registrationCenter.isPresent()) {
			registrationCenterDetailDTO.setRegistrationCenterCode(registrationCenter.get().getName());
			registrationCenterDetailDTO.setAddrLine1(registrationCenter.get().getAddrLine1());
			registrationCenterDetailDTO.setAddrLine2(registrationCenter.get().getAddrLine2());
			registrationCenterDetailDTO.setAddrLine3(registrationCenter.get().getAddrLine3());
			registrationCenterDetailDTO.setLocLine1(registrationCenter.get().getLocLine1());
			registrationCenterDetailDTO.setLocLine2(registrationCenter.get().getLocLine2());
			registrationCenterDetailDTO.setLocLine3(registrationCenter.get().getLocLine3());
			registrationCenterDetailDTO.setLocLine4(registrationCenter.get().getLocLine4());
			registrationCenterDetailDTO.setCountry(registrationCenter.get().getCountry());
			registrationCenterDetailDTO.setLatitude(registrationCenter.get().getLatitude());
			registrationCenterDetailDTO.setLongitude(registrationCenter.get().getLongitude());
			registrationCenterDetailDTO.setPincode(registrationCenter.get().getPincode());
		}
		return registrationCenterDetailDTO;
	}

	@Override
	public List<String> getRoles(String userId) {
		RegistrationUserRoleID registrationUserRoleID = new RegistrationUserRoleID();
		registrationUserRoleID.setUsrId(userId);
		List<RegistrationUserRole> registrationUserRoles = registrationUserRoleRepository
				.findByRegistrationUserRoleID(registrationUserRoleID);
		List<String> roles = new ArrayList<String>();
		for (int role = 0; role < registrationUserRoles.size(); role++) {
			roles.add(registrationUserRoles.get(role).getRegistrationUserRoleID().getRoleCode());
		}
		return roles;
	}

	@Override
	public ResponseDTO getOTP(final String key) {

		ResponseDTO responseDTO = new ResponseDTO();
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		OtpGeneratorResponseDto otpGeneratorResponseDto = new OtpGeneratorResponseDto();

		SuccessResponseDTO successResponseDTO = null;

		otpGeneratorRequestDto.setKey(key);

		try {
			try {
				otpGeneratorResponseDto = (OtpGeneratorResponseDto) serviceDelegateUtil
						.post(RegConstants.OTP_GENERATOR_SERVICE_NAME, otpGeneratorRequestDto);
			} catch (RegBaseCheckedException e) {
				responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
			}
		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				otpGeneratorResponseDto = (OtpGeneratorResponseDto) JsonUtil.jsonStringToJavaObject(
						OtpGeneratorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
			} catch (MosipJsonParseException  | MosipJsonMappingException | MosipIOException exception) {
				//TODO: Need to replace with log error
			}
			responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_GENERATION_ERROR_MESSAGE);

		}

		if (otpGeneratorResponseDto != null) {
			successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegConstants.ALERT_INFORMATION);
			successResponseDTO
					.setMessage(RegConstants.OTP_GENERATION_SUCCESS_MESSAGE + otpGeneratorResponseDto.getOtp());

			Map<String, Object> otherAttributes = new HashMap<String, Object>();
			otherAttributes.put(RegConstants.OTP_GENERATOR_RESPONSE_DTO, otpGeneratorResponseDto);

			successResponseDTO.setOtherAttributes(otherAttributes);
			responseDTO.setSuccessResponseDTO(successResponseDTO);

		} else {
			responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_GENERATION_ERROR_MESSAGE);
		}

		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.service.login.LoginService#validateOTP(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO validateOTP(final String key, final String otp) {

		// Create Response
		ResponseDTO responseDTO = new ResponseDTO();
		SuccessResponseDTO successResponseDTO;
		OtpValidatorResponseDto otpValidatorResponseDto = null;

		// Validator response service api creation
		final String SERVICE_NAME = RegConstants.OTP_VALIDATOR_SERVICE_NAME;
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegConstants.USERNAME_KEY, key);
		requestParamMap.put(RegConstants.OTP_GENERATED, otp);

		try {
			try {
				otpValidatorResponseDto = (OtpValidatorResponseDto) serviceDelegateUtil.get(SERVICE_NAME,
						requestParamMap);
			} catch (RegBaseCheckedException e) {
				responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);

			}
		} catch (HttpClientErrorException httpClientErrorException) {
			try {
				otpValidatorResponseDto = (OtpValidatorResponseDto) JsonUtil.jsonStringToJavaObject(
						OtpValidatorResponseDto.class, httpClientErrorException.getResponseBodyAsString());
			} catch (MosipJsonParseException mosipJsonParseException) {
				mosipJsonParseException.printStackTrace();
			} catch (MosipJsonMappingException mosipJsonMappingException) {
				// TODO Auto-generated catch block
				mosipJsonMappingException.printStackTrace();
			} catch (MosipIOException mosipIOException) {
				// TODO Auto-generated catch block
				mosipIOException.printStackTrace();
			}
			responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);

		}

		if (otpValidatorResponseDto != null && otpValidatorResponseDto.getStatus().equalsIgnoreCase("true")) {

			// Create Success Response
			successResponseDTO = new SuccessResponseDTO();
			successResponseDTO.setCode(RegConstants.ALERT_INFORMATION);
			successResponseDTO.setMessage(RegConstants.OTP_VALIDATION_SUCCESS_MESSAGE);
			Map<String, Object> otherAttributes = new HashMap<String, Object>();
			otherAttributes.put(RegConstants.OTP_VALIDATOR_RESPONSE_DTO, otpValidatorResponseDto);
			successResponseDTO.setOtherAttributes(otherAttributes);
			responseDTO.setSuccessResponseDTO(successResponseDTO);
		} else {

			responseDTO = getErrorResponse(responseDTO, RegConstants.OTP_VALIDATION_ERROR_MESSAGE);

		}

		return responseDTO;

	}

	/**
	 * creation of Error Response
	 * 
	 * @param responseDTO
	 *            response need to be created
	 * @param message
	 *            message to be stored in the response
	 * @return Response response
	 */
	private ResponseDTO getErrorResponse(ResponseDTO responseDTO, final String message) {
		// Create list of Error Response
		LinkedList<ErrorResponseDTO> errorResponseDTOs = new LinkedList<ErrorResponseDTO>();

		// Error response
		ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();

		errorResponseDTO.setCode(RegConstants.ALERT_ERROR);
		errorResponseDTO.setMessage(message);
		Map<String, Object> otherAttributes = new HashMap<String, Object>();
		otherAttributes.put(RegConstants.OTP_VALIDATOR_RESPONSE_DTO, null);

		errorResponseDTOs.add(errorResponseDTO);

		// Assing list of error responses to response
		responseDTO.setErrorResponseDTOs(errorResponseDTOs);
		return responseDTO;

	}
}
