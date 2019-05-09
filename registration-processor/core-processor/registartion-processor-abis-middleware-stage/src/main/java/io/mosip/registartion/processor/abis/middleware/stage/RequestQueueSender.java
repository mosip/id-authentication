package io.mosip.registartion.processor.abis.middleware.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

@Component
public class RequestQueueSender implements Callable<Boolean> {
	private MosipQueue queue;
	private String insertReqBytearray;
	private String abisInBoundQueueAddress;

	private String identifyReqBytearray;
	private String abisOutBoundQueueAddress;

	private AbisRequestDto abisInsertRequestDto;
	private AbisRequestDto abisIdentifyRequestDto;
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;
	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepositary;

	public RequestQueueSender(MosipQueue queue, String insertReqBytearray, String abisInBoundQueueAddress,
			String identifyReqBytearray, String abisOutBoundQueueAddress, AbisRequestDto abisInsertRequestDto,
			AbisRequestDto abisIdentifyRequestDto) {
		super();
		this.queue = queue;
		this.insertReqBytearray = insertReqBytearray;
		this.abisInBoundQueueAddress = abisInBoundQueueAddress;
		this.identifyReqBytearray = identifyReqBytearray;
		this.abisOutBoundQueueAddress = abisOutBoundQueueAddress;
		this.abisInsertRequestDto = abisInsertRequestDto;
		this.abisIdentifyRequestDto = abisIdentifyRequestDto;
	}

	public MosipQueue getQueue() {
		return queue;
	}

	public void setQueue(MosipQueue queue) {
		this.queue = queue;
	}

	public String getInsertReqBytearray() {
		return insertReqBytearray;
	}

	public void setInsertReqBytearray(String insertReqBytearray) {
		this.insertReqBytearray = insertReqBytearray;
	}

	public String getAbisInBoundQueueAddress() {
		return abisInBoundQueueAddress;
	}

	public void setAbisInBoundQueueAddress(String abisInBoundQueueAddress) {
		this.abisInBoundQueueAddress = abisInBoundQueueAddress;
	}

	public String getIdentifyReqBytearray() {
		return identifyReqBytearray;
	}

	public void setIdentifyReqBytearray(String identifyReqBytearray) {
		this.identifyReqBytearray = identifyReqBytearray;
	}

	public String getAbisOutBoundQueueAddress() {
		return abisOutBoundQueueAddress;
	}

	public void setAbisOutBoundQueueAddress(String abisOutBoundQueueAddress) {
		this.abisOutBoundQueueAddress = abisOutBoundQueueAddress;
	}

	public AbisRequestDto getAbisInsertRequestDto() {
		return abisInsertRequestDto;
	}

	public void setAbisInsertRequestDto(AbisRequestDto abisInsertRequestDto) {
		this.abisInsertRequestDto = abisInsertRequestDto;
	}

	public AbisRequestDto getAbisIdentifyRequestDto() {
		return abisIdentifyRequestDto;
	}

	public void setAbisIdentifyRequestDto(AbisRequestDto abisIdentifyRequestDto) {
		this.abisIdentifyRequestDto = abisIdentifyRequestDto;
	}

	@Override
	public Boolean call() throws RegistrationProcessorCheckedException {
		boolean insertCheck = sendToQueue(queue, insertReqBytearray, abisInBoundQueueAddress);
		boolean identifyCheck = false;
		updateAbisRequest(insertCheck, abisInsertRequestDto);
		if (insertCheck) {
			identifyCheck = sendToQueue(queue, identifyReqBytearray, abisOutBoundQueueAddress);
			updateAbisRequest(identifyCheck, abisIdentifyRequestDto);
		}
		return identifyCheck;
	}

	private boolean sendToQueue(MosipQueue queue, String abisInsertRequestDto, String abisQueueAddress)
			throws RegistrationProcessorCheckedException {
		boolean isAddedToQueue;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(abisInsertRequestDto);
			oos.flush();
		} catch (IOException e) {
			throw new RegistrationProcessorCheckedException(PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage(), e);
		}

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

}
