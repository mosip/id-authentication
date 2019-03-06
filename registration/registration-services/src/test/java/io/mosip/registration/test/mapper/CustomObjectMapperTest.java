package io.mosip.registration.test.mapper;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.MoroccoIdentity;
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
		assertDemographicInfo(mapperFacade.map(demographicInfoDTO.getIdentity(), MoroccoIdentity.class));
	}

	private void assertDemographicInfo(MoroccoIdentity actualIdentity) {
		MoroccoIdentity expectedIdentity = (MoroccoIdentity) demographicInfoDTO.getIdentity();
		assertEquals(expectedIdentity.getDateOfBirth(), actualIdentity.getDateOfBirth());
		assertEquals(expectedIdentity.getGender().get(0).getValue(), actualIdentity.getGender().get(0).getValue());
		assertEquals(expectedIdentity.getAddressLine1().get(0).getValue(),
				actualIdentity.getAddressLine1().get(0).getValue());
		assertEquals(expectedIdentity.getAddressLine2().get(0).getValue(),
				actualIdentity.getAddressLine2().get(0).getValue());
		assertEquals(expectedIdentity.getAddressLine3().get(0).getValue(),
				actualIdentity.getAddressLine3().get(0).getValue());
		assertEquals(expectedIdentity.getRegion().get(0).getValue(), actualIdentity.getRegion().get(0).getValue());
		assertEquals(expectedIdentity.getProvince().get(0).getValue(), actualIdentity.getProvince().get(0).getValue());
		assertEquals(expectedIdentity.getCity().get(0).getValue(), actualIdentity.getCity().get(0).getValue());
		assertEquals(expectedIdentity.getResidenceStatus().get(0).getValue(), actualIdentity.getResidenceStatus().get(0).getValue());
		assertEquals(expectedIdentity.getEmail(), actualIdentity.getEmail());
		assertEquals(expectedIdentity.getPhone(), actualIdentity.getPhone());
	}

}
