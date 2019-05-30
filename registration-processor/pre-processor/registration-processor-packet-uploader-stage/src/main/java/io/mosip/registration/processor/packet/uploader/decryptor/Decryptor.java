package io.mosip.registration.processor.packet.uploader.decryptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.uploader.constants.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.packet.uploader.dto.CryptomanagerRequestDto;
import io.mosip.registration.processor.packet.uploader.dto.CryptomanagerResponseDto;
import io.mosip.registration.processor.packet.uploader.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;

/**
 * Decryptor class for packet decryption.
 *
 * @author Girish Yarru
 */
@Component
public class Decryptor {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(Decryptor.class);

	@Value("${registration.processor.application.id}")
	private String applicationId;

	@Value("${mosip.kernel.machineid.length}")
	private int machineIdLength;

	@Value("${mosip.kernel.registrationcenterid.length}")
	private int centerIdLength;

	@Value("${registration.processor.rid.machineidsubstring}")
	private int machineIdSubStringLength;
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Autowired
	private Environment env;

	private static final String DECRYPT_SERVICE_ID = "mosip.registration.processor.crypto.decrypt.id";
	private static final String REG_PROC_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	private static final String DECRYPTION_SUCCESS = "Decryption success for RegistrationId : {}";
	private static final String DECRYPTION_FAILURE = "Virus scan decryption failed for  registrationId ";
	private static final String IO_EXCEPTION = "Exception Converting encrypted packet inputStream to string";

	/**
	 * This method consumes inputStream of encrypted packet and registrationId as
	 * arguments. Hits the kernel's crypto-manager api passing 'application
	 * id,center id and encrypted inputStream in form of string. gets the
	 * response(Success or Failure) as string if success convert string to
	 * cryptomanager response dto and then get decrypted data and then return
	 * inputStream of decrypted data. if failure convert string to cryptomanager
	 * response dto and then get error code and error response and throw
	 * PacketDecryptionFailureException.
	 * 
	 * @param encryptedPacket
	 * @param registrationId
	 * @return
	 * @throws PacketDecryptionFailureException
	 * @throws ApisResourceAccessException
	 * @throws ParseException
	 */

	@SuppressWarnings("unchecked")
	public InputStream decrypt(InputStream encryptedPacket, String registrationId)
			throws PacketDecryptionFailureException, ApisResourceAccessException {
		InputStream outstream = null;
		boolean isTransactionSuccessful = false;
		String description = "";
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, "Decryptor::decrypt()::entry");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String centerId = registrationId.substring(0, centerIdLength);
			String machineId = registrationId.substring(centerIdLength, machineIdSubStringLength);
			String refId = centerId + "_" + machineId;
			String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
			RequestWrapper<CryptomanagerRequestDto> request = new RequestWrapper<>();
			ResponseWrapper<CryptomanagerResponseDto> response;
			cryptomanagerRequestDto.setApplicationId(applicationId);
			cryptomanagerRequestDto.setData(encryptedPacketString);
			cryptomanagerRequestDto.setReferenceId(refId);
			CryptomanagerResponseDto cryptomanagerResponseDto;

			// setLocal Date Time
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			LocalDateTime ldt = null;
			if (registrationId.length() > 14) {
				String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
				Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6));
				ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
				cryptomanagerRequestDto.setTimeStamp(ldt);
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Packet DecryptionFailed-Invalid Packet format");

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						"Packet DecryptionFailed-Invalid Packet format");
			}
			request.setId(env.getProperty(DECRYPT_SERVICE_ID));
			request.setMetadata(null);
			request.setRequest(cryptomanagerRequestDto);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
			LocalDateTime localdatetime = LocalDateTime
					.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
			request.setRequesttime(localdatetime);
			request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));
			response = (ResponseWrapper<CryptomanagerResponseDto>) restClientService
					.postApi(ApiName.DMZCRYPTOMANAGERDECRYPT, "", "", request, ResponseWrapper.class);
			cryptomanagerResponseDto = mapper.readValue(mapper.writeValueAsString(response.getResponse()),
					CryptomanagerResponseDto.class);
			byte[] decryptedPacket = CryptoUtil.decodeBase64(cryptomanagerResponseDto.getData());
			outstream = new ByteArrayInputStream(decryptedPacket);
			isTransactionSuccessful = true;
			description = DECRYPTION_SUCCESS + registrationId;
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Decryptor::decrypt()::exit");
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, IO_EXCEPTION);
			description = DECRYPTION_FAILURE + registrationId + "::"
					+ "Error Converting encrypted packet inputStream to string";
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					IO_EXCEPTION);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Internal Error Occured ");
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();
				description = DECRYPTION_FAILURE + registrationId + "::"
						+ httpClientException.getResponseBodyAsString();
				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						httpClientException.getResponseBodyAsString());
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();
				description = DECRYPTION_FAILURE + registrationId + "::"
						+ httpServerException.getResponseBodyAsString();

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						httpServerException.getResponseBodyAsString());
			} else {
				description = DECRYPTION_FAILURE + registrationId + "::" + e.getMessage();

				throw e;
			}

		} catch (ParseException e) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					"Packet DecryptionFailed-Invalid PacketFormat : Unable to parse packet date and time");
			description = DECRYPTION_FAILURE + registrationId + "::"
					+ "Packet DecryptionFailed-Invalid PacketFormat : Unable to parse packet date and time "
					+ e.getMessage();

			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					"Packet DecryptionFailed-Invalid PacketFormat : Unable to parse packet date and time");
		} finally {
			String eventId = "";
			String eventName = "";
			String eventType = "";
			eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
					: EventName.EXCEPTION.toString();
			eventType = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventType.BUSINESS.toString()
					: EventType.SYSTEM.toString();

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, registrationId,
					ApiName.AUDIT);
		}
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, DECRYPTION_SUCCESS);
		return outstream;
	}

}
