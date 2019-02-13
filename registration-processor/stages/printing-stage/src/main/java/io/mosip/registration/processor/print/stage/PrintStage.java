package io.mosip.registration.processor.print.stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.UinCardType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.notification.template.mapping.NotificationTemplate;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.JsonValue;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.spi.uincardgenerator.UinCardGenerator;
import io.mosip.registration.processor.message.sender.template.generator.TemplateGenerator;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.exception.FieldNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.InstantanceCreationException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.print.dto.IdResponseDTO;
import io.mosip.registration.processor.print.exception.PrintGlobalExceptionHandler;
import io.mosip.registration.processor.print.exception.UINNotFoundInDatabase;
import io.mosip.registration.processor.print.util.UINCardConstant;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * The Class PrintStage.
 * 
 * @author M1048358 Alok
 */
@RefreshScope
@Service
public class PrintStage extends MosipVerticleAPIManager {

	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	/** The Constant LANGUAGE. */
	private static final String LANGUAGE = "language";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	/** The Constant ENG. */
	private static final String ENG = "eng";

	/** The Constant ARA. */
	private static final String ARA = "ara";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(PrintStage.class);

	/** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The cluster manager url. */
	@Value("${vertx.ignite.configuration}")
	private String clusterManagerUrl;

	/** The primary language. */
	@Value("${primary.language}")
	private String LocallangCode;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The utility. */
	@Autowired
	private Utilities utility;

	/** The Constant USER. */
	private static final String USER = "MOSIP_SYSTEM";

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The demographic identity. */
	private JSONObject demographicIdentity = null;

	/** The attributes. */
	private Map<String, Object> attributes = new HashMap<>();

	/** The packet info manager. */
	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The template generator. */
	@Autowired
	private TemplateGenerator templateGenerator;

	/** The uin card generator. */
	@Autowired
	private UinCardGenerator<OutputStream> uinCardGenerator;

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;
	
	@Autowired
	public PrintGlobalExceptionHandler globalExceptionHandler;
	
	@Value("${server.port}")
	private String port;
	
	private MosipEventBus mosipEventBus;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.PRINTING_BUS_IN, MessageBusAddress.PRINTING_BUS_OUT);
	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		object.setMessageBusAddress(MessageBusAddress.BIO_DEDUPE_BUS_IN);
		object.setInternalError(Boolean.FALSE);

		boolean isTransactionSuccessful = false;
		String regId = object.getRid();

		try {
			String uin = packetInfoManager.getUINByRid(regId).get(0);
			if (uin == null) {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), regId,
						PlatformErrorMessages.RPR_PRT_UIN_NOT_FOUND_IN_DATABASE.name());
				throw new UINNotFoundInDatabase(PlatformErrorMessages.RPR_PRT_UIN_NOT_FOUND_IN_DATABASE.getCode());
			}

			List<String> pathsegments = new ArrayList<>();
			pathsegments.add("v1.0");
			pathsegments.add(uin);
			String queryParamName = "type";
			String queryParamValue = "demo";

			IdResponseDTO response = (IdResponseDTO) restClientService.getApi(ApiName.IDREPOSITORY, pathsegments,
					queryParamName, queryParamValue, IdResponseDTO.class);

			getArtifacts(response.getResponse().getEntity());

			InputStream uinArtifact = templateGenerator.getTemplate("", attributes, LocallangCode);

			OutputStream pdf = (OutputStream) uinCardGenerator.generateUinCard(uinArtifact, UinCardType.PDF);

		} catch (IOException | ParseException | ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		} catch (UINNotFoundInDatabase e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_PRT_PDF_GENERATION_FAILED.name() + e.getMessage()
							+ ExceptionUtils.getStackTrace(e));
		}

		return object;
	}

	/**
	 * Gets the artifacts.
	 *
	 * @param idJsonString the id json string
	 * @return the artifacts
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ParseException the parse exception
	 */
	private void getArtifacts(String idJsonString) throws IOException, ParseException {
		NotificationTemplate template = new NotificationTemplate();
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();

		regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
				RegistrationProcessorIdentity.class);
		JSONParser parser = new JSONParser();
		JSONObject demographicJson = (JSONObject) parser.parse(idJsonString);

		demographicIdentity = (JSONObject) demographicJson.get(utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());

		template.setFirstName(getJsonValues(regProcessorIdentityJson.getIdentity().getName().getValue()));
		template.setGender(getJsonValues(regProcessorIdentityJson.getIdentity().getGender().getValue()));

		template.setEmailID(
				(String) demographicIdentity.get(regProcessorIdentityJson.getIdentity().getEmail().getValue()));
		template.setPhoneNumber(
				(String) demographicIdentity.get(regProcessorIdentityJson.getIdentity().getPhone().getValue()));
		template.setDateOfBirth(
				(String) demographicIdentity.get(regProcessorIdentityJson.getIdentity().getDob().getValue()));
		template.setAddressLine1(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine1().getValue()));
		template.setAddressLine2(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine2().getValue()));
		template.setAddressLine3(getJsonValues(regProcessorIdentityJson.getIdentity().getAddressLine3().getValue()));
		template.setRegion(getJsonValues(regProcessorIdentityJson.getIdentity().getRegion().getValue()));
		template.setProvince(getJsonValues(regProcessorIdentityJson.getIdentity().getProvince().getValue()));
		template.setCity(getJsonValues(regProcessorIdentityJson.getIdentity().getCity().getValue()));
		template.setPostalCode(
				(String) demographicIdentity.get(regProcessorIdentityJson.getIdentity().getPostalCode().getValue()));

		setAtrributes(template);

	}

	/**
	 * Sets the atrributes.
	 *
	 * @param template the new atrributes
	 */
	private void setAtrributes(NotificationTemplate template) {
		attributes.put(UINCardConstant.NAME_ENG, getParameter(template.getFirstName(), ENG));
		attributes.put(UINCardConstant.NAME_ARA, getParameter(template.getFirstName(), ARA));
		attributes.put(UINCardConstant.GENDER_ENG, getParameter(template.getGender(), ENG));
		attributes.put(UINCardConstant.GENDER_ARA, getParameter(template.getGender(), ARA));
		attributes.put(UINCardConstant.DATEOFBIRTH, template.getDateOfBirth());
		attributes.put(UINCardConstant.ADDRESSLINE1_ENG, getParameter(template.getAddressLine1(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE1_ARA, getParameter(template.getAddressLine1(), ARA));
		attributes.put(UINCardConstant.ADDRESSLINE2_ENG, getParameter(template.getAddressLine2(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE2_ARA, getParameter(template.getAddressLine2(), ARA));
		attributes.put(UINCardConstant.ADDRESSLINE3_ENG, getParameter(template.getAddressLine3(), ENG));
		attributes.put(UINCardConstant.ADDRESSLINE3_ARA, getParameter(template.getAddressLine3(), ARA));
		attributes.put(UINCardConstant.REGION_ENG, getParameter(template.getRegion(), ENG));
		attributes.put(UINCardConstant.REGION_ARA, getParameter(template.getRegion(), ARA));
		attributes.put(UINCardConstant.PROVINCE_ENG, getParameter(template.getProvince(), ENG));
		attributes.put(UINCardConstant.PROVINCE_ARA, getParameter(template.getProvince(), ARA));
		attributes.put(UINCardConstant.CITY_ENG, getParameter(template.getCity(), ENG));
		attributes.put(UINCardConstant.CITY_ARA, getParameter(template.getCity(), ARA));
		attributes.put(UINCardConstant.POSTALCODE, template.getPostalCode());
		attributes.put(UINCardConstant.PHONENUMBER, template.getPhoneNumber());
		attributes.put(UINCardConstant.EMAILID, template.getEmailID());

	}

	/**
	 * Gets the parameter.
	 *
	 * @param jsonValues the json values
	 * @param langCode the lang code
	 * @return the parameter
	 */
	private String getParameter(JsonValue[] jsonValues, String langCode) {
		String parameter = null;
		if (jsonValues != null) {
			for (int count = 0; count < jsonValues.length; count++) {
				String lang = jsonValues[count].getLanguage();
				if (langCode.contains(lang)) {
					parameter = jsonValues[count].getValue();
					break;
				}
			}
		}
		return parameter;
	}

	/**
	 * Gets the json values.
	 *
	 * @param identityKey the identity key
	 * @return the json values
	 */
	private JsonValue[] getJsonValues(Object identityKey) {
		JSONArray demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = (JSONArray) demographicIdentity.get(identityKey);

		return (demographicJsonNode != null) ? mapJsonNodeToJavaObject(JsonValue.class, demographicJsonNode) : null;
	}

	/**
	 * Map json node to java object.
	 *
	 * @param <T>
	 *            the generic type
	 * @param genericType
	 *            the generic type
	 * @param demographicJsonNode
	 *            the demographic json node
	 * @return the t[]
	 */
	@SuppressWarnings("unchecked")
	private <T> T[] mapJsonNodeToJavaObject(Class<? extends Object> genericType, JSONArray demographicJsonNode) {
		String language;
		String value;
		T[] javaObject = (T[]) Array.newInstance(genericType, demographicJsonNode.size());
		try {
			for (int i = 0; i < demographicJsonNode.size(); i++) {

				T jsonNodeElement = (T) genericType.newInstance();

				JSONObject objects = (JSONObject) demographicJsonNode.get(i);
				language = (String) objects.get(LANGUAGE);
				value = (String) objects.get(VALUE);

				Field languageField = jsonNodeElement.getClass().getDeclaredField(LANGUAGE);
				languageField.setAccessible(true);
				languageField.set(jsonNodeElement, language);

				Field valueField = jsonNodeElement.getClass().getDeclaredField(VALUE);
				valueField.setAccessible(true);
				valueField.set(jsonNodeElement, value);

				javaObject[i] = jsonNodeElement;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "Error while Creating Instance of generic type" + ExceptionUtils.getStackTrace(e));
			throw new InstantanceCreationException(PlatformErrorMessages.RPR_SYS_INSTANTIATION_EXCEPTION.getMessage(),
					e);

		} catch (NoSuchFieldException | SecurityException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					null, "no such field exception" + ExceptionUtils.getStackTrace(e));
			throw new FieldNotFoundException(PlatformErrorMessages.RPR_SYS_NO_SUCH_FIELD_EXCEPTION.getMessage(), e);

		}

		return javaObject;

	}
	
	// Need clarify where to push the template
	public void sendMessage(MessageDTO messageDTO) {
		this.send(this.mosipEventBus, MessageBusAddress.VIRUS_SCAN_BUS_IN, messageDTO);
	}

	@Override
	public void start() {
		Router router = this.postUrl(vertx);
		this.routes(router);
		this.createServer(router, Integer.parseInt(port));
	}

	/**
	 * contains all the routes in the stage
	 * 
	 * @param router
	 */
	private void routes(Router router) {

		router.post("/v0.1/registration-processor/print-stage/resend").handler(ctx -> {
			reSendPrintPdf(ctx);
		}).failureHandler(failureHandler -> {
			this.setResponse(failureHandler, globalExceptionHandler.handler(failureHandler.failure()));
		});

		router.get("/print-stage/health").handler(ctx -> {
			this.setResponse(ctx, "Server is up and running");
		}).failureHandler(context -> {
			this.setResponse(context, context.failure().getMessage());
		});
	}

	private void reSendPrintPdf(RoutingContext ctx) {
		JsonObject object = ctx.getBodyAsJson();
		System.out.println(object.toString());
		MessageDTO messageDTO = new MessageDTO();
		this.setResponse(ctx, "Re-sending to Queue");
		// this.sendMessage(messageDTO);
	}

}
