package io.mosip.authentication.service.impl.indauth.validator;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * Validator for internal authentication request
 * @author Prem Kumar
 *
 */
@Component
public class InternalAuthRequestValidator implements Validator {

	/** UIN validator impl */
	@Autowired
	private UinValidatorImpl uinValidatorImpl;

	/** VID validator Impl */
	@Autowired
	private VidValidatorImpl vidValidatorImpl;
	
	@Autowired
	private DateHelper datehelper;

	@Override
	public boolean supports(Class<?> arg0) {
		return false;
	}

	@Override
	public void validate(Object arg0, Errors arg1) {

	}

	/** validation for UIN and VIN */
	public void validateIdvId(AuthRequestDTO authRequestDTO,Errors errors) {
		String refId = authRequestDTO.getIdvIdType();
		
		if(refId!=null) {
				boolean status=validateUinVin(authRequestDTO, refId);
				if(!status && refId.equals(IdType.UIN.getType())) {
					errors.reject(refId, IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode());
				}
				else if(!status && refId.equals(IdType.VID.getType()))
				{
					errors.reject(refId, IdAuthenticationErrorConstants.INVALID_VID.getErrorCode());

				}
		}
		
	}

	
	/** Validation for Pin Info */
//	public void validatePin(AuthRequestDTO authRequestDTO) {
//		List<PinInfo> pinlist = authRequestDTO.getPinInfo();
//		boolean status=false;
//		for (PinInfo type : pinlist) {
//			String pintype = type.getValue();
//			if (pintype.equalsIgnoreCase(PinType.OTP.getType())) {
//					if(type.getValue().length()==6 && type.getValue()!=null)
//					{
//						status=true;
//					}
//				
//			} else if (pintype.equalsIgnoreCase(PinType.PIN.getType())) {
//			}
//		}
//
//	}
	
	/** Validation for Request AuthType */
	public void validateRequest(AuthRequestDTO  authRequestDTO,Errors errors)
	{
		AuthTypeDTO authTypeDTO=authRequestDTO.getAuthType();
		if(authTypeDTO!=null)
		{
			
			if(authRequestDTO.getAuthType().isFingerprint())
			{
			
			boolean finger=validateFinger(authRequestDTO);
			if(!finger)
			{
				errors.reject("request", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
			}
		}
		if(authRequestDTO.getAuthType().isIris())
		{
			boolean iris=validateIris(authRequestDTO);
			if(!iris)
			{
				errors.reject("request", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
			}
		}
		if(authRequestDTO.getAuthType().isFace())
		{
			boolean face=validateFace(authRequestDTO);
			if(!face)
			{
				errors.reject("request", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
			}
		}
		}
		else
		{
			errors.reject("request", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
		}

	}
	
	/** Validation for DateTime */
	public void validateDate(AuthRequestDTO authRequestDTO,Errors errors)
	{
		if(!authRequestDTO.getReqTime().isEmpty())
		{
			try {
				Date reqDate = datehelper.convertStringToDate(authRequestDTO.getReqTime());
				if(reqDate.after(new Date())) {
					errors.reject("reqTime", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
				}
				
			} catch (IDDataValidationException e) {
				errors.reject("reqTime", IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode());
			}
			
		}
	}
	
	
	public boolean validateFinger(AuthRequestDTO authRequestDTO)
	{
		if(authRequestDTO.getRequest().getIdentity().getLeftIndex().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getLeftLittle().isEmpty()!=false ||
				authRequestDTO.getRequest().getIdentity().getLeftMiddle().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getLeftRing().isEmpty()!=false ||
				authRequestDTO.getRequest().getIdentity().getLeftThumb().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getRightIndex().isEmpty()!=false ||
				authRequestDTO.getRequest().getIdentity().getRightLittle().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getRightMiddle().isEmpty()!=false ||
				authRequestDTO.getRequest().getIdentity().getRightRing().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getRightThumb().isEmpty()!=false
				)
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	public boolean validateIris(AuthRequestDTO authRequestDTO)
	{
		if(authRequestDTO.getRequest().getIdentity().getLeftEye().isEmpty()!=false || authRequestDTO.getRequest().getIdentity().getRightEye().isEmpty()!=false)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean validateFace(AuthRequestDTO authRequestDTO)
	{
		if(authRequestDTO.getRequest().getIdentity().getFace().isEmpty()!=false)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean validateUinVin(AuthRequestDTO authRequestDTO,String refId)
	{
		boolean status=false;
		if (refId.equals(IdType.UIN.getType())) {
			status=uinValidatorImpl.validateId(authRequestDTO.getIdvId());
		} else if (refId.equals(IdType.VID.getType())) {
			status=vidValidatorImpl.validateId(authRequestDTO.getIdvId());
		}
		return status;
	}
	
	
}


