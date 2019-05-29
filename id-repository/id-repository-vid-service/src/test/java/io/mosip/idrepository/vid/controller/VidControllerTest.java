package io.mosip.idrepository.vid.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.VidRequestDTO;
import io.mosip.idrepository.core.dto.VidResponseDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ConfigurationProperties("mosip.idrepo.vid")
@ActiveProfiles("test")
public class VidControllerTest {

	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requesttime";

	@InjectMocks
	private VidController controller;

	@Mock
	private VidService<Object, ResponseWrapper<VidResponseDTO>> vidService;

	@Mock
	private VidRequestValidator vidValidator;

	@Mock
	private VidValidator<String> validator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(controller, "validator", vidValidator);
	}

	/**
	 * Test init binder.
	 */
	@Test
	public void testInitBinder() {
		controller.initBinder(Mockito.mock(WebDataBinder.class));
	}

	@Test
	public void testVidControlerRetrieveVidByVidValid() throws IdRepoAppException {
		ResponseWrapper<VidResponseDTO> value = new ResponseWrapper<VidResponseDTO>();
		Mockito.when(vidService.retrieveUinByVid(Mockito.anyString())).thenReturn(value);
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		ResponseEntity<ResponseWrapper<VidResponseDTO>> responseEntity = controller.retrieveUinByVid("12345");
		assertEquals(value, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testVidControllerRetrieveVidByVidInvalidIDException() throws Throwable {
		try {
			when(validator.validateId(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			Mockito.when(vidService.retrieveUinByVid(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			controller.retrieveUinByVid("12345");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-002 --> Invalid Input Parameter - vid", e.getMessage());
		}
	}

	@Test
	public void testVidControllerRetrieveVidByVidIdRepoAppException() throws Throwable {
		try {
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			Mockito.when(vidService.retrieveUinByVid(Mockito.anyString()))
					.thenThrow(new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
							IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage()));
			controller.retrieveUinByVid("12345");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getCause().getMessage());
		}
	}

	@Test
	public void testUpdateVidStatusvalid() throws IdRepoAppException {
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		req.setId("mosip.vid.update");
		VidRequestDTO request = new VidRequestDTO();
		request.setVidStatus("ACTIVE");
		req.setVersion("v1");
		req.setRequest(request);
		ResponseWrapper<VidResponseDTO> value = new ResponseWrapper<>();
		Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any())).thenReturn(value);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
		controller.updateVidStatus("123456", req, errors);
	}

	@Test
	public void testUpdateVidInvalidVidException() {
		try {
			RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
			when(validator.validateId(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
			controller.updateVidStatus("123456", req, errors);
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-002 --> Invalid Input Parameter - vid", e.getMessage());
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateVidInvalid_DataException() throws IdRepoAppException {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		ResponseWrapper<VidResponseDTO> value = new ResponseWrapper<VidResponseDTO>();
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any())).thenReturn(value);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
		errors.reject(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(),
				String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(), REQUEST_TIME));
		controller.updateVidStatus("123456", req, errors);

	}

	@Test
	public void testUpdateVidIdRepoAppException() throws Throwable {
		try {
			RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any()))
					.thenThrow(new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
							IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage()));
			BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
			controller.updateVidStatus("123456", req, errors);
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getCause().getMessage());
		}
	}

	@Test
	public void testCreateVidValid() throws IdRepoAppException {
		RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
		VidRequestDTO request = new VidRequestDTO();
		request.setUin(2953190571L);
		req.setRequest(request);
		ResponseWrapper<VidResponseDTO> value = new ResponseWrapper<VidResponseDTO>();
		Mockito.when(vidService.createVid(Mockito.any())).thenReturn(value);
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
		ResponseEntity<ResponseWrapper<VidResponseDTO>> responseEntity = controller.createVid(req, errors);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testCreateVidInValid() throws Throwable {
		try {
			RequestWrapper<VidRequestDTO> req = new RequestWrapper<VidRequestDTO>();
			VidRequestDTO request = new VidRequestDTO();
			request.setUin(2953190571L);
			req.setRequest(request);
			BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "RequestWrapper<RequestDTO>");
			Mockito.when(vidService.createVid(Mockito.any())).thenThrow(new IdRepoAppException(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(),
					String.format(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), "create")));
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			controller.createVid(req, errors);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(String.format(IdRepoErrorConstants.VID_GENERATION_FAILED.getErrorMessage(), "create"),
					e.getErrorText());
		}
	}
	
	@Test
	public void testRegenerateVid_Valid() throws IdRepoAppException{
		ResponseWrapper<VidResponseDTO> value = new ResponseWrapper<VidResponseDTO>();
		Mockito.when(vidService.regenerateVid(Mockito.anyString())).thenReturn(value);
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		ResponseEntity<ResponseWrapper<VidResponseDTO>> responseEntity = controller.regenerateVid("12345");
		assertEquals(value, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	@Test
	public void testRegenerateVidIdRepoAppException() throws Throwable {
		try {
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			Mockito.when(vidService.regenerateVid(Mockito.anyString()))
					.thenThrow(new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(),
							IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage()));
			controller.regenerateVid("123456");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-007 --> No Record(s) found", e.getCause().getMessage());
		}
	}
	
	@Test
	public void testRegenerateVid_InvalidVidException() {
		try {
			when(validator.validateId(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			Mockito.when(vidService.regenerateVid(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), "vid")));
			controller.regenerateVid("123456");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-IDC-002 --> Invalid Input Parameter - vid", e.getMessage());
		}
	}
}
