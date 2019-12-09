package io.mosip.resident.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.constant.NotificationTemplateCode;
import io.mosip.resident.constant.RegistrationExternalStatusCode;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.AuthLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.RegProcRePrintRequestDto;
import io.mosip.resident.dto.RegProcRePrintResponseDto;
import io.mosip.resident.dto.RegStatusCheckResponseDTO;
import io.mosip.resident.dto.RegistrationStatusDTO;
import io.mosip.resident.dto.RegistrationStatusRequestDTO;
import io.mosip.resident.dto.RegistrationStatusSubRequestDto;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResidentReprintResponseDto;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.RIDInvalidException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import io.mosip.resident.util.UINCardDownloadService;


@Service
public class ResidentServiceImpl implements ResidentService {

	private static final String DATETIME_PATTERN = "mosip.utc-datetime-pattern";
	private static final String STATUS_CHECK_ID = "mosip.resident.service.status.check.id";
	private static final String STATUS_CHECEK_VERSION = "mosip.resident.service.status.check.version";
	private static final int RID_LENGTH = 29;
	private static final String REJECTED_MESSAGE = "REJECTED - PLEASE VISIT THE NEAREST CENTER FOR DETAILS.";
	private static final String REREGISTER_MESSAGE = "FAILED - PLEASE VISIT THE NEAREST CENTER FOR DETAILS.";
	private static final String RESEND_MESSAGE = "UNDER PROCESSING - PLEASE CHECK BACK AGAIN LATER.";
	private static final String PROCESSING_MESSAGE = "UNDER PROCESSING - PLEASE CHECK BACK AGAIN LATER.";

	private static final Logger logger = LoggerConfiguration.logConfig(ResidentServiceImpl.class);

	@Autowired
	private VidValidator<String> vidValidator;

	@Autowired
	private UinValidator<String> uinValidator;

	@Autowired
	private RidValidator<String> ridValidator;

	@Autowired
	private UINCardDownloadService uinCardDownloadService;

	@Autowired
	private IdAuthService idAuthService;

	@Autowired
	NotificationService notificationService;
	
	@Autowired
	private TokenGenerator tokenGenerator;

	@Autowired
	private ResidentServiceRestClient residentServiceRestClient;
	
	@Autowired
	Environment env;
	
	@Value("${resident.center.id}")
	private String centerId;

	@Value("${resident.machine.id}")
	private String machineId;
	/************** to fetch UIN status for particular RID ******************/

	@Override
	public RegStatusCheckResponseDTO getRidStatus(RequestDTO request) throws ApisResourceAccessException {
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::getRidStatus():: entry");
		validateRID(request.getIndividualId());

		RegStatusCheckResponseDTO response = null;
		ResponseWrapper<RegistrationStatusDTO> responseWrapper =null;

		RegistrationStatusRequestDTO dto = new RegistrationStatusRequestDTO();
		List<RegistrationStatusSubRequestDto> rids = new ArrayList<RegistrationStatusSubRequestDto>();
		RegistrationStatusSubRequestDto rid = new RegistrationStatusSubRequestDto(request.getIndividualId());

		rids.add(rid);
		dto.setRequest(rids);
		dto.setId(env.getProperty(STATUS_CHECK_ID));
		dto.setVersion(env.getProperty(STATUS_CHECEK_VERSION));
		dto.setRequesttime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));

		try {
			responseWrapper =(ResponseWrapper) residentServiceRestClient
                    .postApi(env.getProperty(ApiName.REGISTRATIONSTATUSSEARCH.name()), MediaType.APPLICATION_JSON, dto, ResponseWrapper.class, tokenGenerator.getToken());
		} catch (Exception e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(), "", e.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new ApisResourceAccessException(e.getMessage(), e);
		}
		response = new RegStatusCheckResponseDTO();

		if (responseWrapper.getResponse() == null) {
			if (responseWrapper.getErrors() == null) {
				throw new RIDInvalidException(ResidentErrorCode.NO_RID_FOUND_EXCEPTION.getErrorCode(),
						ResidentErrorCode.NO_RID_FOUND_EXCEPTION.getErrorMessage());
			}
			throw new ResidentServiceException(responseWrapper.getErrors().get(0).getErrorCode(),
					responseWrapper.getErrors().get(0).getMessage());
		} else {
			List<LinkedHashMap<String,String>> statusResponse = (List<LinkedHashMap<String,String>>) responseWrapper.getResponse();

			String statusCode = validateResponse(statusResponse.get(0).get("statusCode"));
			sendNotification(request, statusCode);
			response.setRidStatus(statusCode);
		}
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::getRidStatus():: exit");
		return response;
	}

	private void sendNotification(RequestDTO request, String statusCode)  {
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::sendNotification():: entry");
		NotificationRequestDto notificationRequestDto=new NotificationRequestDto();
		notificationRequestDto.setId(request.getIndividualId());
		notificationRequestDto.setIdType(getIdType(request.getIndividualIdType()));
		notificationRequestDto.setRegistrationType("NEW");
		notificationRequestDto.setTemplateTypeCode(NotificationTemplateCode.RS_NO_MOB_MAIL_ID);
		Map<String, Object> attribute = new HashMap<String, Object>();
		attribute.put("statusCode", statusCode);
		notificationRequestDto.setAdditionalAttributes(attribute);
		try {
			NotificationResponseDTO notificationResponse = notificationService.sendNotification(notificationRequestDto);
			logger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), request.getIndividualId(),
					notificationResponse.getStatus()+notificationResponse.getMessage());
		} catch (ResidentServiceCheckedException e) {
			logger.error(LoggerFileConstant.SESSIONID.toString(),
					LoggerFileConstant.REGISTRATIONID.toString(),"", e.getMessage() + ExceptionUtils.getStackTrace(e));
		}
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::sendNotification():: exit");
	}

	private void validateRID(String registrationId) {
		if (registrationId.length() == RID_LENGTH && registrationId.matches("[0-9]+")) {
			return;
		}
		throw new RIDInvalidException(ResidentErrorCode.INVALID_RID_EXCEPTION.getErrorCode(),
				ResidentErrorCode.INVALID_RID_EXCEPTION.getErrorMessage());
	}

	private String validateResponse(String statusCode) {
		if (statusCode.equalsIgnoreCase(RegistrationExternalStatusCode.PROCESSED.name()))
			return statusCode.toUpperCase();
		if (statusCode.equalsIgnoreCase(RegistrationExternalStatusCode.REJECTED.name()))
			return REJECTED_MESSAGE;
		if (statusCode.equalsIgnoreCase(RegistrationExternalStatusCode.REREGISTER.name()))
			return REREGISTER_MESSAGE;
		if (statusCode.equalsIgnoreCase(RegistrationExternalStatusCode.RESEND.name()))
			return RESEND_MESSAGE;
		if (statusCode.equalsIgnoreCase(RegistrationExternalStatusCode.PROCESSING.name()))
			return PROCESSING_MESSAGE;
		return PROCESSING_MESSAGE;

	}

	/********************** end of RID status check *************/

	@Override
	public byte[] reqEuin(EuinRequestDTO dto) throws OtpValidationFailedException {
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::reqEuin():: entry");
		byte[] response = null;
		IdType idtype = getIdType(dto.getIndividualIdType());
		if (validateIndividualId(dto.getIndividualId(), dto.getIndividualIdType())) {

			if (idAuthService.validateOtp(dto.getTransactionID(), dto.getIndividualId(), dto.getIndividualIdType(),
					dto.getOtp())) {
				try {
					response = uinCardDownloadService.getUINCard(dto.getIndividualId(), dto.getCardType(), idtype);
					if (response != null) {
						NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
						notificationRequestDto.setId(dto.getIndividualId());
						notificationRequestDto.setIdType(idtype);
						notificationRequestDto.setTemplateTypeCode(NotificationTemplateCode.RS_DOW_UIN_Status);
						notificationService.sendNotification(notificationRequestDto);
					} else {
						throw new ResidentServiceException(ResidentErrorCode.REQUEST_FAILED.getErrorCode(),
								ResidentErrorCode.REQUEST_FAILED.getErrorMessage());
					}
				} catch (ApisResourceAccessException e) {
					logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
							LoggerFileConstant.APPLICATIONID.toString(),
							ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode()
									+ ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage()
									+ ExceptionUtils.getStackTrace(e));
					throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode(),
							ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage(), e);
				} catch (ResidentServiceCheckedException e) {
					logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
							LoggerFileConstant.APPLICATIONID.toString(),
							ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode()
									+ ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage()
									+ ExceptionUtils.getStackTrace(e));
					throw new ResidentServiceException(ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode(),
							ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage(), e);
				}
			} else {
				logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
				throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
			}
		} else {
			logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
			throw new ResidentServiceException(ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorCode(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
		}
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::reqEuin():: exit");
		return response;
	}

	@Override
	public ResidentReprintResponseDto reqPrintUin(ResidentReprintRequestDto dto) {
		ResidentReprintResponseDto reprintResponse = new ResidentReprintResponseDto();
		if (validateIndividualId(dto.getIndividualId(), dto.getIndividualIdType().name())) {

			try {
				if (!idAuthService.validateOtp(dto.getTransactionID(), dto.getIndividualId(),
						dto.getIndividualIdType().name(), dto.getOtp())) {
					throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
							ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
				}
				RegProcRePrintRequestDto rePrintReq = new RegProcRePrintRequestDto();
				rePrintReq.setCardType(dto.getIndividualIdType().name());
				rePrintReq.setCenterId(centerId);
				rePrintReq.setMachineId(machineId);
				rePrintReq.setId(dto.getIndividualId());
				rePrintReq.setIdType(dto.getIndividualIdType().name());
				rePrintReq.setReason("resident");
				rePrintReq.setRegistrationType("RES_REPRINT");
				RequestWrapper<RegProcRePrintRequestDto> request = new RequestWrapper<>();
				request.setRequest(rePrintReq);
				request.setId("mosip.uincard.reprint");
				request.setVersion("1.0");
				request.setRequesttime(DateUtils.getUTCCurrentDateTimeString());

				ResponseWrapper<RegProcRePrintResponseDto> response = residentServiceRestClient.postApi(
						env.getProperty(ApiName.REPRINTUIN.name()), MediaType.APPLICATION_JSON, request,
						ResponseWrapper.class, tokenGenerator.getRegprocToken());
				ObjectMapper mapper = new ObjectMapper();
				if (response == null
						|| response != null && response.getErrors() != null && !response.getErrors().isEmpty())
					throw new ResidentServiceException(ResidentErrorCode.RE_PRINT_REQUEST_FAILED.getErrorCode(),
							ResidentErrorCode.RE_PRINT_REQUEST_FAILED.getErrorMessage() + response != null
									? response.getErrors().get(0).toString()
									: null);
				RegProcRePrintResponseDto responseDto = mapper
						.readValue(mapper.writeValueAsString(response.getResponse()), RegProcRePrintResponseDto.class);
				NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
				notificationRequestDto.setId(dto.getIndividualId());
				notificationRequestDto.setIdType(dto.getIndividualIdType());
				notificationRequestDto.setTemplateTypeCode(NotificationTemplateCode.RS_UIN_RPR_Status);
				Map<String, Object> additionalAttributes = new HashMap<String, Object>();
				additionalAttributes.put("RID", responseDto.getRegistrationId());
				notificationRequestDto.setAdditionalAttributes(additionalAttributes);
				NotificationResponseDTO notificationResponseDTO = notificationService
						.sendNotification(notificationRequestDto);
				reprintResponse.setRegistrationId(responseDto.getRegistrationId());
				reprintResponse.setMessage(notificationResponseDTO.getMessage());

			}
			catch (OtpValidationFailedException e) {
				throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage(), e);
			}
			catch (ApisResourceAccessException e) {
				if (e.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
					throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
							httpClientException.getResponseBodyAsString());

				} else if (e.getCause() instanceof HttpServerErrorException) {
					HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
					throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
							httpServerException.getResponseBodyAsString());
				} else {
					throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorCode(),
							ResidentErrorCode.API_RESOURCE_ACCESS_EXCEPTION.getErrorMessage() + e.getMessage(), e);
				}
			} catch (IOException e) {
				throw new ResidentServiceException(ResidentErrorCode.IO_EXCEPTION.getErrorCode(),
						ResidentErrorCode.IO_EXCEPTION.getErrorMessage(), e);
			} catch (ResidentServiceCheckedException e) {
				throw new ResidentServiceException(ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode(),
						ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage(), e);
			}

		}

		return reprintResponse;
	}

	@Override
	public ResponseDTO reqUin(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDTO reqRid(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDTO reqUpdateUin(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDTO revokeVid(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDTO reqAauthLock(AuthLockRequestDto dto) throws OtpValidationFailedException {
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::reqAauthLock():: entry");

		ResponseDTO response = new ResponseDTO();
		if (validateIndividualId(dto.getIndividualId(), dto.getIndividualIdType())) {
			if (idAuthService.validateOtp(dto.getTransactionID(), dto.getIndividualId(), dto.getIndividualIdType(),
					dto.getOtp())) {

				try {
					boolean isAuthTypeLocked = idAuthService.authTypeStatusUpdate(dto.getIndividualId(),
							dto.getIndividualIdType(), dto.getAuthType(), true);
					if (isAuthTypeLocked) {
						NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
						notificationRequestDto.setId(dto.getIndividualId());
						notificationRequestDto.setIdType(getIdType(dto.getIndividualIdType()));
						notificationRequestDto.setTemplateTypeCode(NotificationTemplateCode.RS_LOCK_AUTH_Status);

						NotificationResponseDTO notificationResponseDTO = notificationService
								.sendNotification(notificationRequestDto);
						response.setMessage(notificationResponseDTO.getMessage());

					} else {
						throw new ResidentServiceException(ResidentErrorCode.REQUEST_FAILED.getErrorCode(),
								ResidentErrorCode.REQUEST_FAILED.getErrorMessage());
					}
				} catch (ApisResourceAccessException e) {
					logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
							LoggerFileConstant.APPLICATIONID.toString(),
							ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode()
									+ ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage()
									+ ExceptionUtils.getStackTrace(e));
					throw new ResidentServiceException(ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode(),
							ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorMessage(), e);
				} catch (ResidentServiceCheckedException e) {
					logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
							LoggerFileConstant.APPLICATIONID.toString(),
							ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode()
									+ ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage()
									+ ExceptionUtils.getStackTrace(e));
					throw new ResidentServiceException(ResidentErrorCode.NOTIFICATION_FAILURE.getErrorCode(),
							ResidentErrorCode.NOTIFICATION_FAILURE.getErrorMessage(), e);
				}
			} else {
				logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
						LoggerFileConstant.APPLICATIONID.toString(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
				throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
			}
		} else {
			logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
					LoggerFileConstant.APPLICATIONID.toString(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
			throw new ResidentServiceException(ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorCode(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
		}
		logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.APPLICATIONID.toString(),
				LoggerFileConstant.APPLICATIONID.toString(), "ResidentServiceImpl::reqAauthLock():: exit");
		return response;
	}

	@Override
	public ResponseDTO reqAuthUnlock(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseDTO reqAuthHistory(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean validateIndividualId(String individualId, String individualIdType) {
		boolean validation = false;
		if (individualIdType.equalsIgnoreCase(IdType.UIN.toString())) {
			validation = uinValidator.validateId(individualId);
		} else if (individualIdType.equalsIgnoreCase(IdType.VID.toString())) {
			validation = vidValidator.validateId(individualId);
		} else if (individualIdType.equalsIgnoreCase(IdType.RID.toString())) {
			validation = ridValidator.validateId(individualId);
		} else {
			throw new ResidentServiceException(ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorCode(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
		}
		return validation;
	}

	private IdType getIdType(String individualIdType) {
		IdType idType = null;
		if (individualIdType.equalsIgnoreCase(IdType.UIN.toString())) {
			idType = IdType.UIN;
		} else if (individualIdType.equalsIgnoreCase(IdType.VID.toString())) {
			idType = IdType.VID;
		} else if (individualIdType.equalsIgnoreCase(IdType.RID.toString())) {
			idType = IdType.RID;
		}
		return idType;
	}

}