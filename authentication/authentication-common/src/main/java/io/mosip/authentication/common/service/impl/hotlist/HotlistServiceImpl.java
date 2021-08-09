package io.mosip.authentication.common.service.impl.hotlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.entity.HotlistCache;
import io.mosip.authentication.common.service.repository.HotlistCacheRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.hotlist.constant.HotlistStatus;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.core.websub.model.EventModel;

/**
 * @author Manoj SP
 * @author Mamta A
 */
@Service
@Transactional
public class HotlistServiceImpl implements HotlistService {

	@Value("#{'${mosip.ida.internal.hotlist.idtypes.allowed}'.split(',')}")
	private List<String> idTypes;

	private static final String UNBLOCKED = "UNBLOCKED";

	private static final String EXPIRY_TIMESTAMP = "expiryTimestamp";

	private static final String STATUS = "status";

	private static final String ID_TYPE = "idType";

	private static final String ID = "id";

	@PostConstruct
	public void init() {
		idTypes.remove("");
	}

	@Autowired
	private HotlistCacheRepository hotlistCacheRepo;

	@Override
	public void updateHotlist(String id, String idType, String status, LocalDateTime expiryTimestamp)
			throws IdAuthenticationBusinessException {
		Optional<HotlistCache> hotlistData = hotlistCacheRepo.findByIdHashAndIdType(id, idType);
		if (hotlistData.isPresent()) {
			HotlistCache hotlistCache = hotlistData.get();
			hotlistCache.setStatus(status);
			hotlistCache.setExpiryDTimes(expiryTimestamp);
			hotlistCacheRepo.save(hotlistCache);
		} else {
			HotlistCache hotlistCache = new HotlistCache();
			hotlistCache.setIdHash(id);
			hotlistCache.setIdType(idType);
			hotlistCache.setStatus(status);
			hotlistCache.setExpiryDTimes(expiryTimestamp);
			hotlistCacheRepo.save(hotlistCache);
		}
	}

	@Override
	public void unblock(String id, String idType) throws IdAuthenticationBusinessException {
		Optional<HotlistCache> hotlistData = hotlistCacheRepo.findByIdHashAndIdType(id, idType);
		if (hotlistData.isPresent()) {
			HotlistCache hotlistCache = hotlistData.get();
			hotlistCacheRepo.delete(hotlistCache);
		}
	}

	/**
	 * Retrieve the Hotlist Status information.
	 * 
	 * @param id     the id_hash
	 * @param idType the id_type
	 * @return HotlistDTO consist of hotlisting information
	 * @throws IdAuthenticationBusinessException
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.hotlist.service.HotlistService#
	 * getHotlistStatus(java.lang.String, java.lang.String)
	 */
	@Override
	public HotlistDTO getHotlistStatus(String id, String idType) {
		HotlistDTO dto = new HotlistDTO();
		Optional<HotlistCache> hotlistData = hotlistCacheRepo.findByIdHashAndIdType(id, idType);
		if (hotlistData.isPresent()) {
			HotlistCache hotlistCache = hotlistData.get();
			dto.setStartDTimes(hotlistCache.getStartDTimes());
			if (Objects.nonNull(hotlistCache.getExpiryDTimes())
					&& hotlistCache.getExpiryDTimes().isAfter(DateUtils.getUTCCurrentDateTime())) {
				if (hotlistCache.getStatus().contentEquals(HotlistStatus.BLOCKED))
					dto.setStatus(HotlistStatus.UNBLOCKED);
				else
					dto.setStatus(HotlistStatus.BLOCKED);
			} else {
				dto.setStatus(hotlistCache.getStatus());
			}
		} else {
			dto.setStatus(HotlistStatus.UNBLOCKED);
		}
		return dto;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void handlingHotlistingEvent(EventModel eventModel) throws IdAuthenticationBusinessException {
		Map<String, Object> eventData = eventModel.getEvent().getData();
		if (Objects.nonNull(eventData) && !((Map) eventData).isEmpty()) {
			if (validateHotlistEventData(eventData) && idTypes.contains(eventData.get(ID_TYPE))) {
				String id = (String) eventData.get(ID);
				String idType = (String) eventData.get(ID_TYPE);
				if (idType.equalsIgnoreCase(IdType.UIN.getType()) || idType.equalsIgnoreCase(IdType.VID.getType())) {
					idType = IdAuthCommonConstants.INDIVIDUAL_ID;
				}
				String status = (String) eventData.get(STATUS);
				String expiryTimestamp = (String) eventData.get(EXPIRY_TIMESTAMP);
				if (status.contentEquals(UNBLOCKED) && Objects.isNull(expiryTimestamp)) {
					unblock(id, idType);
				} else {
					updateHotlist(id, idType, status,
							StringUtils.isNotBlank(expiryTimestamp) ? DateUtils.parseToLocalDateTime(expiryTimestamp)
									: null);
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

}
