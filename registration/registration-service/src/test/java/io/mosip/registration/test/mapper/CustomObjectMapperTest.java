package io.mosip.registration.test.mapper;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.DemographicInfo;
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
		demographicInfoDTO = demographicDTO.getDemographicInfoDTO();

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

	private void assertDemographicInfo(DemographicInfo demographicInfo) {
		assertEquals(demographicInfoDTO.getIdentity().getDateOfBirth(),
				demographicInfo.getIdentity().getDateOfBirth());
		assertEquals(demographicInfoDTO.getIdentity().getGender().get(0).getValue(),
				demographicInfo.getIdentity().getGender().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine1().get(0).getValue(),
				demographicInfo.getIdentity().getAddressLine1().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine2().get(0).getValue(),
				demographicInfo.getIdentity().getAddressLine2().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine3().get(0).getValue(),
				demographicInfo.getIdentity().getAddressLine3().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getRegion().get(0).getValue(),
				demographicInfo.getIdentity().getRegion().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getProvince().get(0).getValue(),
				demographicInfo.getIdentity().getProvince().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getCity().get(0).getValue(),
				demographicInfo.getIdentity().getCity().get(0).getValue());
		assertEquals(demographicInfoDTO.getIdentity().getEmail(),
				demographicInfo.getIdentity().getEmail());
		assertEquals(demographicInfoDTO.getIdentity().getPhone(),
				demographicInfo.getIdentity().getPhone());
	}

}
