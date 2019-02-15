package io.mosip.registration.processor.packet.storage.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
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
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The audit log request builder. */
	@Mock
	AuditLogRequestBuilder auditLogRequestBuilder;

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
		
		
		packets = new ArrayList<>();

		ApplicantInfoDto infoDto = new ApplicantInfoDto();
		PhotographDto photographDto = new PhotographDto();
		photographDto.setRegId("2018782130000224092018121229");
		photographDto.setPreRegId("PEN1345T");
		photographDto.setNoOfRetry(4);
		photographDto.setHasExcpPhotograph(false);

		DemographicInfoDto demoDto = new DemographicInfoDto();
		List<DemographicInfoDto> demoDedupeList = new ArrayList<>();
		demoDto.setRegId("2018782130000224092018121229");
		demoDto.setUin("PEN1345T");
		demoDto.setName("firstName");
		demoDto.setLangCode("ar");
		demoDedupeList.add(demoDto);

		infoDto.setDemoDedupeList(demoDedupeList);
		infoDto.setApplicantPhotograph(photographDto);
		packets.add(infoDto);
		when(packetInfoManager.getPacketsforQCUser(any())).thenReturn(packets);
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