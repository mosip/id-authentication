package io.mosip.authentication.internal.service.validator;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.idrepository.core.dto.EventDTO;
import io.mosip.idrepository.core.dto.EventsDTO;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.util.StringUtils;

@Component
public class IdEventNotificationValidator extends IdAuthValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RequestWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(target instanceof RequestWrapper) {
			RequestWrapper requestWrapper = (RequestWrapper) target;
			validateRequestWrapper(requestWrapper, errors);
			
			if(!errors.hasErrors()) {
				Object request = requestWrapper.getRequest();
				if(request == null) {
					errors.rejectValue(IdAuthCommonConstants.REQUEST,
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
						new Object[] { "request" },
						IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
				} else {
					if(request instanceof EventsDTO) {
						EventsDTO eventsDTO = (EventsDTO) request;
						validateEvents(eventsDTO, errors);
					}
				}
			}
		}
	}

	private void validateEvents(EventsDTO eventsDTO, Errors errors) {
		List<EventDTO> events = eventsDTO.getEvents();
		for (int i = 0; i < events.size(); i++) {
			EventDTO event = events.get(i);
			validateEvent(event, i, errors);
		}
		
	}

	private void validateEvent(EventDTO event, int index, Errors errors) {
		String uin = event.getUin();
		String vid = event.getVid();
		
		if(StringUtils.isEmpty(uin) && StringUtils.isEmpty(vid)) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
					new Object[] {"request/events/" + index + "/uin or vid" },
					IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage());
		}
		
		if(!StringUtils.isEmpty(uin)) {
			validateIdvId(uin, IdType.UIN.getType(), errors);
		}
		
		if(!StringUtils.isEmpty(vid)) {
			validateIdvId(vid, IdType.VID.getType(), errors);
		}
		
		Integer transactionLimit = event.getTransactionLimit();
		if(transactionLimit != null && transactionLimit <= 0) {
			errors.rejectValue(IdAuthCommonConstants.REQUEST,
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					new Object[] { "request/events/" + index + "/transactionLimit"},
					IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage());
		}
	}

	private void validateRequestWrapper(RequestWrapper<?> requestWrapper, Errors errors) {
		String id = requestWrapper.getId();
		validateId(id, errors);
		
		if(!errors.hasErrors()) {
			String dateStr;
			if(requestWrapper.getRequesttime() != null) {
				SimpleDateFormat formatter = new SimpleDateFormat(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
				Date date = new Date(requestWrapper.getRequesttime().toInstant(ZoneOffset.UTC).toEpochMilli());
				dateStr = formatter.format(date);
			} else {
				dateStr = null;
			}
			validateReqTime(dateStr, errors, "requesttime", "requesttime");
		}
	}
	

}
