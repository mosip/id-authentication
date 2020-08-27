package io.mosip.authentication.internal.service.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.idrepository.core.dto.Event;
import io.mosip.idrepository.core.dto.EventModel;

@Component
public class IdEventNotificationValidator extends IdAuthValidator {

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
				} else {
					validateEvent(event, errors);
				}
			}
		}
	}


	private void validateEvent(Event event, Errors errors) {
//		String url = event.getDataShareUri();
//		String vid = event.getVid();
//		
//		if(StringUtils.isEmpty(uin) && StringUtils.isEmpty(vid)) {
//			errors.rejectValue(IdAuthCommonConstants.REQUEST,
//					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
//					new Object[] {"request/events/" + index + "/uin or vid" },
//					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
//		}
//		
//		if(!StringUtils.isEmpty(uin)) {
//			validateIdvId(uin, IdType.UIN.getType(), errors);
//		}
//		
//		if(!StringUtils.isEmpty(vid)) {
//			validateIdvId(vid, IdType.VID.getType(), errors);
//		}
//		
//		Integer transactionLimit = event.getTransactionLimit();
//		if(transactionLimit != null && transactionLimit <= 0) {
//			errors.rejectValue(IdAuthCommonConstants.REQUEST,
//					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
//					new Object[] { "request/events/" + index + "/transactionLimit"},
//					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
//		}
	}

	

}
