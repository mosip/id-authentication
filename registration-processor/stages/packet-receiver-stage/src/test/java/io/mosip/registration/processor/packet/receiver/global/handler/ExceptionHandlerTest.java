package io.mosip.registration.processor.packet.receiver.global.handler;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.ValidationException;
import io.mosip.registration.processor.packet.receiver.exception.handler.GlobalExceptionHandler;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.TimeoutException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;


@RunWith(SpringRunner.class)
public class ExceptionHandlerTest {

	@InjectMocks
	GlobalExceptionHandler globalExceptionHandler;

	@Test
	public void testDuplicateEntry() {
		DuplicateUploadRequestException exe = new DuplicateUploadRequestException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-PKR-005 --> The Registration Packet Size is invalid", response);
	}
	
	@Test
	public void testhandlePacketNotValidException() {
		PacketNotValidException exe = new PacketNotValidException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-PKR-003 --> The Registration Packet Size is invalid", response);
	}
	
	@Test
	public void testhandleFileSizeExceedException() {
		FileSizeExceedException exe = new FileSizeExceedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-PKR-002 --> The Registration Packet Size is invalid", response);
	}
	
	@Test
	public void handlePacketNotSyncException() {
		PacketNotSyncException exe = new PacketNotSyncException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-PKR-001 --> The Registration Packet Size is invalid", response);
	}
	@Test
	public void testhandleTablenotAccessibleException() {
		TablenotAccessibleException exe = new TablenotAccessibleException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-RGS-001 --> The Registration Packet Size is invalid", response);
	}
	@Test
	public void handleTimeoutException() {
		TimeoutException exe = new TimeoutException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-SYS-005 --> The Registration Packet Size is invalid", response);
	}
	@Test
	public void testhandleUnexpectedException() {
		UnexpectedException exe = new UnexpectedException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-SYS-001 --> The Registration Packet Size is invalid", response);
	}
	@Test
	public void testhandleValidationException() {
		ValidationException exe = new ValidationException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("RPR-PKR-004 --> The Registration Packet Size is invalid", response);
	}
	@Test
	public void testdataExceptionHandler() {
		DataIntegrityViolationException exe = new DataIntegrityViolationException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("Data Integrity Violation Exception", response);
	}
	@Test
	public void testhandlePacketNotAvailableException() {
		MissingServletRequestPartException exe = new MissingServletRequestPartException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		String response = globalExceptionHandler.handler(exe);	
		assertEquals("Packet not avaialble", response);
	}
	
}
