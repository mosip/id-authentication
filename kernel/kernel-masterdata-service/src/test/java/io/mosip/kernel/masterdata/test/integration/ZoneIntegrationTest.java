package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.test.TestBootApplication;
import io.mosip.kernel.masterdata.utils.ZoneUtils;

@SpringBootTest(classes = TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ZoneIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ZoneUtils zoneUtils;

	private List<Zone> zones;

	private List<Zone> leafsZones;

	@Before
	public void setup() {
		zones = new ArrayList<>();
		Zone zone1 = new Zone("ZONE1", "eng", "ZONE1 name", (short) 1, "hierarchy Name", null, "/ZONE1");
		Zone zone2 = new Zone("ZONE2", "eng", "ZONE2 name", (short) 2, "hierarchy Name", "ZONE1", "/ZONE1/ZONE2");
		Zone zone3 = new Zone("ZONE3", "eng", "ZONE3 name", (short) 3, "hierarchy Name", "ZONE2", "/ZONE1/ZONE2/ZONE3");
		Zone zone4 = new Zone("ZONE4", "eng", "ZONE4 name", (short) 4, "hierarchy Name", "ZONE1", "/ZONE1/ZONE4");
		Zone zone5 = new Zone("ZONE5", "eng", "ZONE5 name", (short) 4, "hierarchy Name", "ZONE1", "/ZONE1/ZONE5");
		zones.addAll(Arrays.asList(zone1, zone2, zone3, zone4, zone5));
		leafsZones = new ArrayList<>();
		leafsZones.addAll(Arrays.asList(zone3, zone4, zone5));
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getZoneHierarchySuccess() throws Exception {
		doReturn(zones).when(zoneUtils).getUserZones();
		mockMvc.perform(get("/zones/hierarchy/{langCode}", "eng")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getZoneHierarchyNoZones() throws Exception {
		mockMvc.perform(get("/zones/hierarchy/{langCode}", "eng")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getZoneLeafsSuccess() throws Exception {
		when(zoneUtils.getUserLeafZones(Mockito.anyString())).thenReturn(leafsZones);
		mockMvc.perform(get("/zones/leafs/{langCode}", "eng")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("zonal-admin")
	public void getZoneNoLeafs() throws Exception {
		mockMvc.perform(get("/zones/leafs/{langCode}", "eng")).andExpect(status().isOk());
	}

}
