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
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
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
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponsePKEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.utilities.RegistrationUtility;
import io.vertx.core.json.JsonObject;

public class AbisMiddleWareStage extends MosipVerticleManager {

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

	// private MosipQueue queue;
	private MosipEventBus mosipEventBus;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The type of queue. */
	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;
	/** The username. */
	@Value("${registration.processor.queue.username}")
	private String username;

	/** The password. */
	@Value("${registration.processor.queue.password}")
	private String password;

	@Value("${registration.processor.abis.inbound.queue1}")
	private String abisInboundAddress1;
	@Value("${registration.processor.abis.inbound.queue2}")
	private String abisInboundAddress2;
	@Value("${registration.processor.abis.inbound.queue3}")
	private String abisInboundAddress3;

	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress1;
	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress2;
	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress3;

	/** The url. */
	@Value("${registration.processor.queue.url}")
	private String url;

	@Autowired
	private RegistrationUtility registrationUtility;
	@Autowired
	private Utilities utility;
	@Autowired
	private RegistrationStatusDao registrationStatusDao;

	private static final String INSERT = "INSERT";
	private static final String IDENTIFY = "IDENTIFY";
	private static final String ABIS_INSERT = "mosip.abis.insert";

	private static final String ABIS_IDENTIFY = "mosip.abis.identify";
	private static final String ID = "id";
	private Map<Integer, String> failureReason = new HashMap<>();
	private static final String CANDIDATELIST = "candidateList";
	private static final String CANDIDATES = "candidates";
	private List<String> abisInboundAddresses;
	List<String> abisOutboundAddresses;
	List<String> queueUserNameList;
	List<String> queuePasswordList;
	List<String> queueUrlList;
	List<String> typeOfQueueList;
	List<MosipQueue> mosipQueueList;
	InternalRegistrationStatusDto registrationStatusDto;

	public void deployVerticle() {
		try {
			mosipQueueList = utility.getMosipQueuesForAbis();
			List<List<String>> inBoundOutBoundList = utility.getMInboundOutBoundAddressList();
			abisInboundAddresses = inBoundOutBoundList.get(0);
			abisOutboundAddresses = inBoundOutBoundList.get(1);
			for (int i = 0; i < abisOutboundAddresses.size(); i++) {
				QueueListener listener = new QueueListener() {
					@Override
					public void setListener(Message message) {
						consumerListener(message);
					}
				};
				mosipQueueManager.consume(mosipQueueList.get(i), abisOutboundAddresses.get(i), listener);
			}
			mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
			this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN,
					MessageBusAddress.ABIS_MIDDLEWARE_BUS_OUT);

		} catch (IOException e) {
		}
	}

	@Override
	public MessageDTO process(MessageDTO object) {

		List<String> abisRefList = packetInfoManager.getReferenceIdByRid(object.getRid());
		if (abisRefList != null && !abisRefList.isEmpty()) {
			try {
				String refRegtrnId = getLatestTransactionId(object.getRid());
				String abisRefId = abisRefList.get(0);
				List<AbisRequestDto> abisInsertIdentifyList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
						refRegtrnId);
				List<AbisRequestDto> abisInsertRequestList = abisInsertIdentifyList.stream()
						.filter(dto -> dto.getRequestType().equals(INSERT)).collect(Collectors.toList());
				List<AbisRequestDto> abisIdentifyRequestList = abisInsertIdentifyList.stream()
						.filter(dto -> dto.getRequestType().equals(IDENTIFY)).collect(Collectors.toList());
				if (abisInsertRequestList.isEmpty()) {
					object.setIsValid(true);
					object.setInternalError(false);
					return object;
				}
				for (int i = 0; i < abisInsertRequestList.size(); i++) {

					byte[] reqBytearray = abisInsertRequestList.get(i).getReqText();

					boolean isAddedToQueue = sendToQueue(mosipQueueList.get(i), new String(reqBytearray),
							abisInboundAddresses.get(i));

					updateAbisRequest(isAddedToQueue, abisInsertRequestList.get(i));
				}

				// List<AbisRequestDto> abisIdentifyRequestList =
				// packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
				// IDENTIFY, refRegtrnId);

				for (int i = 0; i < abisIdentifyRequestList.size(); i++) {
					byte[] identifyReq = abisIdentifyRequestList.get(i).getReqText();
					boolean isAddedToQueue = sendToQueue(mosipQueueList.get(i), new String(identifyReq),
							abisInboundAddresses.get(i));
					updateAbisRequest(isAddedToQueue, abisIdentifyRequestList.get(i));

				}

			} catch (IOException e) {

			}

		}

		return null;
	}

	public void consumerListener(Message message) {

		String response = new String(((ActiveMQBytesMessage) message).getContent().data);

		try {
			JSONObject jsonResponse = JsonUtil.objectMapperReadValue(response, JsonObject.class);
			String id = JsonUtil.getJSONValue(jsonResponse, ID);
			if (id.equals(ABIS_INSERT)) {

				AbisInsertResponseDto abisInsertResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisInsertResponseDto.class);

				updateAbisResponseEntity(abisInsertResponseDto, response);
				updteAbisRequestProcessed(abisInsertResponseDto.getRequestId(), INSERT);

			}

			if (id.equals(ABIS_IDENTIFY)) {

				AbisIdentifyResponseDto abisIdentifyResponseDto = JsonUtil.objectMapperReadValue(response,
						AbisIdentifyResponseDto.class);
				AbisResponseDto abisResponseDto = updateAbisResponseEntity(abisIdentifyResponseDto, response);
				CandidatesDto[] candidatesDtos = abisIdentifyResponseDto.getCandidateList().getCandidates();
				if (candidatesDtos != null) {
					for (CandidatesDto candidatesDto : candidatesDtos) {
						updateAbisResponseDetail(abisIdentifyResponseDto, candidatesDto, abisResponseDto);
					}
				}

				updteAbisRequestProcessed(abisIdentifyResponseDto.getRequestId(), IDENTIFY);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean sendToQueue(MosipQueue queue, String abisInsertRequestDto, String abisQueueAddress)
			throws IOException {
		boolean isAddedToQueue;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(abisInsertRequestDto);
		oos.flush();
		byte[] abisRequestDtoBytes = bos.toByteArray();
		isAddedToQueue = mosipQueueManager.send(queue, abisRequestDtoBytes, abisQueueAddress);

		return isAddedToQueue;
	}

	private void updateAbisRequest(boolean isAddedToQueue, AbisRequestDto abisRequestDto) {
		if (isAddedToQueue) {

			AbisRequestEntity abisReqEntity = convertAbisRequestDtoToAbisRequestEntity(abisRequestDto);
			abisReqEntity.setStatusCode("SENT");
			abisReqEntity.setStatusComment("Sent sucessfully to ABIS");
			abisRequestRepositary.update(abisReqEntity);
		}

	}

	private void updteAbisRequestProcessed(String abisRequestId, String requestType) {

	}

	private AbisRequestEntity convertAbisRequestDtoToAbisRequestEntity(AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = new AbisRequestEntity();
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

	private AbisResponseEntity updateAbisResponse(boolean isAddedToQueue, AbisResponseDto abisResponseDto) {
		if (isAddedToQueue) {

			AbisResponseEntity abisResponseEntity = new AbisResponseEntity();
			abisResponseEntity.setCrBy(abisResponseDto.getCrBy());
			abisResponseEntity.setIsDeleted(false);
			abisResponseEntity.setLangCode(abisResponseDto.getLangCode());
			abisResponseEntity.setRespDtimes(abisResponseDto.getRespDtimes());
			abisResponseEntity.setRespText(abisResponseDto.getRespText());
			abisResponseEntity.setStatusCode(abisResponseDto.getStatusCode());
			abisResponseEntity.setStatusComment(abisResponseDto.getStatusComment());
			abisResponseEntity.setUpdBy(abisResponseDto.getUpdBy());
			return abisResponseEntity;
		}
		return null;
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

		abisResponseDto.setId(registrationUtility.generateId());
		abisResponseDto.setRespText(response.getBytes());
		int responseStatus = abisCommonResponseDto.getReturnValue();

		abisResponseDto.setStatusCode(responseStatus == 1 ? "SUCCESS" : "FAILED");
		abisResponseDto.setStatusComment(getFaliureReason(abisCommonResponseDto.getFailureReason()));
		abisResponseDto.setLangCode("eng");
		abisResponseDto.setCrBy("SYSTEM");
		abisResponseDto.setUpdBy("SYSTEM");
		abisResponseDto.setIsDeleted(false);
		abisResponseDto.setAbisRequest(abisCommonResponseDto.getRequestId());

		abisResponseRepositary.save(convertAbisResponseDtoToAbisResponseEntity(abisResponseDto));

		return abisResponseDto;
	}

	private void updateAbisResponseDetail(AbisCommonResponseDto abisCommonResponseDto, CandidatesDto candidatesDto,
			AbisResponseDto abisResponseDto) {
		AbisResponseDetEntity abisResponseDetEntity = new AbisResponseDetEntity();
		AbisResponseDetPKEntity abisResponseDetPKEntity = new AbisResponseDetPKEntity();
		abisResponseDetPKEntity.setAbisRespId(abisResponseDto.getId());
		abisResponseDetPKEntity.setMatchedBioRefId(candidatesDto.getReferenceId());
		abisResponseDetEntity.setId(abisResponseDetPKEntity);
		abisResponseDetEntity.setScore(Integer.valueOf(candidatesDto.getScaledScore()));
		abisResponseDetEntity.setCrBy("SYSTEM");
		abisResponseDetEntity.setUpdBy("SYSTEM");
		abisResponseDetEntity.setIsDeleted(false);
		abisResponseDetailRepositary.save(abisResponseDetEntity);

	}

	private String getLatestTransactionId(String registrationId) {
		RegistrationStatusEntity entity = registrationStatusDao.findById(registrationId);
		return entity != null ? entity.getLatestRegistrationTransactionId() : null;

	}

}
