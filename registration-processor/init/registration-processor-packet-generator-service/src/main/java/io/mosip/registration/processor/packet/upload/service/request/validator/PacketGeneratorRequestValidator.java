package io.mosip.registration.processor.packet.upload.service.request.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorRequestDto;
import io.mosip.registration.processor.packet.service.exception.PacketGeneratorValidationException;

/**
 * The Class PacketGeneratorRequestValidator.
 * 
 * @author Rishabh Keshari
 */
@Component
public class PacketGeneratorRequestValidator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger regProcLogger = RegProcessorLogger.getLogger(PacketGeneratorRequestValidator.class);

	/** The Constant ID_REPO_SERVICE. */
	private static final String PACKET_GENERATOR_SERVICE = "PacketGenerationService";

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "requesttime";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";

	/** The Constant REG_PACKET_GENERATOR_APPLICATION_VERSION. */
	private static final String REG_PACKET_GENERATOR_APPLICATION_VERSION = "mosip.registration.processor.packetgenerator.version";

	/** The env. */
	@Autowired
	private Environment env;

	/** The id. */
	private Map<String, String> id = new HashMap<>();
	
	
	/** The grace period. */
	@Value("${mosip.registration.processor.grace.period}")
	private int gracePeriod;

	/**
	 * Validate.
	 *
	 * @param request
	 *            the request
	 * @throws PacketGeneratorValidationException
	 *             the packet generator validation exception
	 */
	public void validate(PacketGeneratorRequestDto request) throws PacketGeneratorValidationException {
		id.put("status", env.getProperty(REG_PACKET_GENERATOR_SERVICE_ID));
		validateReqTime(request.getRequesttime());
		validateId(request.getId());
		validateVersion(request.getVersion());

	}

	/**
	 * Validate id.
	 *
	 * @param id
	 *            the id
	 * @throws PacketGeneratorValidationException
	 *             the packet generator validation exception
	 */
	private void validateId(String id) throws PacketGeneratorValidationException {
		PacketGeneratorValidationException exception = new PacketGeneratorValidationException();
		if (Objects.isNull(id)) {
			throw new PacketGeneratorValidationException(
					PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD),
					exception);

		} else if (!this.id.containsValue(id)) {
			throw new PacketGeneratorValidationException(
					PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD),
					exception);

		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver
	 *            the ver
	 * @throws PacketGeneratorValidationException
	 *             the packet generator validation exception
	 */
	private void validateVersion(String ver) throws PacketGeneratorValidationException {
		String version = env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION);
		PacketGeneratorValidationException exception = new PacketGeneratorValidationException();
		if (Objects.isNull(ver)) {
			throw new PacketGeneratorValidationException(
					PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getMessage(), VER), exception);

		} else if (!version.equals(ver)) {
			throw new PacketGeneratorValidationException(
					PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), VER), exception);

		}
	}

	/**
	 * Validate req time.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @throws PacketGeneratorValidationException
	 *             the packet generator validation exception
	 */
	private void validateReqTime(String timestamp) throws PacketGeneratorValidationException {
		PacketGeneratorValidationException exception = new PacketGeneratorValidationException();
		if (Objects.isNull(timestamp)) {
			throw new PacketGeneratorValidationException(
					PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getMessage(), TIMESTAMP),
					exception);

		} else {
			try {
				if (Objects.nonNull(env.getProperty(DATETIME_PATTERN))) {
					DateTimeFormatterFactory timestampFormat = new DateTimeFormatterFactory(
							env.getProperty(DATETIME_PATTERN));
					timestampFormat.setTimeZone(TimeZone.getTimeZone(env.getProperty(DATETIME_TIMEZONE)));
					if (!(DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
							.isAfter(new DateTime().minusSeconds(gracePeriod))
							&& DateTime.parse(timestamp, timestampFormat.createDateTimeFormatter())
									.isBefore(new DateTime().plusSeconds(gracePeriod)))) {
						regProcLogger.error(PACKET_GENERATOR_SERVICE, "PacketGeneratorRequestValidator", "validateReqTime",
								"\n" + PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage());
						
						throw new PacketGeneratorValidationException(
								PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
								String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP),
								exception);

					}

				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(PACKET_GENERATOR_SERVICE, "PacketGeneratorRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				throw new PacketGeneratorValidationException(
						PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP),
						exception);

			}
		}
	}

}
