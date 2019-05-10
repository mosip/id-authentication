package io.mosip.registartion.processor.abis.middleware.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jms.Message;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorUnCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisCommonResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.CandidatesDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponsePKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.mosip.registration.processor.status.utilities.RegistrationUtility;

public class AbisMiddleWareStage extends MosipVerticleManager {
	private static Logger regProcLogger = RegProcessorLogger.getLogger(AbisMiddleWareStage.class);

	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepositary;

	@Autowired
	private BasePacketRepository<AbisResponseEntity, String> abisResponseRepositary;

	@Autowired
	private BasePacketRepository<AbisResponseDetEntity, String> abisResponseDetailRepositary;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The url. */
	@Value("${registration.processor.queue.url}")
	private String url;

	@Autowired
	private Utilities utility;
	@Autowired
	private RegistrationStatusDao registrationStatusDao;
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;
	private static final String INSERT = "INSERT";
	private static final String IDENTIFY = "IDENTIFY";
	private static final String PROCESSED = "PROCESSED";
	private static final String SYSTEM = "SYSTEM";

	private Map<Integer, String> failureReason = new HashMap<>();
	private List<String> abisInboundAddresses;
	List<String> abisOutboundAddresses;
	List<String> queueUserNameList;
	List<String> queuePasswordList;
	List<String> queueUrlList;
	List<String> typeOfQueueList;
	List<MosipQueue> mosipQueueList;
	InternalRegistrationStatusDto registrationStatusDto;
	List<AbisRequestDto> abisInsertRequestList;
	List<AbisRequestDto> abisIdentifyRequestList;
	String registrationId;

	public void deployVerticle() throws RegistrationProcessorCheckedException {
		try {
			mosipQueueList = utility.getMosipQueuesForAbis();
			List<List<String>> inBoundOutBoundList = utility.getInboundOutBoundAddressList();
			abisInboundAddresses = inBoundOutBoundList.get(0);
			abisOutboundAddresses = inBoundOutBoundList.get(1);
			for (int i = 0; i < abisOutboundAddresses.size(); i++) {
				String abisInBoundaddress = abisInboundAddresses.get(i);
				MosipQueue queue = mosipQueueList.get(i);
				QueueListener listener = new QueueListener() {
					@Override
					public void setListener(Message message) {
						try {
							consumerListener(message, abisInBoundaddress, queue);
						} catch (RegistrationProcessorUnCheckedException | RegistrationProcessorCheckedException e) {
							registrationStatusDto.setRegistrationId(registrationId);
							registrationStatusDto.setLatestTransactionStatusCode(
									RegistrationTransactionStatusCode.REPROCESS.toString());
							registrationStatusDto
									.setStatusComment(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage());
							regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
									LoggerFileConstant.REGISTRATIONID.toString(), "", ExceptionUtils.getStackTrace(e));
							registrationStatusService.updateRegistrationStatus(registrationStatusDto);

						}
					}
				};
				mosipQueueManager.consume(queue, abisOutboundAddresses.get(i), listener);
			}

		} catch (Exception e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}

		MessageDTO dto = new MessageDTO();
		dto.setRid("80655732150002920190122164907");
		process(dto);
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		object.setIsValid(false);
		object.setInternalError(false);
		boolean isTransactionSuccessful = false;
		String description = "";
		registrationId = object.getRid();
		String exceptionMesaage = "";
		registrationStatusDto.setRegistrationId(registrationId);

		List<String> abisRefList = packetInfoManager.getReferenceIdByRid(registrationId);
		if (abisRefList == null || abisRefList != null && abisRefList.isEmpty()) {
			exceptionMesaage = "Abis reference id not found";
			throw new RegistrationProcessorUnCheckedException(
					PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getCode(),
					PlatformErrorMessages.ABIS_REFERENCE_ID_NOT_FOUND.getMessage());
		}

		try {
			String refRegtrnId = getLatestTransactionId(registrationId);
			if (refRegtrnId == null) {
				exceptionMesaage = "latest transactionId not found";
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.LATEST_TRANSACTION_ID_NOT_FOUND.getCode(),
						PlatformErrorMessages.LATEST_TRANSACTION_ID_NOT_FOUND.getMessage());
			}

			String abisRefId = abisRefList.get(0);
			List<AbisRequestDto> abisInsertIdentifyList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
					refRegtrnId);
			if (abisInsertIdentifyList.isEmpty()) {
				exceptionMesaage = "Identify requests not found for abisrefId" + abisRefId;
				throw new RegistrationProcessorUnCheckedException(
						PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getCode(),
						PlatformErrorMessages.IDENTIFY_REQUESTS_NOT_FOUND.getMessage());
			}

			abisInsertRequestList = abisInsertIdentifyList.stream().filter(dto -> dto.getRequestType().equals(INSERT))
					.collect(Collectors.toList());
			abisIdentifyRequestList = abisInsertIdentifyList.stream()
					.filter(dto -> dto.getRequestType().equals(IDENTIFY)).collect(Collectors.toList());

			for (int i = 0; i < abisInsertRequestList.size(); i++) {

				byte[] reqBytearray = abisInsertRequestList.get(i).getReqText();

				boolean isAddedToQueue = sendToQueue(mosipQueueList.get(i), new String(reqBytearray),
						abisInboundAddresses.get(i));

				updateAbisRequest(isAddedToQueue, abisInsertRequestList.get(i));
			}
			object.setIsValid(true);
			object.setInternalError(false);
			isTransactionSuccessful = true;
			description = "Abis insertRequests sucessfully sent to Queue";

		} catch (RegistrationProcessorUnCheckedException | RegistrationProcessorCheckedException e) {
			object.setInternalError(true);
			object.setIsValid(false);
			description = exceptionMesaage;
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			registrationStatusDto.setStatusComment(exceptionMesaage);
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
		} catch (Exception e) {
			object.setInternalError(true);
			object.setIsValid(false);
			description = exceptionMesaage;
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			registrationStatusDto.setStatusComment("Unknown exception occured in abis middle ware");
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, ExceptionUtils.getStackTrace(e));
		} finally {
			registrationStatusService.updateRegistrationStatus(registrationStatusDto);

			String eventId = isTransactionSuccessful ? EventId.RPR_402.toString() : EventId.RPR_405.toString();
			String eventName = isTransactionSuccessful ? EventName.UPDATE.toString() : EventName.EXCEPTION.toString();
			String eventType = isTransactionSuccessful ? EventType.BUSINESS.toString() : EventType.SYSTEM.toString();

			String moduleId = isTransactionSuccessful ? "Abis-MiddleWare Success" : "";
			String moduleName = "Abis-MiddleWare";

			auditLogRequestBuilder.createAuditRequestBuilder(description, eventId, eventName, eventType, moduleId,
					moduleName, registrationId);
		}

		return object;
	}

	public void consumerListener(Message message, String abisInBoundAddress, MosipQueue queue)
			throws RegistrationProcessorCheckedException {

		String response = new String(((ActiveMQBytesMessage) message).getContent().data);
		try {
			AbisCommonResponseDto abisCommonResponseDto = JsonUtil.objectMapperReadValue(response,
					AbisCommonResponseDto.class);
			AbisRequestDto abisCommonRequestDto = packetInfoManager
					.getAbisRequestByRequestId(abisCommonResponseDto.getRequestId());
			if (abisCommonRequestDto.getRequestType().equals(INSERT)) {
				AbisInsertResponseDto abisInsertResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisInsertResponseDto.class);

				updateAbisResponseEntity(abisInsertResponseDto, response);
				updteAbisRequestProcessed(abisInsertResponseDto.getRequestId());
				if (abisInsertResponseDto.getReturnValue() == 1) {
					List<AbisRequestDto> temp = abisIdentifyRequestList.stream()
							.filter(dto1 -> dto1.getAbisAppCode().equals(abisCommonRequestDto.getAbisAppCode()))
							.collect(Collectors.toList());
					AbisRequestDto abisIdentifyRequestDto = temp.get(0);
					boolean isAddedToQueue = sendToQueue(queue, new String(abisIdentifyRequestDto.getReqText()),
							abisInBoundAddress);
					updateAbisRequest(isAddedToQueue, abisIdentifyRequestDto);
				}
			}
			if (abisCommonRequestDto.getRequestType().equals(IDENTIFY)) {

				AbisIdentifyResponseDto abisIdentifyResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisIdentifyResponseDto.class);
				AbisResponseDto abisResponseDto = updateAbisResponseEntity(abisIdentifyResponseDto, response);
				CandidatesDto[] candidatesDtos = abisIdentifyResponseDto.getCandidateList().getCandidates();
				for (CandidatesDto candidatesDto : candidatesDtos) {
					updateAbisResponseDetail(candidatesDto, abisResponseDto);
				}

				updteAbisRequestProcessed(abisIdentifyResponseDto.getRequestId());
				String batchId = packetInfoManager.getBatchIdByRequestId(abisIdentifyResponseDto.getRequestId());
				if (checkAllIdentifyRequestsProcessed(batchId)) {
					MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
					this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN,
							MessageBusAddress.ABIS_MIDDLEWARE_BUS_OUT);
				}

			}

		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}

	}

	private boolean sendToQueue(MosipQueue queue, String abisReqTextString, String abisQueueAddress)
			throws RegistrationProcessorCheckedException {
		boolean isAddedToQueue;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(abisReqTextString);
			oos.flush();

			byte[] abisRequestDtoBytes = bos.toByteArray();
			isAddedToQueue = mosipQueueManager.send(queue, abisRequestDtoBytes, abisQueueAddress);
		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}

		return isAddedToQueue;
	}

	private void updateAbisRequest(boolean isAddedToQueue, AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = convertAbisRequestDtoToAbisRequestEntity(abisRequestDto);

		if (isAddedToQueue) {

			abisReqEntity.setStatusCode("SENT");
			abisReqEntity.setStatusComment("Request sent to ABIS is sucessful");
		} else {
			abisReqEntity.setStatusCode("FAILED");
			abisReqEntity.setStatusComment("Request sent to ABIS is unsucessful");
			registrationStatusDto
					.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
			registrationStatusDto.setStatusComment("Request sent to ABIS is unsucessful");
		}
		abisRequestRepositary.save(abisReqEntity);

	}

	private void updteAbisRequestProcessed(String abisRequestId) {
		abisRequestRepositary.updateAbisRequestStatusCode(abisRequestId);
	}

	private AbisRequestEntity convertAbisRequestDtoToAbisRequestEntity(AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = new AbisRequestEntity();
		AbisRequestPKEntity abisReqPKEntity = new AbisRequestPKEntity();
		abisReqPKEntity.setId(abisRequestDto.getId());
		abisReqEntity.setId(abisReqPKEntity);
		abisReqEntity.setAbisAppCode(abisRequestDto.getAbisAppCode());
		abisReqEntity.setBioRefId(abisRequestDto.getBioRefId());
		abisReqEntity.setCrBy(abisRequestDto.getCrBy());
		abisReqEntity.setIsDeleted(false);
		abisReqEntity.setLangCode(abisRequestDto.getLangCode());
		abisReqEntity.setRefRegtrnId(abisRequestDto.getRefRegtrnId());
		abisReqEntity.setReqBatchId(abisRequestDto.getReqBatchId());
		abisReqEntity.setReqText(abisRequestDto.getReqText());
		abisReqEntity.setRequestDtimes(abisRequestDto.getRequestDtimes());
		abisReqEntity.setRequestType(abisRequestDto.getRequestType());
		abisReqEntity.setStatusCode(abisRequestDto.getStatusCode());
		abisReqEntity.setStatusComment(abisRequestDto.getStatusComment());
		abisReqEntity.setUpdBy(abisRequestDto.getUpdBy());

		return abisReqEntity;

	}

	private AbisResponseEntity convertAbisResponseDtoToAbisResponseEntity(AbisResponseDto abisResponseDto) {
		AbisResponseEntity abisResponseEntity = new AbisResponseEntity();
		AbisResponsePKEntity abisResponsePKEntity = new AbisResponsePKEntity();
		abisResponsePKEntity.setId(abisResponseDto.getId());
		abisResponseEntity.setId(abisResponsePKEntity);
		abisResponseEntity.setAbisRequest(abisResponseDto.getAbisRequest());
		abisResponseEntity.setRespText(abisResponseDto.getRespText());
		abisResponseEntity.setStatusCode(abisResponseDto.getStatusCode());
		abisResponseEntity.setStatusComment(abisResponseDto.getStatusComment());
		abisResponseEntity.setLangCode(abisResponseDto.getLangCode());
		abisResponseEntity.setCrBy(abisResponseDto.getCrBy());
		abisResponseEntity.setUpdBy(abisResponseDto.getUpdBy());
		abisResponseEntity.setIsDeleted(abisResponseDto.getIsDeleted());

		return abisResponseEntity;

	}

	private String getFaliureReason(int key) {
		failureReason.put(1, "Internal error - Unknown");
		failureReason.put(2, "Aborted");
		failureReason.put(3, "Unexpected error - Unable to access biometric data");
		failureReason.put(4, "Unable to serve the request");
		failureReason.put(5, "Invalid request / Missing mandatory fields");
		failureReason.put(6, "Unauthorized Access");
		failureReason.put(7, "Unable to fetch biometric details");
		return failureReason.get(key);

	}

	private AbisResponseDto updateAbisResponseEntity(AbisCommonResponseDto abisCommonResponseDto, String response) {
		AbisResponseDto abisResponseDto = new AbisResponseDto();

		abisResponseDto.setId(RegistrationUtility.generateId());
		abisResponseDto.setRespText(response.getBytes());
		int responseStatus = abisCommonResponseDto.getReturnValue();

		abisResponseDto.setStatusCode(responseStatus == 1 ? "SUCCESS" : "FAILED");
		abisResponseDto.setStatusComment(getFaliureReason(abisCommonResponseDto.getFailureReason()));
		abisResponseDto.setLangCode("eng");
		abisResponseDto.setCrBy(SYSTEM);
		abisResponseDto.setUpdBy(SYSTEM);
		abisResponseDto.setIsDeleted(false);
		abisResponseDto.setAbisRequest(abisCommonResponseDto.getRequestId());

		abisResponseRepositary.save(convertAbisResponseDtoToAbisResponseEntity(abisResponseDto));

		return abisResponseDto;
	}

	private void updateAbisResponseDetail(CandidatesDto candidatesDto, AbisResponseDto abisResponseDto) {
		AbisResponseDetEntity abisResponseDetEntity = new AbisResponseDetEntity();
		AbisResponseDetPKEntity abisResponseDetPKEntity = new AbisResponseDetPKEntity();
		abisResponseDetPKEntity.setAbisRespId(abisResponseDto.getId());
		abisResponseDetPKEntity.setMatchedBioRefId(candidatesDto.getReferenceId());
		abisResponseDetEntity.setId(abisResponseDetPKEntity);
		abisResponseDetEntity.setScore(Integer.valueOf(candidatesDto.getScaledScore()));
		abisResponseDetEntity.setCrBy(SYSTEM);
		abisResponseDetEntity.setUpdBy(SYSTEM);
		abisResponseDetEntity.setIsDeleted(false);
		abisResponseDetailRepositary.save(abisResponseDetEntity);

	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

	private boolean checkAllIdentifyRequestsProcessed(String batchId) {
		List<String> batchStatus = packetInfoManager.getBatchStatusbyBatchId(batchId);
		if (batchStatus != null) {
			boolean flag = batchStatus.stream().allMatch(status -> status.equals(PROCESSED));
			if (flag)
				return true;
		}
		return false;
	}

}
