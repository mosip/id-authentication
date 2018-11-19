package io.kernel.core.idrepo.validator;

import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

@Component
public class IdRequestValidator implements Validator {

    @Autowired
    private Environment env;
    
    @Autowired
    private UinValidatorImpl uinValidator;

    @Override
    public boolean supports(Class<?> clazz) {
	return clazz.isAssignableFrom(IdRequestDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
	IdRequestDTO request = (IdRequestDTO) target;

	validateReqTime(request.getTimestamp(), errors);
	
	if (!errors.hasErrors()) {
	    validateId(request.getId(), errors);
	    validateVer(request.getVer(), errors);
	    validateUin(request.getUin(), errors);
	    validateStatus(request.getStatus(), errors);
	    validateRegId(request.getRegistrationId(), errors);
	    validateRequest(request.getRequest(), errors);
	}
    }

    private void validateId(String id, Errors errors) {
	if(!id.equals(env.getProperty("mosip.idrepo.id"))) {
	    errors.rejectValue("id", "errorCode", "validateId");
	}
    }

    private void validateVer(String ver, Errors errors) {
	if (!ver.equals(env.getProperty("mosip.idrepo.version"))) {
	    errors.rejectValue("ver", "errorCode", "validateVer");
	}
	
    }

    private void validateUin(String uin, Errors errors) {
	
    }

    private void validateStatus(String status, Errors errors) {
	if(!status.equals(env.getProperty("mosip.idrepo.status.registered"))) {
	    errors.rejectValue("ver", "errorCode", "validateStatus");
	}
    }

    private void validateRegId(String registrationId, Errors errors) {
	
    }

    private void validateRequest(JSONObject request, Errors errors) {
	
    }

    private void validateReqTime(Date timestamp, Errors errors) {
	
    }

}
