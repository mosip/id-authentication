package io.mosip.authentication.internal.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

@Component
public class CredentialIssueEventValidator extends IdAuthValidator {

	private static final String EVENT_FIELD = "event";

	@Override
	public boolean supports(Class<?> clazz) {
		return EventModel.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(target instanceof EventModel) {
			EventModel eventModel = (EventModel) target;
			
			if(!errors.hasErrors()) {
				Event event = eventModel.getEvent();
				if(event == null) {
					errors.rejectValue(EVENT_FIELD,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { EVENT_FIELD },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				}
			}
		}
	}

}
