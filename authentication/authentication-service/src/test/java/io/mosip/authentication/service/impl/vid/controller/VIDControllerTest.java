package io.mosip.authentication.service.impl.vid.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class VIDControllerTest {
	
	@InjectMocks
	private VIDController vidController;
	
	@Mock
	private StaticPinFacade staticFacadeImpl;
	
	@Mock
	private UinValidatorImpl uinValidator;
	
	@Test
	public void generateVIDTest() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		VIDResponseDTO vidResponseDTO=new VIDResponseDTO();
		Mockito.when(staticFacadeImpl.generateVID(Mockito.anyString())).thenReturn(vidResponseDTO);
		Mockito.when(uinValidator.validateId(Mockito.any())).thenReturn(true);
		vidController.generateVID(Mockito.anyString());
		
	}

	@Test(expected=IdAuthenticationAppException.class)
	public void generateVIDTestFail() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		VIDResponseDTO vidResponseDTO=new VIDResponseDTO();
		Mockito.when(staticFacadeImpl.generateVID(Mockito.anyString())).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.VID_REGENERATION_FAILED));
		Mockito.when(uinValidator.validateId(Mockito.any())).thenReturn(true);
		vidController.generateVID(Mockito.anyString());
		
	}
}
