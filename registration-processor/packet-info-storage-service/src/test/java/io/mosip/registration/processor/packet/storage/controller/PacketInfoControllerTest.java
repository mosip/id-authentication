package io.mosip.registration.processor.packet.storage.controller;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.dto.PhotographDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

/**
 * The Class PacketInfoControllerTest.
 * 
 * @author M1047487
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PacketInfoControllerTest {

	/** The packet info controller. */
	@InjectMocks
	PacketInfoController packetInfoController = new PacketInfoController();

	/** The packet info manager. */
	@MockBean
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The webApplicationContext. */
	@Autowired
	private WebApplicationContext webApplicationContext;

	/** The audit log request builder. */
	@MockBean
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The reg osi dto. */
	@MockBean
	private RegOsiDto regOsiDto;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The packets. */
	List<ApplicantInfoDto> packets;

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		when(packetInfoManager.getPacketsforQCUser(ArgumentMatchers.any())).thenReturn(packets);
		packets = new ArrayList<>();

		ApplicantInfoDto infoDto = new ApplicantInfoDto();
		PhotographDto photographDto = new PhotographDto();
		photographDto.setRegId("2018782130000224092018121229");
		photographDto.setPreRegId("PEN1345T");
		photographDto.setNoOfRetry(4);
		photographDto.setHasExcpPhotograph(false);

		DemographicDedupeDto demoDto = new DemographicDedupeDto();
		List<DemographicDedupeDto> demoDedupeList = new ArrayList<>();
		demoDto.setRegId("2018782130000224092018121229");
		demoDto.setUin("PEN1345T");
		demoDto.setName("firstName");
		demoDto.setLangCode("ar");
		demoDedupeList.add(demoDto);

		infoDto.setDemoDedupeList(demoDedupeList);
		infoDto.setApplicantPhotograph(photographDto);
		packets.add(infoDto);

	}

	/**
	 * Gets the packets for QC user success test.
	 *
	 * @return the packets for QC user success test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getPacketsforQCUserSuccessTest() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.get("/v0.1/registration-processor/packet-info-storage-service/getexceptiondata")
						.param("qcuserId", "1234").accept(MediaType.ALL_VALUE).contentType(MediaType.ALL_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

}