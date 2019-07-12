package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.ZoneUser;
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
	public void testUserZone() {
		doReturn(zones).when(zoneRepository).findAllNonDeleted();
		doReturn(zoneUsers).when(zoneUserRepository).findByUserIdNonDeleted(Mockito.anyString());
		List<Zone> result = zoneUtils.getUserZones();
		assertNotNull(result);
		assertNotEquals(0, result.size());
	}

}
