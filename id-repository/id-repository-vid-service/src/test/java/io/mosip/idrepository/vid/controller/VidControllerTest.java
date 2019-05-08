package io.mosip.idrepository.vid.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.spi.VidService;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.validator.VidRequestValidator;
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
public class VidControllerTest {

	/** The Constant TIMESTAMP. */
	private static final String REQUEST_TIME = "requestTime";

	@InjectMocks
	private VidController controller;

	@Mock
	private VidService<Object, VidResponseDTO> vidService;

	@InjectMocks
	private VidRequestValidator vidRequestValidator;

	@Mock
	private VidRequestValidator vidValidator;

	@Mock
	private VidValidator<String> validator;

	@Before
	public void before() {
		ReflectionTestUtils.setField(controller, "vidRequestValidator", vidRequestValidator);
	}

	/**
	 * Test init binder.
	 */
	@Test
	public void testInitBinder() {
		WebDataBinder binder = new WebDataBinder(new VidRequestDTO());
		controller.initBinder(binder);
	}

	@Test
	public void testVidControler_Valid() throws IdRepoAppException {
		VidResponseDTO value = new VidResponseDTO();
		Mockito.when(vidService.retrieveUinByVid(Mockito.anyString())).thenReturn(value);
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		ResponseEntity<VidResponseDTO> responseEntity = controller.retrieveUinByVid("12345");
		assertEquals(value, responseEntity.getBody());
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Test
	public void testVidController_InvalidIDException() throws Throwable {
		try {
			VidResponseDTO value = new VidResponseDTO();
			when(validator.validateId(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "vid")));
			Mockito.when(vidService.retrieveUinByVid(Mockito.anyString())).thenReturn(value);
			controller.retrieveUinByVid("12345");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-VID-001 --> Invalid Input Parameter - vid", e.getMessage());
		}
	}

	@Test
	public void testVidController_IdRepoAppException() throws Throwable {
		try {
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			Mockito.when(vidService.retrieveUinByVid(Mockito.anyString()))
					.thenThrow(new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
							IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage()));
			controller.retrieveUinByVid("12345");
		} catch (IdRepoAppException e) {
			assertEquals("IDR-VID-006 --> No Record(s) found", e.getCause().getMessage());
		}
	}

	@Test
	public void testUpdateVidStatus_valid() throws IdRepoAppException {
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		VidRequestDTO req = new VidRequestDTO();
		VidResponseDTO value = new VidResponseDTO();
		Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any())).thenReturn(value);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "VidRequestDTO");
		controller.updateVidStatus("123456", req, errors);
	}

	@Test
	public void testUpdateVid_InvalidVidException() {
		try {
			VidRequestDTO req = new VidRequestDTO();
			VidResponseDTO value = new VidResponseDTO();
			when(validator.validateId(Mockito.anyString()))
					.thenThrow(new InvalidIDException(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorCode(),
							String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER_VID.getErrorMessage(), "vid")));
			Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any())).thenReturn(value);
			BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "VidRequestDTO");
			controller.updateVidStatus("123456", req, errors);
		} catch (IdRepoAppException e) {
			assertEquals("IDR-VID-001 --> Invalid Input Parameter - vid", e.getMessage());
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateVid_Invalid_DataException() throws IdRepoAppException {
		VidRequestDTO req = new VidRequestDTO();
		VidResponseDTO value = new VidResponseDTO();
		when(validator.validateId(Mockito.anyString())).thenReturn(true);
		Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any())).thenReturn(value);
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "VidRequestDTO");
		errors.reject(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorCode(),
				String.format(IdRepoErrorConstants.MISSING_INPUT_PARAMETER_VID.getErrorMessage(), REQUEST_TIME));
		controller.updateVidStatus("123456", req, errors);

	}

	@Test
	public void testUpdateVid_IdRepoAppException() throws Throwable {
		try {
			VidRequestDTO req = new VidRequestDTO();
			when(validator.validateId(Mockito.anyString())).thenReturn(true);
			Mockito.when(vidService.updateVid(Mockito.anyString(), Mockito.any()))
					.thenThrow(new IdRepoAppException(IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorCode(),
							IdRepoErrorConstants.NO_RECORD_FOUND_VID.getErrorMessage()));
			BeanPropertyBindingResult errors = new BeanPropertyBindingResult(req, "VidRequestDTO");
			controller.updateVidStatus("123456", req, errors);
		} catch (IdRepoAppException e) {
			assertEquals("IDR-VID-006 --> No Record(s) found", e.getCause().getMessage());
		}
	}
}
