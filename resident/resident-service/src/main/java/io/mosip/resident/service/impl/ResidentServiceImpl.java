package io.mosip.resident.service.impl;

import io.mosip.resident.exception.OtpValidationFailedException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.constant.NotificationTemplateCode;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.AuthLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.NotificationResponseDTO;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.UINCardDownloadService;

@Service
public class ResidentServiceImpl implements ResidentService {

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

	@Override
	public ResponseDTO getRidStatus(RequestDTO request) {
		ResponseDTO response = new ResponseDTO();
		response.setMessage("RID status successfully sent to abXXXXXXXXXcd@xyz.com");
		response.setStatus("success");
		return response;
	}

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
						notificationRequestDto.setRegistrationType("NEW");
						notificationRequestDto.setTemplateTypeCode(NotificationTemplateCode.RS_DOW_UIN_Status);
						notificationService.sendNotification(notificationRequestDto);
					}else {
						throw new ResidentServiceException(
								ResidentErrorCode.REQUEST_FAILED.getErrorCode(),
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
	public ResponseDTO reqPrintUin(ResidentReprintRequestDto dto) {
		// TODO Auto-generated method stub
		return null;
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
	public ResponseDTO generatVid(RequestDTO dto) {
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
						throw new ResidentServiceException(
								ResidentErrorCode.REQUEST_FAILED.getErrorCode(),
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