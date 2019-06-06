package io.mosip.registration.processor.packet.manager.service.impl.test;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;

import javax.swing.text.Utilities;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.ResponseDTO;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.IdentityJsonValues;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.impl.IdRepoServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, IOUtils.class, JsonUtil.class })
public class IdRepoServiceImplTest {

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@InjectMocks
	private IdRepoServiceImpl idRepoService;

	@Before
	public void setUp() throws Exception {

		IdentityJsonValues jv = new IdentityJsonValues();
		jv.setValue("1");

		Identity identity = new Identity();
		identity.setAge(jv);

		ResponseDTO rdto = new ResponseDTO();
		rdto.setIdentity(identity);

		IdResponseDTO dto = new IdResponseDTO();
		dto.setResponse(rdto);

		ResponseWrapper<IdResponseDTO> response = new ResponseWrapper();

		response.setId("1");
		response.setResponse(dto);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(response);

	}

	@Test
	public void testgetIdJsonFromIDRepo() throws Exception {
		JSONObject matchedDemographicIdentity = idRepoService.getIdJsonFromIDRepo("", "Identity");
		assertNull(matchedDemographicIdentity);

	}

	@Test
	public void testgetUINByRID() throws Exception {
		JSONObject demoJson = new JSONObject();
		demoJson.put("UIN", "1");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", any(), any()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", any(), any()).thenReturn(demoJson);
		Number matchedDemographicIdentity = idRepoService.getUinByRid("", "Identity");
		assertNull(matchedDemographicIdentity);

	}
}
