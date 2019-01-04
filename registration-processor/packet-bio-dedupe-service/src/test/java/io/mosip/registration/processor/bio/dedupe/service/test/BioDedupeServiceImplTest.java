package io.mosip.registration.processor.bio.dedupe.service.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.bio.dedupe.service.impl.BioDedupeServiceImpl;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;

@RefreshScope
@RunWith(PowerMockRunner.class)
public class BioDedupeServiceImplTest {

	@Mock
	RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	PacketInfoManagerImpl packetInfoManagerImpl;

	@Mock
	AbisInsertResponceDto abisInsertResponceDto = new AbisInsertResponceDto();

	@InjectMocks
	BioDedupeServiceImpl bioDedupeService = new BioDedupeServiceImpl();

	@Test
	public void insertBiometricsTest() throws ApisResourceAccessException {

		Mockito.doNothing().when(packetInfoManagerImpl).saveAbisRef(any());

		abisInsertResponceDto.setReturnValue("success");
		Mockito.when(restClientService.postApi(any(), anyString(), anyString(), anyString(), any()))
				.thenReturn(abisInsertResponceDto);

		String registrationId = "1000";
		String authResponse = bioDedupeService.insertBiometrics(registrationId);
		assertTrue(authResponse.equals("success"));

	}

}
