package io.mosip.kernel.idrepo.test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;

import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.core.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idrepo.controller.IdRepoController;
import io.mosip.kernel.idrepo.validator.IdRequestValidator;
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

	@Mock
	private IdRequestValidator validator;

	@Mock
	private UinValidatorImpl uinValidatorImpl;

	@InjectMocks
	IdRepoController controller;

	@Before
	public void before() {
		Map<String, String> id = Maps.newHashMap("read", "mosip.id.read");
		id.put("create", "mosip.id.create");
		id.put("update", "mosip.id.update");
		ReflectionTestUtils.setField(controller, "id", id);
		ReflectionTestUtils.setField(controller, "allowedTypes", Lists.newArrayList("bio", "demo", "all"));
	}

	@Test
	public void testAddIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		when(idRepoService.addIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity("1234", request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityFailed() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		when(idRepoService.addIdentity(any(), any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR));
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity("1234", request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException {
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("errorCode");
		controller.addIdentity("1234", request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityExceptionInvalidUin() throws IdRepoAppException {
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.create");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		when(uinValidatorImpl.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		controller.addIdentity("1234", request, errors);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test
	public void testRetrieveIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentity("1234", "demo");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testRetrieveIdentityAll() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentity("1234", "demo,all");
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
		when(uinValidatorImpl.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		controller.retrieveIdentity("1234", "demo");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityInvalidType() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		controller.retrieveIdentity("1234", "dem");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityMultipleInvalidType() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		controller.retrieveIdentity("1234", "dem, abc");
	}

	@Test
	public void testRetrieveIdentityMultipleValidType() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		controller.retrieveIdentity("1234", "demo,all,bio");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityRequestParameterMap() throws IdRepoAppException {
		Map<String, String[]> paramMap = new HashMap<>();
		paramMap.put("k", new String[] { "v" });
		controller.retrieveIdentity("1234", "dem, abc");
	}

	/**
	 * Test retrieve identity null id.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNullId() throws IdRepoAppException {
		when(uinValidatorImpl.validateId(any())).thenThrow(new InvalidIDException(null, null));
		controller.retrieveIdentity(null, null);
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
	public void updateIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		controller.updateIdentity("1234", request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void updateIdentityInvalidId() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(any())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		controller.updateIdentity("1234", request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void updateIdentityIdRepoDataValidationException() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("");
		controller.updateIdentity("1234", request, errors);
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityFailed() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		request.setId("mosip.id.update");
		when(idRepoService.updateIdentity(any(), any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.UNKNOWN_ERROR));
		ResponseEntity<IdResponseDTO> responseEntity = controller.updateIdentity("1234", request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testValidateIdNullId() {
		IdRequestDTO request = new IdRequestDTO();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		ReflectionTestUtils.invokeMethod(controller, "validateId", null, errors, "read");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), "id"),
					error.getDefaultMessage());
			assertEquals("id", ((FieldError) error).getField());
		});
	}

	@Test
	public void testValidateIdInvalidId() {
		IdRequestDTO request = new IdRequestDTO();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		ReflectionTestUtils.invokeMethod(controller, "validateId", "abc", errors, "read");
		assertTrue(errors.hasErrors());
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "id"),
					error.getDefaultMessage());
			assertEquals("id", ((FieldError) error).getField());
		});
	}
}
