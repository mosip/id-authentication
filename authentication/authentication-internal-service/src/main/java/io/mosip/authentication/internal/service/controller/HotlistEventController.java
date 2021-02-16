package io.mosip.authentication.internal.service.controller;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_TOPIC;
import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.IDA_WEBSUB_HOTLIST_CALLBACK_SECRET;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.websub.api.annotation.PreAuthenticateContentAndVerifyIntent;

/**
 * @author Manoj SP
 *
 */
@RestController
public class HotlistEventController {

	@Value("#{'${mosip.ida.internal.hotlist.idtypes.allowed:}'.split(',')}")
	private List<String> idTypes;

	private static final String UNBLOCKED = "UNBLOCKED";

	private static final String BLOCKED = "BLOCKED";

	private static final String EXPIRY_TIMESTAMP = "expiryTimestamp";

	private static final String STATUS = "status";

	private static final String ID_TYPE = "idType";

	private static final String HOTLIST_DATA = "hotlistData";

	private static final String ID = "id";

	private static Logger logger = IdaLogger.getLogger(HotlistEventController.class);

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
	public void handleHotlisting(@RequestBody EventModel eventModel) {
		logger.debug(IdAuthCommonConstants.SESSION_ID, "HotlistEventController", "handleHotlisting", "EVENT RECEIVED");
		Object data = eventModel.getEvent().getData().get(HOTLIST_DATA);
		if (Objects.nonNull(data) && data instanceof List && !((List) data).isEmpty()) {
			((List<Map<String, Object>>) data).stream()
					.filter(hotlistEventData -> validateHotlistEventData(hotlistEventData)
							&& idTypes.contains(hotlistEventData.get(ID_TYPE)))
					.forEach(hotlistData -> {
						String id = (String) hotlistData.get(ID);
						String idType = (String) hotlistData.get(ID_TYPE);
						String status = (String) hotlistData.get(STATUS);
						String expiryTimestamp = (String) hotlistData.get(EXPIRY_TIMESTAMP);
						try {
							if (status.contentEquals(BLOCKED)) {
								hotlistService.block(id, idType, status,
										StringUtils.isNotBlank(expiryTimestamp)
												? DateUtils.parseToLocalDateTime(expiryTimestamp)
												: null);
							} else if (status.contentEquals(UNBLOCKED)) {
								hotlistService.unblock(id, idType);
							}
						} catch (IdAuthenticationBusinessException e) {
							throw new IdAuthUncheckedException(e.getErrorCode(), e.getErrorText(), e);
						}
					});
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

}
