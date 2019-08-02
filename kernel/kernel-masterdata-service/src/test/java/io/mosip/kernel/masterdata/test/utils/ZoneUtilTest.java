package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ZoneRepository;
import io.mosip.kernel.masterdata.repository.ZoneUserRepository;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ZoneUtilTest {

	@MockBean
	private ZoneRepository zoneRepository;

	@MockBean
	private ZoneUserRepository zoneUserRepository;

	@Autowired
	private ZoneUtils zoneUtils;

	List<Zone> zones;
	List<ZoneUser> zoneUsers;

	@Before
	public void setup() {
		zones = new ArrayList<>();
		zones.add(new Zone("AAA", "ENG", "AAA", (short) 0, "AAA", null, "AAA"));
		zones.add(new Zone("BBB", "ENG", "AAA", (short) 0, "BBB", "AAA", "AAA/BBB"));
		zones.add(new Zone("CCC", "ENG", "AAA", (short) 0, "CCC", "AAA", "AAA/CCC"));
		zones.add(new Zone("DDD", "ENG", "AAA", (short) 0, "DDD", "AAA", "AAA/DDD"));
		zones.add(new Zone("AAA1", "ENG", "AAA", (short) 0, "AAA1", "BBB", "AAA/BBB/AAA1"));
		zones.add(new Zone("AAA2", "ENG", "AAA", (short) 0, "AAA2", "CCC", "AAA/CCC/AAA2"));
		zones.add(new Zone("AAA3", "ENG", "AAA", (short) 0, "AAA3", "DDD", "AAA/DDD/AAA3"));
		zones.add(new Zone("AAA4", "ENG", "AAA", (short) 0, "AAA4", "AAA3", "AAA/DDD/AAA3/AAA4"));

		zoneUsers = new ArrayList<>();
		ZoneUser user = new ZoneUser();
		user.setUserId("zonal-admin");
		user.setZoneCode("AAA");
		user.setLangCode("ENG");
		zoneUsers.add(user);
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetZone() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		doReturn(zoneUsers).when(zoneUserRepository).findByUserIdNonDeleted(Mockito.anyString());
		Zone zone = new Zone();
		zone.setCode("DDD");
		List<Zone> result = zoneUtils.getZones(zone);
		assertNotNull(result);
		assertNotEquals(0, result.size());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void testGetZonesFailure() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		doReturn(zoneUsers).when(zoneUserRepository).findByUserIdNonDeleted(Mockito.anyString());
		List<Zone> result = zoneUtils.getUserZones();
		assertNotNull(result);
		assertNotEquals(0, result.size());
	}

	@Test(expected = MasterDataServiceException.class)
	@WithUserDetails("zonal-admin")
	public void testUserZoneFailure() {
		doThrow(DataRetrievalFailureException.class).when(zoneRepository).findAllNonDeleted();
		doReturn(zoneUsers).when(zoneUserRepository).findByUserIdNonDeleted(Mockito.anyString());
		zoneUtils.getUserZones();
	}

	@Test(expected = MasterDataServiceException.class)
	@WithUserDetails("zonal-admin")
	public void testUserZoneUserFailure() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		doThrow(DataRetrievalFailureException.class).when(zoneUserRepository)
				.findByUserIdNonDeleted(Mockito.anyString());
		zoneUtils.getUserZones();

	}

	@Test(expected = MasterDataServiceException.class)
	@WithUserDetails("zonal-admin")
	public void testUserZoneNotFound() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		List<Zone> result = zoneUtils.getUserZones();
		assertNotNull(result);
		assertNotEquals(0, result.size());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void testZonesNotFound() {
		List<Zone> result = zoneUtils.getUserZones();
		assertTrue(result.isEmpty());
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void testZoneLeafSuccess() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		doReturn(zoneUsers).when(zoneUserRepository).findByUserIdNonDeleted(Mockito.anyString());
		zoneUtils.getUserLeafZones("eng");
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void testZoneLeafNoUserZone() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		zoneUtils.getUserLeafZones("eng");
	}

	@WithUserDetails("zonal-admin")
	@Test
	public void testChildZoneNoZone() {
		zoneUtils.getZones(zones.get(0));
	}

}
