package io.mosip.authentication.service.impl.vid.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.vid.service.impl.controller.VIDController;
import io.mosip.authentication.vid.service.impl.id.service.impl.VIDServiceImpl;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class VIDControllerTest {

	@InjectMocks
	private VIDController vidController;

	@Mock
	private VIDServiceImpl vidService;

	@Mock
	private UinValidatorImpl uinValidator;

	@Test
	public void generateVIDTest() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		VIDResponseDTO vidResponseDTO = new VIDResponseDTO();
		Mockito.when(vidService.generateVID(Mockito.anyString())).thenReturn(vidResponseDTO);
		Mockito.when(uinValidator.validateId(Mockito.any())).thenReturn(true);
		vidController.generateVID(Mockito.anyString());

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void generateVIDTestFail() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		Mockito.when(vidService.generateVID(Mockito.anyString())).thenThrow(
				new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED));
		Mockito.when(uinValidator.validateId(Mockito.any())).thenReturn(true);
		vidController.generateVID(Mockito.anyString());

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void generateVIDTestFailUINValidate()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		Mockito.when(uinValidator.validateId(Mockito.any())).thenThrow(InvalidIDException.class);
		vidController.generateVID(Mockito.anyString());

	}
}
