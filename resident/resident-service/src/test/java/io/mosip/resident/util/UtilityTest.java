package io.mosip.resident.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.IdRepoResponseDto;
import io.mosip.resident.dto.VidGeneratorResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.IdRepoAppException;
import io.mosip.resident.exception.ResidentServiceCheckedException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class })
public class UtilityTest {
	@Mock
	private ResidentServiceRestClient residentServiceRestClient;

	@Mock
	private TokenGenerator tokenGenerator;

	@InjectMocks
	private Utilitiy utility;
	private JSONObject identity;

	@Before
	public void setUp() throws IOException, ApisResourceAccessException {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJson = new File(classLoader.getResource("ID.json").getFile());
		InputStream is = new FileInputStream(idJson);
		String idJsonString = IOUtils.toString(is, "UTF-8");
		identity = JsonUtil.objectMapperReadValue(idJsonString, JSONObject.class);
		ResponseWrapper<IdRepoResponseDto> response = new ResponseWrapper<>();
		IdRepoResponseDto idRepoResponseDto = new IdRepoResponseDto();
		idRepoResponseDto.setStatus("Activated");
		idRepoResponseDto.setIdentity(JsonUtil.getJSONObject(identity, "identity"));
		response.setResponse(idRepoResponseDto);
		Mockito.when(residentServiceRestClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenReturn(response);
		Mockito.when(tokenGenerator.getToken()).thenReturn("abcdefghijklmn");
		ReflectionTestUtils.setField(utility, "configServerFileStorageURL", "url");
		ReflectionTestUtils.setField(utility, "getRegProcessorIdentityJson", "json");
		ReflectionTestUtils.setField(utility, "languageType", "BOTH");
		ReflectionTestUtils.setField(utility, "primaryLang", "eng");
		ReflectionTestUtils.setField(utility, "secondaryLang", "ara");

	}

	@Test
	public void retrieveIdrepoJsonSuccessTest()
			throws ResidentServiceCheckedException, IOException, ApisResourceAccessException {
		// UIN
		JSONObject identityJsonObj = utility.retrieveIdrepoJson("3527812406", IdType.UIN);
		assertEquals(identityJsonObj.get("UIN"), JsonUtil.getJSONObject(identity, "identity").get("UIN"));
		// RID
		JSONObject jsonUsingRID = utility.retrieveIdrepoJson("10008200070004420191203104356", IdType.RID);
		assertEquals(jsonUsingRID.get("UIN"), JsonUtil.getJSONObject(identity, "identity").get("UIN"));
		// VID
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add("5628965106742572");
		VidGeneratorResponseDto vidResponse = new VidGeneratorResponseDto();
		vidResponse.setUIN("3527812406");
		vidResponse.setVidStatus("Active");
		ResponseWrapper<VidGeneratorResponseDto> response = new ResponseWrapper<>();
		response.setResponse(vidResponse);
		Mockito.when(residentServiceRestClient.getApi(ApiName.GETUINBYVID, pathsegments, null, null,
				ResponseWrapper.class, "abcdefghijklmn")).thenReturn(response);
		JSONObject jsonUsingVID = utility.retrieveIdrepoJson("5628965106742572", IdType.VID);
		assertEquals(jsonUsingVID.get("UIN"), JsonUtil.getJSONObject(identity, "identity").get("UIN"));

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void retrieveIdrepoJsonClientError() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpClientErrorException clientExp = new HttpClientErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", clientExp);
		Mockito.when(residentServiceRestClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		utility.retrieveIdrepoJson("3527812406", IdType.UIN);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void retrieveIdrepoJsonServerError() throws ApisResourceAccessException, ResidentServiceCheckedException {
		HttpServerErrorException serverExp = new HttpServerErrorException(HttpStatus.BAD_GATEWAY);
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway", serverExp);
		Mockito.when(residentServiceRestClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		utility.retrieveIdrepoJson("3527812406", IdType.UIN);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void retrieveIdrepoJsonUnknownException()
			throws ApisResourceAccessException, ResidentServiceCheckedException {
		ApisResourceAccessException apiResourceAccessExp = new ApisResourceAccessException("BadGateway",
				new RuntimeException());
		Mockito.when(residentServiceRestClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenThrow(apiResourceAccessExp);
		utility.retrieveIdrepoJson("3527812406", IdType.UIN);

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void tokenGeneratorException()
			throws ApisResourceAccessException, ResidentServiceCheckedException, IOException {
		Mockito.when(tokenGenerator.getToken()).thenThrow(new IOException());
		utility.retrieveIdrepoJson("3527812406", IdType.UIN);

	}

	@Test(expected = IdRepoAppException.class)
	public void testIdRepoAppException() throws ApisResourceAccessException, ResidentServiceCheckedException {
		Mockito.when(residentServiceRestClient.getApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(Class.class), Mockito.any())).thenReturn(null);
		utility.retrieveIdrepoJson("3527812406", IdType.UIN);

	}

	@Test(expected = IdRepoAppException.class)
	public void vidResponseNull() throws ApisResourceAccessException, ResidentServiceCheckedException {
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add("5628965106742572");
		Mockito.when(residentServiceRestClient.getApi(ApiName.GETUINBYVID, pathsegments, null, null,
				ResponseWrapper.class, "abcdefghijklmn")).thenReturn(null);
		utility.retrieveIdrepoJson("5628965106742572", IdType.VID);

	}

	@Test
	public void testGetMailingAttributes() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJson = new File(classLoader.getResource("IdentityMapping.json").getFile());
		InputStream is = new FileInputStream(idJson);
		String mappingJson = IOUtils.toString(is, "UTF-8");
		Utilitiy utilitySpy = Mockito.spy(utility);
		Mockito.doReturn(mappingJson).when(utilitySpy).getMappingJson();
		Map<String, Object> attributes = utilitySpy.getMailingAttributes("3527812406", IdType.UIN);
		assertEquals("girish.yarru@mindtree.com", attributes.get("email"));

		ReflectionTestUtils.setField(utilitySpy, "languageType", "NA");
		Map<String, Object> attributes1 = utilitySpy.getMailingAttributes("3527812406", IdType.UIN);
		assertEquals("girish.yarru@mindtree.com", attributes1.get("email"));

	}

	@Test(expected = ResidentServiceCheckedException.class)
	public void testGetMailingAttributesIOException() throws IOException, ResidentServiceCheckedException {
		ClassLoader classLoader = getClass().getClassLoader();
		File idJson = new File(classLoader.getResource("IdentityMapping.json").getFile());
		InputStream is = new FileInputStream(idJson);
		String mappingJson = IOUtils.toString(is, "UTF-8");
		Utilitiy utilitySpy = Mockito.spy(utility);
		Mockito.doReturn(mappingJson).when(utilitySpy).getMappingJson();
		Mockito.doReturn(JsonUtil.getJSONObject(identity, "identity")).when(utilitySpy)
				.retrieveIdrepoJson(Mockito.anyString(), Mockito.any());
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.objectMapperReadValue(mappingJson, JSONObject.class)).thenThrow(new IOException());
		utilitySpy.getMailingAttributes("3527812406", IdType.UIN);

	}
}
