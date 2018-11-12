package io.mosip.registration.test.mapper;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.mosip.kernel.core.util.exception.MosipJsonProcessingException;
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
	public void convertTest() throws MosipJsonProcessingException {
		PacketMetaInfo packetMetaInfo = mapperFacade.convert(registrationDTO, PacketMetaInfo.class, "packetMetaInfo");
		Assert.assertNotNull(packetMetaInfo.getIdentity().getLeftEye());
		Assert.assertNull(packetMetaInfo.getIdentity().getRightEye());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getApplicantPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionPhotograph());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getExceptionBiometrics());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getRightSlap());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getLeftSlap());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getThumbs());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getParentFingerprint());
		Assert.assertNull(packetMetaInfo.getIdentity().getParentIris());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getMetaData());
		Assert.assertNotNull(packetMetaInfo.getIdentity().getOsiData());
	}
}
