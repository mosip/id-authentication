package io.mosip.registration.processor.virus.scanner.job.decrypter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.virus.scanner.job.decrypter.constant.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.dto.CryptomanagerRequestDto;
import io.mosip.registration.processor.virus.scanner.job.dto.CryptomanagerResponseDto;

/**
 * Decryptor class for packet decryption.
 *
 * @author Girish Yarru
 */
@Component
public class Decryptor {
	private static io.mosip.kernel.core.logger.spi.Logger regProcLogger = RegProcessorLogger.getLogger(Decryptor.class);

	@Value("${registration.processor.application.id}")
	private String applicationId;

	@Value("${mosip.kernel.rid.machineid-length}")
	private int machineIdLength;

	@Value("${mosip.kernel.rid.centerid-length}")
	private int centerIdLength;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

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
	 * @throws ParseException
	 */

	public InputStream decrypt(InputStream encryptedPacket, String registrationId)
			throws PacketDecryptionFailureException {
		InputStream outstream = null;
		boolean isTransactionSuccessful = false;
		String description = "";
		try {
			String centerId = registrationId.substring(machineIdLength, machineIdLength + centerIdLength);
			String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
			cryptomanagerRequestDto.setApplicationId(applicationId);
			cryptomanagerRequestDto.setData(encryptedPacketString);
			cryptomanagerRequestDto.setReferenceId(centerId);

			// setLocal Date Time
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			if (registrationId.length() > 14) {
				String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
				Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6));
				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
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

			CryptomanagerResponseDto cryptomanagerResponseDto = (CryptomanagerResponseDto) restClientService.postApi(
					ApiName.CRYPTOMANAGERDECRYPT, "", "", cryptomanagerRequestDto, CryptomanagerResponseDto.class);
			byte[] decryptedPacket = CryptoUtil.decodeBase64(cryptomanagerResponseDto.getData());
			outstream = new ByteArrayInputStream(decryptedPacket);

			isTransactionSuccessful = true;
			description = DECRYPTION_FAILURE + registrationId;
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

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						e.getMessage());
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

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType,
					registrationId);
		}
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, DECRYPTION_SUCCESS);
		return outstream;
	}

}
