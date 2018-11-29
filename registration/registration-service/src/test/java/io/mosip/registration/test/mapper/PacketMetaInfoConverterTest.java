package io.mosip.registration.test.mapper;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mapper.CustomObjectMapper;
import io.mosip.registration.test.util.datastub.DataProvider;
import ma.glasnost.orika.MapperFacade;

public class PacketMetaInfoConverterTest {
	
	private MapperFacade mapperFacade = CustomObjectMapper.MAPPER_FACADE;
	private static RegistrationDTO registrationDTO;

	@BeforeClass
	public static void initialize() throws RegBaseCheckedException {
		// RegistrationDTO
		registrationDTO = DataProvider.getPacketDTO();
	}
	
	@Test
	public void convertTest() throws JsonProcessingException {
		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightEye());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getRightSlap());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getLeftSlap());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getApplicant().getThumbs());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerIris());
		Assert.assertNull(packetMetaInfo.getIdentity().getBiometric().getIntroducer().getIntroducerImage());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getCheckSum());
	}
}
