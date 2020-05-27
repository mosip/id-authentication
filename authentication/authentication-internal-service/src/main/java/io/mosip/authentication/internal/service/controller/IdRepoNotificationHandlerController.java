package io.mosip.authentication.internal.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.idevent.service.IdChangeEventHandlerService;
import io.mosip.authentication.internal.service.validator.IdEventNotificationValidator;
import io.mosip.idrepository.core.dto.EventDTO;
import io.mosip.idrepository.core.dto.EventsDTO;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The {@code IdRepoNotificationHandlerController} used to handle the
 * notification events posted by ID Repo module.
 *
 * @author Loganathan Sekar
 */
@RestController
public class IdRepoNotificationHandlerController {
	
	/** The id change event handler service. */
	@Autowired
	private IdChangeEventHandlerService idChangeEventHandlerService;

	@Autowired
	private IdEventNotificationValidator validator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validator);
	}

	/**
	 * Handle events end point.
	 *
	 * @param notificationEventsDto the notification events dto
	 * @param e the e
	 * @return the response entity
	 * @throws IdAuthenticationBusinessException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR', 'RESIDENT', 'ID_AUTHENTICATION')")
	@PostMapping(path = "/notify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Event Notification Callback API", response = IdAuthenticationAppException.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request authenticated successfully") })
	public ResponseWrapper<?> handleEvents(@Validated @RequestBody RequestWrapper<EventsDTO> notificationEventsDto, @ApiIgnore Errors e) throws IdAuthenticationBusinessException {
		DataValidationUtil.validate(e);
		handleEvents(notificationEventsDto.getRequest().getEvents());
		return new ResponseWrapper<>();
	}

	/**
	 * Handle events.
	 *
	 * @param events the events
	 * @return true, if successful
	 * @throws IdAuthenticationBusinessException 
	 */
	private void handleEvents(List<EventDTO> events) throws IdAuthenticationBusinessException {
		idChangeEventHandlerService.handleIdEvent(events);
	}

}
