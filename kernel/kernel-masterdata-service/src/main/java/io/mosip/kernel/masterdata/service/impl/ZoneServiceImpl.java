package io.mosip.kernel.masterdata.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.constant.ZoneErrorCode;
import io.mosip.kernel.masterdata.dto.getresponse.ZoneNameResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.ZoneExtnDto;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ZoneRepository;
import io.mosip.kernel.masterdata.repository.ZoneUserRepository;
import io.mosip.kernel.masterdata.service.ZoneService;
import io.mosip.kernel.masterdata.utils.MapperUtils;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

/**
 * Zone Service Implementation
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Service
public class ZoneServiceImpl implements ZoneService {

	@Autowired
	private ZoneUtils zoneUtils;

	@Autowired
	ZoneUserRepository zoneUserRepository;

	@Autowired
	ZoneRepository zoneRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ZoneService#getUserZoneHierarchy(java.lang
	 * .String)
	 */
	@Override
	public List<ZoneExtnDto> getUserZoneHierarchy(String langCode) {
		List<Zone> zones = zoneUtils.getUserZones();
		if (zones != null && !zones.isEmpty()) {
			List<Zone> zoneList = zones.parallelStream().filter(z -> z.getLangCode().equals(langCode))
					.collect(Collectors.toList());
			return MapperUtils.mapAll(zoneList, ZoneExtnDto.class);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.masterdata.service.ZoneService#getUserLeafZone(java.lang.
	 * String)
	 */
	@Override
	public List<ZoneExtnDto> getUserLeafZone(String langCode) {
		List<Zone> zones = zoneUtils.getUserLeafZones(langCode);
		if (zones != null && !zones.isEmpty()) {
			List<Zone> zoneList = zones.parallelStream().filter(z -> z.getLangCode().equals(langCode))
					.collect(Collectors.toList());
			return MapperUtils.mapAll(zoneList, ZoneExtnDto.class);
		}
		return Collections.emptyList();
	}

	@Override
	public ZoneNameResponseDto getZoneNameBasedOnLangCodeAndUserID(String userID, String langCode) {
		ZoneNameResponseDto zoneNameResponseDto = new ZoneNameResponseDto();
		ZoneUser zoneUser = null;
		Zone zone = null;
		try {
			zoneUser = zoneUserRepository.findByUserIdAndLangCodeNonDeleted(userID, langCode);
			if (zoneUser == null) {
				throw new DataNotFoundException(ZoneErrorCode.ZONEUSER_ENTITY_NOT_FOUND.getErrorCode(),
						ZoneErrorCode.ZONEUSER_ENTITY_NOT_FOUND.getErrorMessage());
			}
			zone = zoneRepository.findZoneByCodeAndLangCodeNonDeleted(zoneUser.getZoneCode(), zoneUser.getLangCode());
			if (zone == null) {
				throw new DataNotFoundException(ZoneErrorCode.ZONE_ENTITY_NOT_FOUND.getErrorCode(),
						ZoneErrorCode.ZONE_ENTITY_NOT_FOUND.getErrorMessage());
			}
		} catch (DataAccessException | DataAccessLayerException exception) {
			throw new MasterDataServiceException(ZoneErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
					ZoneErrorCode.INTERNAL_SERVER_ERROR.getErrorMessage());
		}
		zoneNameResponseDto.setZoneName(zone.getName());
		return zoneNameResponseDto;
	}

}
