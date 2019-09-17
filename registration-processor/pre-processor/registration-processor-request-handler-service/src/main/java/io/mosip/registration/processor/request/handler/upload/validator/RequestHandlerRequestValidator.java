package io.mosip.registration.processor.request.handler.upload.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.UinValidator;
import io.mosip.kernel.core.idvalidator.spi.VidValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import io.mosip.registration.processor.core.constant.CardType;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdRepoAppException;
import io.mosip.registration.processor.packet.storage.exception.VidCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.request.handler.service.dto.MachineResponseDto;
import io.mosip.registration.processor.request.handler.service.dto.RegistrationCenterResponseDto;
import io.mosip.registration.processor.request.handler.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.request.handler.service.exception.RequestHandlerValidationException;
import io.mosip.registration.processor.status.code.RegistrationType;

/**
 * The Class PacketGeneratorRequestValidator.
 * 
 * @author Rishabh Keshari
 */
@Component
public class RequestHandlerRequestValidator {

	/** The Constant VER. */
	private static final String VER = "version";

	/** The Constant DATETIME_TIMEZONE. */
	private static final String DATETIME_TIMEZONE = "mosip.registration.processor.timezone";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	/** The mosip logger. */
	Logger regProcLogger = RegProcessorLogger.getLogger(RequestHandlerRequestValidator.class);

	/** The Constant ID_REPO_SERVICE. */
	private static final String REQUEST_HANDLER_SERVICE = "RequestHandlerService";

	/** The Constant TIMESTAMP. */
	private static final String TIMESTAMP = "requesttime";

	/** The Constant ID_FIELD. */
	private static final String ID_FIELD = "id";

	/** The Constant UIN. */
	private static final String UIN = "UIN";

	/** The Constant VID. */
	private static final String VID = "VID";

	/** The Constant REG_PACKET_GENERATOR_SERVICE_ID. */
	private static final String REG_PACKET_GENERATOR_SERVICE_ID = "mosip.registration.processor.registration.packetgenerator.id";

	/** The Constant REG_UINCARD_REPRINT_SERVICE_ID. */
	private static final String REG_UINCARD_REPRINT_SERVICE_ID = "mosip.registration.processor.uincard.reprint.id";

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

	/** The primary languagecode. */
	@Value("${mosip.primary-language}")
	private String primaryLanguagecode;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper = new ObjectMapper();

	/** The uin validator impl. */
	@Autowired
	private UinValidator<String> uinValidatorImpl;

	/** The vid validator impl. */
	@Autowired
	private VidValidator<String> vidValidatorImpl;

	/** The utilities. */
	@Autowired
	private Utilities utilities;

	/**
	 * Validate.
	 *
	 * @param requestTime
	 *            the request time
	 * @param requestId
	 *            the request id
	 * @param requestVersion
	 *            the request version
	 * @throws RequestHandlerValidationException
	 *             the packet generator validation exception
	 */
	public void validate(String requestTime, String requestId, String requestVersion)
			throws RequestHandlerValidationException {
		id.put("packet_generator", env.getProperty(REG_PACKET_GENERATOR_SERVICE_ID));
		id.put("uincard_reprint_status", env.getProperty(REG_UINCARD_REPRINT_SERVICE_ID));
		validateReqTime(requestTime);
		validateId(requestId);
		validateVersion(requestVersion);

	}

	/**
	 * Validate id.
	 *
	 * @param id
	 *            the id
	 * @throws RequestHandlerValidationException
	 *             the packet generator validation exception
	 */
	private void validateId(String id) throws RequestHandlerValidationException {
		RequestHandlerValidationException exception = new RequestHandlerValidationException();
		if (Objects.isNull(id)) {
			throw new RequestHandlerValidationException(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getMessage(), ID_FIELD),
					exception);

		} else if (!this.id.containsValue(id)) {
			throw new RequestHandlerValidationException(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), ID_FIELD),
					exception);

		}
	}

	/**
	 * Validate ver.
	 *
	 * @param ver
	 *            the ver
	 * @throws RequestHandlerValidationException
	 *             the packet generator validation exception
	 */
	private void validateVersion(String ver) throws RequestHandlerValidationException {
		String version = env.getProperty(REG_PACKET_GENERATOR_APPLICATION_VERSION);
		RequestHandlerValidationException exception = new RequestHandlerValidationException();
		if (Objects.isNull(ver)) {
			throw new RequestHandlerValidationException(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getMessage(), VER), exception);

		} else if (!version.equals(ver)) {
			throw new RequestHandlerValidationException(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
					String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), VER), exception);

		}
	}

	/**
	 * Validate req time.
	 *
	 * @param timestamp
	 *            the timestamp
	 * @throws RequestHandlerValidationException
	 *             the packet generator validation exception
	 */
	private void validateReqTime(String timestamp) throws RequestHandlerValidationException {
		RequestHandlerValidationException exception = new RequestHandlerValidationException();
		if (Objects.isNull(timestamp)) {
			throw new RequestHandlerValidationException(PlatformErrorMessages.RPR_PGS_MISSING_INPUT_PARAMETER.getCode(),
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
						regProcLogger.error(REQUEST_HANDLER_SERVICE, "PacketGeneratorRequestValidator",
								"validateReqTime",
								"\n" + PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage());

						throw new RequestHandlerValidationException(
								PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
								String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(),
										TIMESTAMP),
								exception);

					}

				}
			} catch (IllegalArgumentException e) {
				regProcLogger.error(REQUEST_HANDLER_SERVICE, "PacketGeneratorRequestValidator", "validateReqTime",
						"\n" + ExceptionUtils.getStackTrace(e));
				throw new RequestHandlerValidationException(
						PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getCode(),
						String.format(PlatformErrorMessages.RPR_PGS_INVALID_INPUT_PARAMETER.getMessage(), TIMESTAMP),
						exception);

			}
		}
	}

	/**
	 * Checks if is valid center.
	 *
	 * @param centerId
	 *            the center id
	 * @return true, if is valid center
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean isValidCenter(String centerId) throws RegBaseCheckedException, IOException {
		boolean isValidCenter = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(centerId);
		pathsegments.add(primaryLanguagecode);
		RegistrationCenterResponseDto rcpdto;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		try {
			if (centerId != null && !centerId.isEmpty()) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"PacketGeneratorServiceImpl::isValidCenter():: Centerdetails Api call started");
				responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.CENTERDETAILS, pathsegments, "",
						"", ResponseWrapper.class);
				rcpdto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
						RegistrationCenterResponseDto.class);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"PacketGeneratorServiceImpl::isValidCenter():: Centerdetails Api call  ended with response data : "
								+ JsonUtil.objectMapperObjectToJson(rcpdto));
				if (responseWrapper.getErrors() == null && !rcpdto.getRegistrationCenters().isEmpty()) {
					isValidCenter = true;
				} else {
					List<ErrorDTO> error = responseWrapper.getErrors();

					throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
							error.get(0).getMessage(), new Throwable());
				}
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						"Center id is mandatory", new Throwable());
			}
		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), e);

			}

		}
		return isValidCenter;
	}

	/**
	 * Checks if is valid machine.
	 *
	 * @param machine
	 *            the machine
	 * @return true, if is valid machine
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean isValidMachine(String machine) throws RegBaseCheckedException, IOException {
		boolean isValidMachine = false;
		List<String> pathsegments = new ArrayList<>();
		pathsegments.add(machine);
		pathsegments.add(primaryLanguagecode);
		MachineResponseDto machinedto;
		ResponseWrapper<?> responseWrapper = new ResponseWrapper<>();
		try {

			if (machine != null && !machine.isEmpty()) {
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"PacketGeneratorServiceImpl::isValidMachine():: MachineDetails Api call started");
				responseWrapper = (ResponseWrapper<?>) restClientService.getApi(ApiName.MACHINEDETAILS, pathsegments,
						"", "", ResponseWrapper.class);
				machinedto = mapper.readValue(mapper.writeValueAsString(responseWrapper.getResponse()),
						MachineResponseDto.class);
				regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), "",
						"\"PacketGeneratorServiceImpl::isValidMachine():: MachienDetails Api call  ended with response data : "
								+ JsonUtil.objectMapperObjectToJson(machinedto));
				if (responseWrapper.getErrors() == null && !machinedto.getMachines().isEmpty()) {
					isValidMachine = true;
				} else {
					List<ErrorDTO> error = responseWrapper.getErrors();
					throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
							error.get(0).getMessage(), new Throwable());
				}
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						"Machine id is mandatory", new Throwable());
			}

		} catch (ApisResourceAccessException e) {
			if (e.getCause() instanceof HttpClientErrorException) {
				List<ErrorDTO> error = responseWrapper.getErrors();
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						error.get(0).getMessage(), e);

			}

		}
		return isValidMachine;

	}

	/**
	 * Checks if is valid uin.
	 *
	 * @param uin
	 *            the uin
	 * @return true, if is valid uin
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidUin(String uin) throws RegBaseCheckedException {
		boolean isValidUIN = false;
		try {
			isValidUIN = uinValidatorImpl.validateId(uin);
			JSONObject jsonObject = utilities.retrieveIdrepoJson(Long.parseLong(uin));
			if (isValidUIN && jsonObject != null) {
				isValidUIN = true;
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, "UIN is not valid",
						new Throwable());

			}
		} catch (InvalidIDException ex) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, ex.getErrorText(), ex);

		} catch (IdRepoAppException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		} catch (NumberFormatException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		} catch (ApisResourceAccessException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		} catch (IOException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		}
		return isValidUIN;
	}

	/**
	 * Checks if is valid re print registration type.
	 *
	 * @param registrationType
	 *            the registration type
	 * @return true, if is valid re print registration type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidRePrintRegistrationType(String registrationType) throws RegBaseCheckedException {
		if (registrationType != null && (registrationType.equalsIgnoreCase(RegistrationType.RES_REPRINT.toString()))) {
			return true;
		} else {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
					"Invalid RegistrationType:Enter RES_REPRINT", new Throwable());
		}

	}

	/**
	 * Checks if is valid registration type and uin.
	 *
	 * @param registrationType
	 *            the registration type
	 * @param uin
	 *            the uin
	 * @return true, if is valid registration type and uin
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidRegistrationTypeAndUin(String registrationType, String uin) throws RegBaseCheckedException {
		try {
			if (registrationType != null && (registrationType.equalsIgnoreCase(RegistrationType.ACTIVATED.toString())
					|| registrationType.equalsIgnoreCase(RegistrationType.DEACTIVATED.toString()))) {
				boolean isValidUin = uinValidatorImpl.validateId(uin);
				if (isValidUin) {
					String status = utilities.retrieveIdrepoJsonStatus(Long.parseLong(uin));
					if (!status.equalsIgnoreCase(registrationType)) {
						return true;
					} else {
						throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
								"Uin is already " + status, new Throwable());
					}
				} else {
					throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
							"UIN is not valid", new Throwable());
				}
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
						"Invalid RegistrationType:Enter ACTIVATED or DEACTIVATED", new Throwable());
			}
		} catch (InvalidIDException ex) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, ex.getErrorText(), ex);
		} catch (NumberFormatException | IdRepoAppException | ApisResourceAccessException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		}
	}

	/**
	 * Checks if is valid vid.
	 *
	 * @param vid
	 *            the vid
	 * @return true, if is valid vid
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidVid(String vid) throws RegBaseCheckedException {
		boolean isValidVID = false;
		try {
			isValidVID = vidValidatorImpl.validateId(vid);
			String result = utilities.getUinByVid(vid);
			if (isValidVID && result != null) {
				isValidVID = true;
			} else {
				throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, "VID is not valid",
						new Throwable());

			}
		} catch (InvalidIDException ex) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, ex.getErrorText(), ex);

		} catch (IdRepoAppException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		} catch (NumberFormatException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		} catch (ApisResourceAccessException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		} catch (VidCreationException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e.getErrorText(), e);
		}
		return isValidVID;
	}

	/**
	 * Checks if is valid id type.
	 *
	 * @param idType
	 *            the id type
	 * @return true, if is valid id type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidIdType(String idType) throws RegBaseCheckedException {
		if (idType != null && (idType.equalsIgnoreCase(UIN) || idType.equalsIgnoreCase(VID))) {
			return true;
		} else {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
					"Invalid IdType : Enter UIN or VID", new Throwable());
		}

	}

	/**
	 * Checks if is valid card type.
	 *
	 * @param cardType
	 *            the card type
	 * @return true, if is valid card type
	 * @throws RegBaseCheckedException
	 *             the reg base checked exception
	 */
	public boolean isValidCardType(String cardType) throws RegBaseCheckedException {
		if (cardType != null && !cardType.isEmpty() && (cardType.equalsIgnoreCase(CardType.UIN.toString())
				|| cardType.equalsIgnoreCase(CardType.MASKED_UIN.toString()))) {
			return true;
		} else {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION,
					"Invalid CardType : Enter UIN or MASKED_UIN", new Throwable());
		}

	}

}
