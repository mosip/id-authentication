package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_CALLBACK_SECRET;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_TOPIC;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.hotlist.dto.HotlistResponseDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 * @author Mamta A
 */
@RestController
public class HotlistEventController {

	@Value("{'${mosip.ida.internal.hotlist.idtypes.allowed}'.split(',')}")
	private List<String> idTypes;
	
	@Value("${ida.api.id.hotlist}")
	private String API_ID;
	
	@Value("${ida.api.version.hotlist}")
	private String API_VERSION;
	
	private static final String UNBLOCKED = "UNBLOCKED";

	private static final String BLOCKED = "BLOCKED";

	private static final String EXPIRY_TIMESTAMP = "expiryTimestamp";

	private static final String STATUS = "status";

	private static final String ID_TYPE = "idType";

	private static final String HOTLIST_DATA = "hotlistData";

	private static final String ID = "id";
	
	
	private static Logger logger = IdaLogger.getLogger(HotlistEventController.class);

	@Autowired
	Environment environment;
	
	@Autowired
	private HotlistService hotlistService;

	@PostConstruct
	public void init() {
		idTypes.remove("");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/callback/hotlist", consumes = "application/json")
	@PreAuthenticateContentAndVerifyIntent(secret = "${" + IDA_WEBSUB_HOTLIST_CALLBACK_SECRET
			+ "}", callback = "/idauthentication/v1/internal/callback/hotlist", topic = "${" + IDA_WEBSUB_HOTLIST_TOPIC
					+ "}")
	public void handleHotlisting(@RequestBody EventModel eventModel) throws IdAuthenticationBusinessException {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "HotlistEventController", "handleHotlisting", "EVENT RECEIVED");
		Object data = eventModel.getEvent().getData().get(HOTLIST_DATA);
		if (Objects.nonNull(data) && data instanceof Map && !((Map) data).isEmpty()) {
			Map<String, Object> eventData = (Map<String, Object>) data;
			if (validateHotlistEventData(eventData) && idTypes.contains(eventData.get(ID_TYPE))) {
				String id = (String) eventData.get(ID);
				String idType = (String) eventData.get(ID_TYPE);
				String status = (String) eventData.get(STATUS);
				String expiryTimestamp = (String) eventData.get(EXPIRY_TIMESTAMP);
				if (status.contentEquals(BLOCKED)) {
					hotlistService.block(id, idType, status,
							StringUtils.isNotBlank(expiryTimestamp) ? DateUtils.parseToLocalDateTime(expiryTimestamp)
									: null);
				} else if (status.contentEquals(UNBLOCKED)) {
					hotlistService.unblock(id, idType);
				}
			}
		}
	}

	private boolean validateHotlistEventData(Map<String, Object> hotlistEventData) {
		Object id = hotlistEventData.get(ID);
		Object idType = hotlistEventData.get(ID_TYPE);
		Object status = hotlistEventData.get(STATUS);
		return hotlistEventData.containsKey(ID) && id instanceof String && StringUtils.isNotBlank((String) id)
				&& hotlistEventData.containsKey(ID_TYPE) && idType instanceof String
				&& StringUtils.isNotBlank((String) idType)
				&& ((hotlistEventData.containsKey(STATUS) && status instanceof String
						&& StringUtils.isNotBlank((String) status))
						|| (hotlistEventData.containsKey(EXPIRY_TIMESTAMP)
								&& hotlistEventData.get(EXPIRY_TIMESTAMP) instanceof String));
	}

	/**
	 * Hotlist status end point
	 * @param id
	 * @param idtype
	 * @return response entity 
	 * @throws IdAuthenticationAppException 
	 */
	@PreAuthorize("hasAnyRole('REGISTRATION_PROCESSOR')")
	@GetMapping(path = "/hotlist/status/id/{id}/idtype/{idtype}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HotlistResponseDTO> getHotlistingStatus(@PathVariable String id, @PathVariable String idtype) throws IdAuthenticationAppException{
		HotlistResponseDTO response = new HotlistResponseDTO();
		response.setId(API_ID);
		response.setVersion(API_VERSION);
		response.setResponseTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		try {
			HotlistDTO hotlistStatus = hotlistService.getHotlistStatus(id, idtype);
			response.setResponse(hotlistStatus);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
		catch (IdAuthenticationBusinessException e) {
			logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(), e.getErrorText());
			throw new IdAuthenticationAppException(e.getErrorCode(), e.getErrorText(), e);
		}
	}
}
