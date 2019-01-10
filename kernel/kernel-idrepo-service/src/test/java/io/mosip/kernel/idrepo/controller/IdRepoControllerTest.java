package io.mosip.kernel.idrepo.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.spi.IdRepoService;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.IdResponseDTO;
import io.mosip.kernel.idrepo.entity.Uin;
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
@ConfigurationProperties("mosip.kernel.idrepo")
public class IdRepoControllerTest {

	private Map<String, String> id;

	@Mock
	private IdRepoService<IdRequestDTO, IdResponseDTO, Uin> idRepoService;

	@Mock
	private IdRequestValidator validator;

	@Mock
	private UinValidatorImpl uinValidatorImpl;

	@InjectMocks
	IdRepoController controller;

	@Mock
	HttpServletRequest request;

	@Before
	public void before() {
		ReflectionTestUtils.setField(controller, "id", id);
	}

	@Test
	public void testAddIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		IdRequestDTO request = new IdRequestDTO();
		when(idRepoService.addIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.addIdentity("1234", request,
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
		controller.addIdentity("1234", request, errors);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test
	@Ignore
	public void testRetrieveIdentity() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenReturn(true);
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.retrieveIdentity("1234", "demo", request);
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
	@Ignore
	public void testRetrieveIdentityInvalidUin() throws IdRepoAppException {
		IdResponseDTO response = new IdResponseDTO();
		when(uinValidatorImpl.validateId(anyString())).thenThrow(new InvalidIDException(null, null));
		when(idRepoService.retrieveIdentity(any(), any())).thenReturn(response);
		controller.retrieveIdentity("1234", "demo", request);
	}

	/**
	 * Test retrieve identity null id.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testRetrieveIdentityNullId() throws IdRepoAppException {
		controller.retrieveIdentity(null, null, request);
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
		when(idRepoService.updateIdentity(any(), any())).thenReturn(response);
		ResponseEntity<IdResponseDTO> responseEntity = controller.updateIdentity("1234", request,
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
		controller.updateIdentity("1234", request, errors);
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

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}
}
