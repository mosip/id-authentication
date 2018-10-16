package io.mosip.registration.test.mapper;

import org.junit.Test;

import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.json.demo.Demographic;
import io.mosip.registration.dto.json.demo.DemographicInfo;
import io.mosip.registration.dto.json.metadata.PacketInfo;
import io.mosip.registration.dto.json.metadata.Photograph;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;
import ma.glasnost.orika.MapperFacade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.OffsetDateTime;

import org.junit.Before;

public class CustomObjectMapperTest {

	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	private RegistrationDTO registrationDTO;
	private DemographicInfoDTO demographicInfoDTO;
	private DemographicDTO demographicDTO;

	@Before
	public void initialize() throws RegBaseCheckedException {
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
	public void testPacketINfoConversion() {
		PacketInfo packetInfo = mapperFacade.map(registrationDTO, PacketInfo.class);
		Photograph photograph = packetInfo.getPhotograph();
		ApplicantDocumentDTO applicantDocumentDTO = registrationDTO.getDemographicDTO().getApplicantDocumentDTO();
		assertEquals(applicantDocumentDTO.getPhotographName(), photograph.getPhotographName());
		assertEquals(applicantDocumentDTO.isHasExceptionPhoto(), photograph.isHasExceptionPhoto());
		assertEquals(applicantDocumentDTO.getExceptionPhotoName(), photograph.getExceptionPhotoName());
		assertEquals(applicantDocumentDTO.getQualityScore(), photograph.getQualityScore(), 0.0);
		assertEquals(applicantDocumentDTO.getNumRetry(), photograph.getNumRetry());
		assertEquals(applicantDocumentDTO.getDocumentDetailsDTO().size(),
				packetInfo.getDocument().getDocumentDetails().size());
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
		assertEquals(demographicInfoDTO.getFirstName(), demographicInfo.getFirstName());
		assertEquals(demographicInfoDTO.getMiddleName(), demographicInfo.getMiddleName());
		assertEquals(demographicInfoDTO.getLastName(), demographicInfo.getLastName());
		assertEquals(demographicInfoDTO.getDateOfBirth(), demographicInfo.getDateOfBirth());
		assertEquals(demographicInfoDTO.getGender(), demographicInfo.getGender());
		assertEquals(demographicInfoDTO.getAddressDTO().getLine1(), demographicInfo.getAddressDTO().getLine1());
		assertEquals(demographicInfoDTO.getAddressDTO().getLine2(), demographicInfo.getAddressDTO().getLine2());
		assertNull(demographicInfo.getAddressDTO().getLine3());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getLine4(),
				demographicInfo.getAddressDTO().getLocationDTO().getLine4());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getLine5(),
				demographicInfo.getAddressDTO().getLocationDTO().getLine5());
		assertEquals(demographicInfoDTO.getAddressDTO().getLocationDTO().getLine6(),
				demographicInfo.getAddressDTO().getLocationDTO().getLine6());
		assertEquals(demographicInfoDTO.getEmailId(), demographicInfo.getEmailId());
		assertEquals(demographicInfoDTO.getMobile(), demographicInfo.getMobile());
		assertEquals(demographicInfoDTO.isChild(), demographicInfo.isChild());
	}
}
