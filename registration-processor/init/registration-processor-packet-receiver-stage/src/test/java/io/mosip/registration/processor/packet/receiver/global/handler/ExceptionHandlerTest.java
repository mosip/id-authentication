package io.mosip.registration.processor.packet.receiver.global.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.receiver.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.receiver.exception.DuplicateUploadRequestException;
import io.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotSyncException;
import io.mosip.registration.processor.packet.receiver.exception.PacketNotValidException;
import io.mosip.registration.processor.packet.receiver.exception.PacketSizeNotInSyncException;
import io.mosip.registration.processor.packet.receiver.exception.UnequalHashSequenceException;
import io.mosip.registration.processor.packet.receiver.exception.ValidationException;
import io.mosip.registration.processor.packet.receiver.exception.VirusScanFailedException;
import io.mosip.registration.processor.packet.receiver.exception.VirusScannerServiceException;
import io.mosip.registration.processor.packet.receiver.exception.handler.PacketReceiverExceptionHandler;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.TimeoutException;
import io.mosip.registration.processor.packet.receiver.exception.systemexception.UnexpectedException;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;

@RunWith(SpringRunner.class)
// @RunWith(PowerMockRunner.class)
public class ExceptionHandlerTest {

	@InjectMocks
	PacketReceiverExceptionHandler packetReceiverExceptionHandler;

	@Mock
	private Environment env;

	PacketReceiverResponseDTO packetReceiverResponseDTO = new PacketReceiverResponseDTO();
	private List<ErrorDTO> errors = new ArrayList<>();
	ErrorDTO errorDTO = new ErrorDTO("", "");

	Gson gson = new GsonBuilder().create();

	/**
	 * Sets the up.
	 * 
	 * @throws JsonProcessingException
	 */
	@Before
	public void setUp() throws JsonProcessingException {
		when(env.getProperty("mosip.registration.processor.packet.id")).thenReturn("mosip.registration.packet");
		when(env.getProperty("mosip.registration.processor.datetime.pattern"))
				.thenReturn("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		when(env.getProperty("mosip.registration.processor.application.version")).thenReturn("1.0");

		packetReceiverResponseDTO = new PacketReceiverResponseDTO();
		if (Objects.isNull(packetReceiverResponseDTO.getId())) {
			packetReceiverResponseDTO.setId("mosip.registration.packet");
		}

		packetReceiverResponseDTO.setVersion("1.0");
		packetReceiverResponseDTO.setResponse(null);
	}

	@Test
	public void testDuplicateEntry() throws JsonParseException, JsonMappingException, IOException {
		DuplicateUploadRequestException exe = new DuplicateUploadRequestException(
				PlatformErrorMessages.RPR_PKR_DUPLICATE_PACKET_RECIEVED.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-005");
		errorDTO.setMessage("Duplicate Request Received");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandlePacketNotValidException() {
		PacketNotValidException exe = new PacketNotValidException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =  packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-003");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleFileSizeExceedException() {
		FileSizeExceedException exe = new FileSizeExceedException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-002");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void handlePacketNotSyncException() {
		PacketNotSyncException exe = new PacketNotSyncException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =  packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-001");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleTablenotAccessibleException() {
		TablenotAccessibleException exe = new TablenotAccessibleException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-RGS-001");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void handleTimeoutException() {
		TimeoutException exe = new TimeoutException(PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-SYS-005");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleUnexpectedException() {
		UnexpectedException exe = new UnexpectedException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-SYS-001");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleValidationException() {
		ValidationException exe = new ValidationException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-004");
		errorDTO.setMessage("Invalid Packet Size");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testdataExceptionHandler() {
		DataIntegrityViolationException exe = new DataIntegrityViolationException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		packetReceiverResponseDTO.setErrors(null);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandlePacketNotAvailableException() {
		MissingServletRequestPartException exe = new MissingServletRequestPartException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-006 ");
		errorDTO.setMessage("Packet Not Available in Request");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandlePacketSizeNotInSyncException() {
		PacketSizeNotInSyncException exe = new PacketSizeNotInSyncException(
				PlatformErrorMessages.RPR_PKR_INVALID_PACKET_SIZE_SYNCED.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-013");
		errorDTO.setMessage("Packet Size is Not Matching");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleVirusScanFailedException() {
		VirusScanFailedException exe = new VirusScanFailedException(
				PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCAN_FAILED.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-010");
		errorDTO.setMessage("Virus was Found in Packet");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleUnequalHashSequenceException() {
		UnequalHashSequenceException exe = new UnequalHashSequenceException(
				PlatformErrorMessages.RPR_PKR_PACKET_HASH_NOT_EQUALS_SYNCED_HASH.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 = packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-009");
		errorDTO.setMessage("Packet HashSequence did not match");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

	@Test
	public void testhandleVirusScannerServiceException() {
		VirusScannerServiceException exe = new VirusScannerServiceException(
				PlatformErrorMessages.PRP_PKR_PACKET_VIRUS_SCANNER_SERVICE_FAILED.getMessage());
		PacketReceiverResponseDTO packetReceiverResponseDTO1 =  packetReceiverExceptionHandler.handler(exe);
		packetReceiverResponseDTO.setResponsetime(packetReceiverResponseDTO1.getResponsetime());
		errorDTO.setErrorCode("RPR-PKR-008");
		errorDTO.setMessage("Virus Scan Service is Not Responding");
		errors.add(errorDTO);
		packetReceiverResponseDTO.setErrors(errors);
		assertEquals(gson.toJson(packetReceiverResponseDTO), gson.toJson(packetReceiverResponseDTO1));
	}

}
