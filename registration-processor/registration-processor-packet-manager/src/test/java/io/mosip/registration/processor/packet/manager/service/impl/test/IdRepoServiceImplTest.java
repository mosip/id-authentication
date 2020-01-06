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

/**
 * The Class IdRepoServiceImplTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class, IOUtils.class, JsonUtil.class })
public class IdRepoServiceImplTest {

	/** The rest client service. */
	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The id repo service. */
	@InjectMocks
	private IdRepoServiceImpl idRepoService;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
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

	/**
	 * Testget id json from ID repo.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testgetIdJsonFromIDRepo() throws Exception {
		JSONObject matchedDemographicIdentity = idRepoService.getIdJsonFromIDRepo("", "Identity");
		assertNull(matchedDemographicIdentity);

	}

	/**
	 * Testget UIN by RID.
	 *
	 * @throws Exception
	 *             the exception
	 */
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

	/**
	 * Testfind uin from idrepo.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testfindUinFromIdrepo() throws Exception {

		JSONObject demoJson = new JSONObject();
		demoJson.put("UIN", "1");
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "objectMapperReadValue", any(), any()).thenReturn(demoJson);
		PowerMockito.when(JsonUtil.class, "getJSONObject", any(), any()).thenReturn(demoJson);
		Number matchedDemographicIdentity = idRepoService.findUinFromIdrepo("", "Identity");
		assertNull(matchedDemographicIdentity);

	}
}
