package io.mosip.idrepository.identity.test.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.RequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.IdRepoService;
import io.mosip.idrepository.identity.controller.IdRepoController;
import io.mosip.idrepository.identity.validator.IdRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

/**
 * The Class IdRepoControllerTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoControllerTest {

	@Mock
	private IdRepoService<IdRequestDTO, IdResponseDTO> idRepoService;

	@InjectMocks
	private IdRequestValidator validator;

	@Mock
	private UinValidatorImpl uinValidator;

	@Mock
	private RidValidatorImpl ridValidator;

	@InjectMocks
	IdRepoController controller;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private Environment env;

	@Before
	public void before() {
		Map<String, String> id = Maps.newHashMap("read", "mosip.id.read");
		id.put("create", "mosip.id.create");
		id.put("update", "mosip.id.update");
		ReflectionTestUtils.setField(controller, "id", id);
		ReflectionTestUtils.setField(controller, "mapper", mapper);
		ReflectionTestUtils.setField(controller, "validator", validator);
		ReflectionTestUtils.setField(controller, "env", env);
		ReflectionTestUtils.setField(validator, "id", id);

		ReflectionTestUtils.setField(validator, "allowedTypes", Lists.newArrayList("bio", "demo", "all"));
	}

	@Test
	public void testAddIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		when(idRepoService.addIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity(request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityFailed()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.creat");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		when(idRepoService.addIdentity(any(), any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR));
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity(request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException   the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException(null, null));
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("errorCode");
		controller.addIdentity(request, errors);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test
	public void testRetrieveIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentityByUin(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentityByUin("1234", "demo");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testRetrieveIdentityAll() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentityByUin(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentityByUin("1234", "demo,all");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityInvalidUin() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentityByUin(any(), any())).thenReturn(response);
		controller.retrieveIdentityByUin("1234", "demo");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityRequestParameterMap() throws IdRepoAppException {
		Map<String, String[]> paramMap = new HashMap<>();
		paramMap.put("k", new String[] { "v" });
		controller.retrieveIdentityByUin("1234", "dem, abc");
	}

	/**
	 * Test init binder.
	 */
	@Test
	public void testInitBinder() {
		ReflectionTestUtils.setField(controller, "validator", new IdRequestValidator());
		WebDataBinder binder = new WebDataBinder(new IdRequestDTO());
		controller.initBinder(binder);
	}

	@Test
	public void updateIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		controller.updateIdentity(request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void updateIdentityInvalidId()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(any())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		controller.updateIdentity(request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void updateIdentityIdRepoDataValidationException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("");
		controller.updateIdentity(request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityFailed()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		request.setRequest(requestDTO);
		when(idRepoService.updateIdentity(any(), any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR));
		ResponseEntity<IdResponseDTO> responseEntity = controller.updateIdentity(request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testRetrieveIdentityByRid() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentityByRid("1234", "demo");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testRetrieveIdentityByRidAll() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentityByRid("1234", "demo,all");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityByRidInvalidUin() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(null)).thenThrow(new InvalidIDException(null, null));
		when(ridValidator.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		try {
			when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
			controller.retrieveIdentityByRid("1234", "demo");
		} catch (IdRepoAppException e) {
			throw new IdRepoAppException(IdRepoErrorConstants.INVALID_UIN.getErrorCode(),
					String.format(IdRepoErrorConstants.INVALID_UIN.getErrorMessage()));
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityByRidMultipleInvalidType() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(ridValidator.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
		controller.retrieveIdentityByRid("1234", "dem, abc");
	}

	@Test
	public void testRetrieveIdentityByRidInvalidType() throws Throwable {
		try {
			IdResponseDTO response = new IdResponseDTO();
			when(ridValidator.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
			when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
			controller.retrieveIdentityByRid("1234", "dem");
		} catch (IdRepoAppException e) {
			assertEquals("KER-IDR-002 --> Invalid Input Parameter - type", e.getCause().getMessage());
		}
	}

	@Test
	public void testRetrieveIdentityByRidMultipleValidType() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(ridValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentityByRid(any(), any())).thenReturn(response);
		controller.retrieveIdentityByRid("1234", "demo,all,bio");
	}

	@Test
	public void testRetrieveIdentityByRidNullId() throws Throwable {
		try {
			when(ridValidator.validateId(null))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_REGISTRATION_ID.getErrorCode(),
							IdRepoErrorConstants.INVALID_REGISTRATION_ID.getErrorMessage()));
			controller.retrieveIdentityByRid(null, null);
		} catch (IdRepoAppException e) {
			assertEquals("KER-IDR-021 --> Invalid Registration ID", e.getCause().getMessage());
		}
	}

	@Test
	public void testGetUin_valid() throws JsonParseException, JsonMappingException, IOException {
		String uin = "6743571690";
		RequestDTO requestDTO = new RequestDTO();
		Object identity = mapper.readValue(
				"{\"UIN\":6743571690,\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		requestDTO.setIdentity(identity);
		String uinOutPut = ReflectionTestUtils.invokeMethod(controller, "getUin", requestDTO);
		assertEquals(uin, uinOutPut);
	}

	@Test
	public void testGetUin_missingInputUin() throws Throwable {
		RequestDTO requestDTO = new RequestDTO();
		Object identity;
		try {
			identity = mapper.readValue(
					"{\"dateOfBirth\":\"12345\",\"fullName\":[{\"language\":\"ARA\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes(),
					Object.class);
			requestDTO.setIdentity(identity);
			ReflectionTestUtils.invokeMethod(controller, "getUin", requestDTO);
		} catch (UndeclaredThrowableException e) {
			assertEquals("KER-IDR-001 --> Missing Input Parameter - /identity/UIN", e.getCause().getMessage());
		}
	}

	@Test
	public void testGetUin_JsonProcessingException() throws Throwable {
		try {
			ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
			when(mockMapper.writeValueAsString(Mockito.any()))
					.thenThrow(new JsonProcessingException(IdRepoErrorConstants.INVALID_REQUEST.getErrorMessage()) {
					});
			ReflectionTestUtils.setField(controller, "mapper", mockMapper);
			ReflectionTestUtils.invokeMethod(controller, "getUin", "");
		} catch (UndeclaredThrowableException e) {
			assertEquals("KER-IDR-007 --> Invalid Request; \n"
					+ "nested exception is io.mosip.idrepository.identity.test.controller.IdRepoControllerTest$1: Invalid Request",
					e.getCause().getMessage());
		}

	}

}
