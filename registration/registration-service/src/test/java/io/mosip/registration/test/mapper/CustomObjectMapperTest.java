package io.mosip.registration.test.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.OffsetDateTime;

import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.demo.DemographicInfo;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;
import ma.glasnost.orika.MapperFacade;

public class CustomObjectMapperTest {

	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	private static RegistrationDTO registrationDTO;
	private static DemographicInfoDTO demographicInfoDTO;
	private static DemographicDTO demographicDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		// RegistrationDTO
		registrationDTO = DataProvider.getPacketDTO();

		// Set DemographicDTO
		demographicDTO = registrationDTO.getDemographicDTO();

		// DemographicInfoDTO
		demographicInfoDTO = demographicDTO.getDemoInLocalLang();
	}

	@Test
	public void testOffsetDateTimeConversion() {
		OffsetDateTime time = OffsetDateTime.now();
		OffsetDateTime convertedTime = mapperFacade.map(time, OffsetDateTime.class);
		assertEquals(time, convertedTime);
	}

	@Test
	public void testDemographicInfoConversion() {
		assertDemographicInfo(mapperFacade.map(demographicInfoDTO, DemographicInfo.class));
	}

	@Test
	public void testDemographicConversion() {
		Demographic demographic = mapperFacade.map(demographicDTO, Demographic.class);
		assertDemographicInfo(demographic.getDemoInLocalLang());
		assertDemographicInfo(demographic.getDemoInUserLang());
	}

	private void assertDemographicInfo(DemographicInfo demographicInfo) {
		assertEquals(demographicInfoDTO.getDateOfBirth(), demographicInfo.getDateOfBirth());
		assertEquals(demographicInfoDTO.getGender(), demographicInfo.getGender());
		assertEquals(demographicInfoDTO.getAddressDTO().getAddressLine1(), demographicInfo.getAddressDTO().getAddressLine1());
		assertEquals(demographicInfoDTO.getAddressDTO().getAddressLine2(), demographicInfo.getAddressDTO().getAddressLine2());
		assertNull(demographicInfo.getAddressDTO().getAddressLine3());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getRegion(),
				demographicInfo.getAddressDTO().getLocationDTO().getRegion());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getProvince(),
				demographicInfo.getAddressDTO().getLocationDTO().getProvince());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getCity(),
				demographicInfo.getAddressDTO().getLocationDTO().getCity());
		assertEquals(demographicInfoDTO.getEmailId(), demographicInfo.getEmailId());
		assertEquals(demographicInfoDTO.getMobile(), demographicInfo.getMobile());
		assertEquals(demographicInfoDTO.isChild(), demographicInfo.isChild());
	}

}
