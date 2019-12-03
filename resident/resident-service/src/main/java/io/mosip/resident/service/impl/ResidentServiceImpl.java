package io.mosip.resident.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.PrintRequest;
import io.mosip.resident.dto.PrintResponse;
import io.mosip.resident.dto.RequestDTO;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.dto.UINCardRequestDTO;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@Service
public class ResidentServiceImpl implements ResidentService {

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
    
    private static final String PRINT_ID="mosip.registration.processor.print.id";
    private static final String PRINT_VERSION="mosip.registration.processor.application.version";
    private static final String DATETIME_PATTERN = "mosip.utc-datetime-pattern";


	@Override
	public ResponseDTO getRidStatus(RequestDTO request) {
		ResponseDTO response = new ResponseDTO();
		response.setMessage("RID status successfully sent to abXXXXXXXXXcd@xyz.com");
		response.setStatus("success");
		return response;
	}

	@Override
	public PrintResponse reqEuin(UINCardRequestDTO dto) {
		if(validateIndividualId(dto.getIdValue(),dto.getIdtype())) {
		/*TODO IDA OTP Authentication*/
		}
		PrintRequest request=new PrintRequest();
		request.setRequest(dto);
		request.setId(env.getProperty(PRINT_ID));
		request.setVersion(env.getProperty(PRINT_VERSION));
		request.setRequesttime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
		PrintResponse response= new PrintResponse();
		try {
			response = (PrintResponse) residentServiceRestClient.postApi(env.getProperty(ApiName.REGPROCPRINT.name()), null,
					request, PrintResponse.class, tokenGenerator.getToken());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*TODO Send notification*/
		return response;
	}

	@Override
	public ResponseDTO reqPrintUin(RequestDTO dto) {
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
	
	private boolean validateIndividualId(String individualId,IdType individualIdType) {
		boolean validation=false;
		if(individualIdType.toString().equalsIgnoreCase(IdType.UIN.toString())) {
			validation= uinValidator.validateId(individualId);
		}
		else if(individualIdType.toString().equalsIgnoreCase(IdType.VID.toString())) {
			validation= vidValidator.validateId(individualId);
		}
		else if(individualIdType.toString().equalsIgnoreCase(IdType.RID.toString())) {
			validation= ridValidator.validateId(individualId);
		}
		return validation;
	}

}