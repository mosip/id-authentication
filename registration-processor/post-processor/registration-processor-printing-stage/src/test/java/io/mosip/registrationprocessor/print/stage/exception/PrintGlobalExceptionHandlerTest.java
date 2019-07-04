package io.mosip.registrationprocessor.print.stage.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.print.exception.PrintGlobalExceptionHandler;
import io.mosip.registration.processor.print.exception.TimeoutException;
import io.mosip.registration.processor.print.exception.UnexpectedException;
import io.mosip.registration.processor.print.service.exception.UINNotFoundInDatabase;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.vertx.core.json.DecodeException;


@RunWith(SpringRunner.class)
public class PrintGlobalExceptionHandlerTest {

	@InjectMocks
	private PrintGlobalExceptionHandler globalExceptionHandler;

	@Test
	public void testhandleTablenotAccessibleException() {
		TablenotAccessibleException exe = new TablenotAccessibleException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("The Registration Packet Size is invalid", response);
	}
	@Test
	public void handleTimeoutException() {
		TimeoutException exe = new TimeoutException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-SYS-005 --> Invalid Packet Size", response);
	}
	@Test
	public void testhandleUnexpectedException() {
		UnexpectedException exe = new UnexpectedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-SYS-001 --> Invalid Packet Size", response);
	}
	
	@Test
	public void testUINNotFoundInDatabase() {
		UINNotFoundInDatabase exe = new UINNotFoundInDatabase(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("The Registration Packet Size is invalid", response);
	}
	
	@Test
	public void testDecodeException() {
		DecodeException exe = new DecodeException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("The Registration Packet Size is invalid", response);
	}
	@Test 
	public void testInternalException() {
		Exception exe = new Exception(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("The Registration Packet Size is invalid", response);
	}
}
