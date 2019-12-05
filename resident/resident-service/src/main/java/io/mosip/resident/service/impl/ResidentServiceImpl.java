package io.mosip.resident.service.impl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.NotificationRequestDto;
import io.mosip.resident.dto.PrintRequest;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.dto.UINCardRequestDTO;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceException;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.util.NotificationService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

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
    private Environment env;

    @Autowired
    private ResidentServiceRestClient residentServiceRestClient;

    @Autowired
    private TokenGenerator tokenGenerator;
    
    @Autowired
    private IdAuthService idAuthService;
    
    @Autowired
    NotificationService notificationService;
    
    private static final String PRINT_ID="mosip.registration.processor.print.id";
    private static final String PRINT_VERSION="mosip.registration.processor.application.version";


	@Override
	public ResponseDTO getRidStatus(RequestDTO request) {
		ResponseDTO response = new ResponseDTO();
		response.setMessage("RID status successfully sent to abXXXXXXXXXcd@xyz.com");
		response.setStatus("success");
		return response;
	}

	@Override
	public byte[]  reqEuin(EuinRequestDTO dto) {
		
		byte[]	response;
		
		if(validateIndividualId(dto.getIndividualId(),dto.getIndividualIdType())) {
			
			if(idAuthService.validateOtp(dto.getTransactionID(), dto.getIndividualId(),
					dto.getIndividualIdType(), dto.getOtp())) {
				
				PrintRequest request=new PrintRequest();
				UINCardRequestDTO uincardDTO=new UINCardRequestDTO();
				uincardDTO.setCardType(dto.getCardType());
				uincardDTO.setIdValue(dto.getIndividualId());
				IdType idtype=getIdType(dto.getIndividualIdType());
				uincardDTO.setIdtype(idtype);
				request.setRequest(uincardDTO);
				request.setId(env.getProperty(PRINT_ID));
				request.setVersion(env.getProperty(PRINT_VERSION));
				request.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
				try {
					response = (byte[]) residentServiceRestClient.postApi(env.getProperty(ApiName.REGPROCPRINT.name()),
							null,request, byte[].class, tokenGenerator.getToken());
					if(response !=null) {
						NotificationRequestDto notificationRequestDto=new NotificationRequestDto();
						notificationRequestDto.setId(dto.getIndividualId());
						notificationRequestDto.setIdType(idtype);
						notificationRequestDto.setRegistrationType("NEW");
						notificationRequestDto.setTemplateType(NotificationTemplateCode.RS_DOW_UIN_Status);
						notificationService.sendNotification(notificationRequestDto);
					}
					
				} catch ( Exception e) {
					logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
							dto.getIndividualIdType(), ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode()
							+ e.getMessage()+ ExceptionUtils.getStackTrace(e));
					throw new ApisResourceAccessException("Unable to fetch uin card");
				} 
			}
			else {
				throw new ResidentServiceException(ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorCode(),
						ResidentErrorCode.OTP_VALIDATION_FAILED.getErrorMessage());
			}
		}
		else {
			throw new ResidentServiceException(ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorCode(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
		}
		
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
	public ResponseDTO reqAauthLock(RequestDTO dto) {
		// TODO Auto-generated method stub
		return null;
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
	
	private boolean validateIndividualId(String individualId,String individualIdType) {
		boolean validation=false;
		if(individualIdType.equalsIgnoreCase(IdType.UIN.toString())) {
			validation= uinValidator.validateId(individualId);
		}
		else if(individualIdType.equalsIgnoreCase(IdType.VID.toString())) {
			validation= vidValidator.validateId(individualId);
		}
		else if(individualIdType.equalsIgnoreCase(IdType.RID.toString())) {
			validation= ridValidator.validateId(individualId);
		}
		else {
			throw new ResidentServiceException(ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorCode(),
					ResidentErrorCode.IN_VALID_UIN_OR_VID.getErrorMessage());
		}
		return validation;
	}
	private IdType getIdType(String individualIdType) {
		IdType idType=null;
		if(individualIdType.equalsIgnoreCase(IdType.UIN.toString())) {
			idType= IdType.UIN;
		}
		else if(individualIdType.equalsIgnoreCase(IdType.VID.toString())) {
			idType= IdType.VID;
		}
		else if(individualIdType.equalsIgnoreCase(IdType.RID.toString())) {
			idType= IdType.RID;
		}
		return idType;
	}

}