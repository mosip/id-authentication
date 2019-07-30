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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

	@Autowired
	@Qualifier("zoneTree")
	private UBtree<Zone> zoneTree;

	@Value("mosip.kernel.masterdata.zone-heirarchy-path-delimiter:/")
	private String hierarchyPathDelimiter;

	/**
	 * Method to get the all the users zones based on the passed list of zone and
	 * will fetch all the child hierarchy.
	 * 
	 * @param zones
	 *            input to search the users zones
	 * @return list of zones
	 */
	public List<Zone> getUserZones(List<Zone> zones) {
		List<Zone> zoneIds = new ArrayList<>();
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		List<ZoneUser> userZones = getZoneUser(userName);
		if (userZones != null && !userZones.isEmpty()) {
			for (ZoneUser zu : userZones) {
				searchZones(zones, zoneIds, zu);
			}
		} else {
			throw new MasterDataServiceException(ZoneErrorCode.USER_ZONE_UNAVAILABLE.getErrorCode(),
					String.format(ZoneErrorCode.USER_ZONE_UNAVAILABLE.getErrorMessage(), userName));
		}
		return zoneIds;
	}

	/**
	 * Method to search the all the child zones as per input.
	 * 
	 * @param zones
	 *            list of zones
	 * @param zoneIds
	 *            zone id's to be searched
	 * @param zu
	 *            zone user
	 */
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

	/**
	 * Method to fetch the users zone as well as all the child zones.
	 * 
	 * @return list of zones
	 */
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

	/**
	 * Method to fetch all the child zones of the passed zone.
	 * 
	 * @param zone
	 *            input for which search the child zones
	 * @return list of zones
	 */
	public List<Zone> getZones(Zone zone) {
		Objects.requireNonNull(zone, "zone cannot be null");
		Set<String> zoneList = new HashSet<>();
		List<Zone> zones = getUserZones();
		if (zones != null && !zones.isEmpty()) {
			zones.stream().filter(z -> z.getHierarchyPath().contains(zone.getCode())).map(Zone::getHierarchyPath)
					.forEach(i -> {
						int iIndex = i.lastIndexOf(zone.getCode());
						String szone = i.substring(iIndex);
						String[] sArray = szone.split(hierarchyPathDelimiter);
						for (String zoneCode : sArray) {
							zoneList.add(zoneCode);
						}
					});
			return zones.stream().filter(z -> zoneList.contains(z.getCode())).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * Method to fetch the all the child zones as per the input
	 * 
	 * @param zones
	 *            input list of zone to search from
	 * @param zone
	 *            zone to be search
	 * @return list of zones
	 */
	private List<Zone> getDescedants(List<Zone> zones, Zone zone) {
		List<Zone> zoneList = zones.stream().filter(i -> i.getLangCode().equals(zone.getLangCode()))
				.collect(Collectors.toList());
		List<Node<Zone>> tree = zoneTree.createTree(zoneList);
		Node<Zone> node = zoneTree.findNode(tree, zone.getCode());
		return zoneTree.getChildHierarchy(node);
	}

	/**
	 * Method to fetch the user's zone
	 * 
	 * @param userName
	 *            input username
	 * @return {@link List} of {@link ZoneUser}
	 */
	private List<ZoneUser> getZoneUser(String userName) {
		List<ZoneUser> userZone = null;
		try {
			userZone = zoneUserRepository.findByUserIdNonDeleted(userName);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(ZoneErrorCode.USER_ZONE_FETCH_EXCEPTION.getErrorCode(),
					ZoneErrorCode.USER_ZONE_FETCH_EXCEPTION.getErrorMessage());
		}
		return userZone;
	}

	/**
	 * Method to fetch the user's zone hierarchy leaf zones
	 * 
	 * @param langCode
	 *            input language code
	 * @return {@link List} of {@link Zone}
	 */
	public List<Zone> getUserLeafZones(String langCode) {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		List<ZoneUser> userZones = getZoneUser(userName);
		if (userZones != null && !userZones.isEmpty()) {
			Optional<String> zoneId = userZones.stream().map(ZoneUser::getZoneCode).findFirst();
			if (zoneId.isPresent()) {
				List<Zone> zones = getUserZones();
				List<Zone> langSpecificZones = zones.stream().filter(i -> i.getLangCode().equals(langCode))
						.collect(Collectors.toList());
				List<Node<Zone>> tree = zoneTree.createTree(langSpecificZones);
				Node<Zone> node = zoneTree.findNode(tree, zoneId.get());
				return zoneTree.findLeafsValue(node);
			}
		}
		return Collections.emptyList();
	}

}
