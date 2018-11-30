package io.kernel.idrepo.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

import io.kernel.idrepo.controller.IdRepoController;
import io.kernel.idrepo.dto.IdRequestDTO;
import io.kernel.idrepo.dto.IdResponseDTO;
import io.kernel.idrepo.exception.IdRepoAppException;
import io.kernel.idrepo.service.IdRepoService;
import io.kernel.idrepo.validator.IdRequestValidator;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
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

	/** The controller. */
	@InjectMocks
	IdRepoController controller;

	/** The id repo service. */
	@Mock
	private IdRepoService idRepoService;

	/** The validator. */
	@Mock
	private IdRequestValidator validator;

	/** The uin validator. */
	@Mock
	private UinValidatorImpl uinValidator;

	/**
	 * Test add identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test
	public void testAddIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		when(idRepoService.addIdentity(any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity(request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException {
		IdRequestDTO request = new IdRequestDTO();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("errorCode");
		controller.addIdentity(request, errors);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test
	public void testRetrieveIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentity(any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentity("1234");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityInvalidUin() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidator.validateId(anyString())).thenReturn(false);
		when(idRepoService.retrieveIdentity(any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentity("1234");
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * Test retrieve identity null id.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNullId() throws IdRepoAppException {
		controller.retrieveIdentity(null);
	}

	/**
	 * Test retrieve identity invalid id.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityInvalidId() throws IdRepoAppException {
		when(uinValidator.validateId(anyString())).thenThrow(new InvalidIDException("errorCode", "errorMessage"));
		controller.retrieveIdentity("1234");
	}

	/**
	 * Test update identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test
	public void testUpdateIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		when(idRepoService.updateIdentity(any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.updateIdentity(request,
				new BeanPropertyBindingResult(request, "IdRequestDTO"));
		assertEquals(response, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	/**
	 * Test update identity exception.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityException() throws IdRepoAppException {
		IdRequestDTO request = new IdRequestDTO();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "IdRequestDTO");
		errors.reject("errorCode");
		controller.updateIdentity(request, errors);
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
}
