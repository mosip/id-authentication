package io.mosip.registration.processor.reprocessor.stage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.ModuleName;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.code.RegistrationTransactionTypeCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.RegistrationType;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.exception.util.PlatformSuccessMessages;
import io.mosip.registration.processor.core.logger.LogDescription;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.util.MessageBusUtil;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.retry.verticle.constants.ReprocessorConstants;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * The Reprocessor Stage to deploy the scheduler and implement re-processing
 * logic
 * 
 * @author Alok Ranjan
 * @author Sowmya
 * @author Pranav Kumar
 * 
 * @since 0.10.0
 *
 */
public class ReprocessorStage extends MosipVerticleManager {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(ReprocessorStage.class);

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** The environment. */
	@Autowired
	Environment environment;

	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;

	/** The fetch size. */
	@Value("${registration.processor.reprocess.fetchsize}")
	private Integer fetchSize;

	/** The elapse time. */
	@Value("${registration.processor.reprocess.elapse.time}")
	private long elapseTime;

	/** The reprocess count. */
	@Value("${registration.processor.reprocess.attempt.count}")
	private Integer reprocessCount;

	/** The is transaction successful. */
	boolean isTransactionSuccessful;

	/** The registration status service. */
	@Autowired
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Autowired
	AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration id. */
	private String registrationId = "";

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this, clusterManagerUrl);
		deployScheduler(mosipEventBus.getEventbus());

	}

	/**
	 * This method deploys the chime scheduler
	 *
	 * @param vertx
	 *            the vertx
	 */
	private void deployScheduler(Vertx vertx) {
		vertx.deployVerticle(ReprocessorConstants.CEYLON_SCHEDULER, this::schedulerResult);
	}

	public void schedulerResult(AsyncResult<String> res) {
		if (res.succeeded()) {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "ReprocessorStage::schedular()::deployed");
			cronScheduling(vertx);
		} else {
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", "ReprocessorStage::schedular()::deploymemnt failure");
		}
	}

	/**
	 * This method does the cron scheduling by fetchin cron expression from config
	 * server
	 *
	 * @param vertx
	 *            the vertx
	 */
	private void cronScheduling(Vertx vertx) {

		EventBus eventBus = vertx.eventBus();
		// listen the timer events
		eventBus.consumer((ReprocessorConstants.TIMER_EVENT), message -> {

			process(new MessageDTO());
		});

		// description of timers
		JsonObject timer = (new JsonObject())
				.put(ReprocessorConstants.TYPE, environment.getProperty(ReprocessorConstants.TYPE_VALUE))
				.put(ReprocessorConstants.SECONDS, environment.getProperty(ReprocessorConstants.SECONDS_VALUE))
				.put(ReprocessorConstants.MINUTES, environment.getProperty(ReprocessorConstants.MINUTES_VALUE))
				.put(ReprocessorConstants.HOURS, environment.getProperty(ReprocessorConstants.HOURS_VALUE))
				.put(ReprocessorConstants.DAY_OF_MONTH,
						environment.getProperty(ReprocessorConstants.DAY_OF_MONTH_VALUE))
				.put(ReprocessorConstants.MONTHS, environment.getProperty(ReprocessorConstants.MONTHS_VALUE))
				.put(ReprocessorConstants.DAYS_OF_WEEK,
						environment.getProperty(ReprocessorConstants.DAYS_OF_WEEK_VALUE));

		// create scheduler
		eventBus.send(ReprocessorConstants.CHIME,
				(new JsonObject()).put(ReprocessorConstants.OPERATION, ReprocessorConstants.OPERATION_VALUE)
						.put(ReprocessorConstants.NAME, ReprocessorConstants.NAME_VALUE)
						.put(ReprocessorConstants.DESCRIPTION, timer),
				ar -> {
					if (ar.succeeded()) {
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), "",
								"ReprocessorStage::schedular()::started");
					} else {
						regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), "",
								"ReprocessorStage::schedular()::failed");
						vertx.close();
					}
				});

	}

	/**
	 * Send message.
	 *
	 * @param message
	 *            the message
	 * @param toAddress
	 *            the to address
	 */
	public void sendMessage(MessageDTO message, MessageBusAddress toAddress) {
		this.send(this.mosipEventBus, toAddress, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */
	@Override
	public MessageDTO process(MessageDTO object) {
		List<InternalRegistrationStatusDto> dtolist = null;
		LogDescription description = new LogDescription();
		List<String> statusList = new ArrayList<>();
		statusList.add(RegistrationTransactionStatusCode.SUCCESS.toString());
		statusList.add(RegistrationTransactionStatusCode.REPROCESS.toString());
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"ReprocessorStage::process()::entry");
		try {
			Integer totalUnprocessesPackets = registrationStatusService.getUnProcessedPacketsCount(elapseTime,
					reprocessCount, statusList);

			while (totalUnprocessesPackets > 0) {
				dtolist = registrationStatusService.getUnProcessedPackets(fetchSize, elapseTime, reprocessCount,
						statusList);
				if (!(dtolist.isEmpty())) {
					dtolist.forEach(dto -> {
						this.registrationId = dto.getRegistrationId();
						if (reprocessCount.equals(dto.getReProcessRetryCount())) {
							dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.FAILED.toString());
							dto.setLatestTransactionTypeCode(
									RegistrationTransactionTypeCode.PACKET_REPROCESS.toString());
							dto.setStatusComment("Reprocess count has exceeded the configured attempts");
							dto.setStatusCode(RegistrationStatusCode.FAILED.toString());
							object.setRid(registrationId);
							object.setIsValid(false);
							object.setReg_type(RegistrationType.valueOf(dto.getRegistrationType()));
							description.setMessage(PlatformSuccessMessages.RPR_RE_PROCESS_FAILED.getMessage());

						} else {
							object.setRid(registrationId);
							object.setIsValid(true);
							object.setReg_type(RegistrationType.valueOf(dto.getRegistrationType()));
							isTransactionSuccessful = true;
							String stageName = MessageBusUtil.getMessageBusAdress(dto.getRegistrationStageName());
							if (RegistrationTransactionStatusCode.REPROCESS.name()
									.equalsIgnoreCase(dto.getLatestTransactionStatusCode())) {
								stageName = stageName.concat(ReprocessorConstants.BUS_IN);
							} else {
								stageName = stageName.concat(ReprocessorConstants.BUS_OUT);
							}
							MessageBusAddress address = new MessageBusAddress(stageName);
							sendMessage(object, address);
							dto.setUpdatedBy(ReprocessorConstants.USER);
							Integer reprocessRetryCount = dto.getReProcessRetryCount() != null
									? dto.getReProcessRetryCount() + 1
									: 1;
							dto.setReProcessRetryCount(reprocessRetryCount);
							dto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
							dto.setLatestTransactionTypeCode(
									RegistrationTransactionTypeCode.PACKET_REPROCESS.toString());
							dto.setStatusComment("Reprocess Completed");

							description.setMessage(PlatformSuccessMessages.RPR_SENT_TO_REPROCESS_SUCCESS.getMessage());

						}
						regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
								LoggerFileConstant.REGISTRATIONID.toString(), registrationId, description.getMessage());
						registrationStatusService.updateRegistrationStatus(dto);
						String eventId = EventId.RPR_402.toString();
						String eventName = EventName.UPDATE.toString();
						String eventType = EventType.BUSINESS.toString();

						/** Module-Id can be Both Success/Error code */
						String moduleId = PlatformSuccessMessages.RPR_SENT_TO_REPROCESS_SUCCESS.getCode();
						String moduleName = ModuleName.RE_PROCESSOR.toString();
						auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName,
								eventType, moduleId, moduleName, registrationId);
					});

					totalUnprocessesPackets = totalUnprocessesPackets - fetchSize;
				}

			}

		} catch (TablenotAccessibleException e) {
			isTransactionSuccessful = false;
			object.setInternalError(Boolean.TRUE);
			description.setMessage(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage());
			description.setCode(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
					description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e.toString());

		} catch (Exception ex) {
			isTransactionSuccessful = false;
			description.setMessage(PlatformErrorMessages.REPROCESSOR_STAGE_FAILED.getMessage());
			description.setCode(PlatformErrorMessages.REPROCESSOR_STAGE_FAILED.getCode());
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					description.getCode() + " -- " + registrationId,
					PlatformErrorMessages.STRUCTURAL_VALIDATION_FAILED.getMessage() + ex.getMessage()
							+ ExceptionUtils.getStackTrace(ex));
			object.setInternalError(Boolean.TRUE);

		} finally {
			regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, description.getMessage());
			if (isTransactionSuccessful)
				description.setMessage(PlatformSuccessMessages.RPR_RE_PROCESS_SUCCESS.getMessage());

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			/** Module-Id can be Both Success/Error code */
			String moduleId = isTransactionSuccessful ? PlatformSuccessMessages.RPR_RE_PROCESS_SUCCESS.getCode()
					: description.getCode();
			String moduleName = ModuleName.RE_PROCESSOR.toString();
			auditLogRequestBuilder.createAuditRequestBuilder(description.getMessage(), eventId, eventName, eventType,
					moduleId, moduleName, registrationId);
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				description.getCode(), "PacketValidatorStage::process()::exit");
		return object;
	}
}
