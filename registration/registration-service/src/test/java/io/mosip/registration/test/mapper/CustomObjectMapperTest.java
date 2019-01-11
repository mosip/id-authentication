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
		assertEquals(demographicInfoDTO.getIdentity().getDateOfBirth().getValue(),
				demographicInfo.getIdentity().getDateOfBirth().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getGender().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getGender().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine1().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getAddressLine1().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine2().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getAddressLine2().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getAddressLine3().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getAddressLine3().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getRegion().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getRegion().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getProvince().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getProvince().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getCity().getValues().getFirst().getValue(),
				demographicInfo.getIdentity().getCity().getValues().getFirst().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getEmail().getValue(),
				demographicInfo.getIdentity().getEmail().getValue());
		assertEquals(demographicInfoDTO.getIdentity().getPhone().getValue(),
				demographicInfo.getIdentity().getPhone().getValue());
	}

}
