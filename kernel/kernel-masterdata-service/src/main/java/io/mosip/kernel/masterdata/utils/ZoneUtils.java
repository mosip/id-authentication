package io.mosip.kernel.masterdata.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.masterdata.constant.ZoneErrorCode;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ZoneRepository;
import io.mosip.kernel.masterdata.repository.ZoneUserRepository;

/**
 * Zone utility
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Component
public class ZoneUtils {

	@Autowired
	private ZoneRepository zoneRepository;

	@Autowired
	private ZoneUserRepository zoneUserRepository;

	ThreadLocal<List<Zone>> local;
	ThreadLocal<List<Zone>> list;

	public List<Zone> getUserZones(List<Zone> zones) {
		List<Zone> zoneIds = new ArrayList<>();
		List<ZoneUser> userZones = null;
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			userZones = zoneUserRepository.findByUserIdNonDeleted(userName);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ZoneErrorCode.USER_ZONE_FETCH_EXCEPTION.getErrorCode(),
					ZoneErrorCode.USER_ZONE_FETCH_EXCEPTION.getErrorMessage());
		}
		if (userZones != null && !userZones.isEmpty()) {
			initialize(zones);
			for (ZoneUser zu : userZones) {
				searchZones(zones, zoneIds, zu);
			}
		} else {
			throw new MasterDataServiceException(ZoneErrorCode.USER_ZONE_UNAVAILABLE.getErrorCode(),
					String.format(ZoneErrorCode.USER_ZONE_UNAVAILABLE.getErrorMessage(), userName));
		}
		return zoneIds;
	}

	private void searchZones(List<Zone> zones, List<Zone> zoneIds, ZoneUser zu) {
		Optional<Zone> zoneOptional = zones.stream()
				.filter(i -> zu.getZoneCode().equals(i.getCode()) && zu.getLangCode().equals(i.getLangCode()))
				.findFirst();
		if (zoneOptional.isPresent()) {
			Zone zone = zoneOptional.get();
			if (!zoneIds.contains(zone))
				zoneIds.add(zone);
			List<Zone> zoneList = getDescedants(zones, zone);
			if (zoneList != null && !zoneList.isEmpty()) {
				zoneIds.addAll(zoneList);
			}
		}
	}

	public List<Zone> getUserZones() {
		List<Zone> zones = null;
		try {
			zones = zoneRepository.findAllNonDeleted();
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ZoneErrorCode.ZONE_FETCH_EXCEPTION.getErrorCode(),
					ZoneErrorCode.ZONE_FETCH_EXCEPTION.getErrorMessage());
		}

		if (zones != null && !zones.isEmpty()) {
			List<Zone> userZones = getUserZones(zones);
			List<String> zoneIds = userZones.stream().map(Zone::getCode).collect(Collectors.toList());
			return zones.stream().filter(i -> zoneIds.contains(i.getCode())).collect(Collectors.toList());
		}

		else
			return Collections.emptyList();
	}

	public List<Zone> getZones(Zone zone) {
		Objects.requireNonNull(zone, "zone cannot be null");
		Set<String> zoneList = new HashSet<>();
		List<Zone> zones = getUserZones();
		if (zones != null && !zones.isEmpty()) {
			zones.stream().filter(z -> z.getHierarchyPath().contains(zone.getCode())).map(Zone::getHierarchyPath)
					.forEach(i -> {
						int iIndex = i.lastIndexOf(zone.getCode());
						String szone = i.substring(iIndex);
						String[] sArray = szone.split("/");
						for (String zoneCode : sArray) {
							zoneList.add(zoneCode);
						}
					});
			return zones.stream().filter(z -> zoneList.contains(z.getCode())).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private List<Zone> getDescedants(List<Zone> zones, Zone zone) {
		getImmdChild(zones, zone);
		return list.get();
	}

	private void initialize(List<Zone> zones) {
		local = new ThreadLocal<>();
		local.set(zones);
		list = new ThreadLocal<>();
		list.set(new ArrayList<>());
	}

	private void getImmdChild(List<Zone> zones, Zone zone) {
		zones.stream().filter(child -> isChild(child, zone)).forEach(i -> {
			list.get().add(i);
			getImmdChild(local.get(), i);
		});
	}

	private boolean isChild(Zone child, Zone parent) {
		if (child.getParentZoneCode() != null)
			return child.getParentZoneCode().equals(parent.getCode())
					&& child.getLangCode().equals(parent.getLangCode());
		else
			return false;
	}

}
