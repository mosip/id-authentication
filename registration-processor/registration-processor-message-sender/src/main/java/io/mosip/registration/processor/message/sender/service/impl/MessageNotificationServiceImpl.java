package io.mosip.registration.processor.message.sender.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDetails;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfo;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationRequest;
import io.mosip.registration.processor.message.sender.dto.MessageNotificationResponse;
import io.mosip.registration.processor.message.sender.service.MessageNotificationService;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

public class MessageNotificationServiceImpl implements MessageNotificationService{
	@Autowired
	private FileSystemAdapter<InputStream, Boolean> adapter;
	@Autowired
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> basePacketRepository;
	/** The Constant FILE_SEPARATOR. */
	public static final String FILE_SEPARATOR = "\\";

	
	public MessageNotificationResponse sendSmsNotification(MessageNotificationRequest messageNotificationRequest) {
		MessageNotificationResponse messageNotificationResponse=new MessageNotificationResponse();
		if (messageNotificationRequest != null) {
			try {
				if(messageNotificationRequest.getRid().equalsIgnoreCase("RID")){
					InputStream demographicInfoStream = adapter.getFile(messageNotificationRequest.getRid(), PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
					DemographicInfo demographicInfo = (DemographicInfo) JsonUtil.inputStreamtoJavaObject(demographicInfoStream,DemographicInfo.class);
					for(DemographicDetails dto:demographicInfo.getIdentity().getMobileNumber()) {
							dto.getValue();
					}
				}else if(messageNotificationRequest.getIdType().equalsIgnoreCase("UIN")) {
					IndividualDemographicDedupeEntity individualDemographicDedupeEntity=basePacketRepository.getRegId(messageNotificationRequest.getRid());
					InputStream packetMetaInfoStream = adapter.getFile(individualDemographicDedupeEntity.getId().getRegId(),PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.DEMOGRAPHICINFO.name());
					DemographicInfo demographicInfo = (DemographicInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,DemographicInfo.class);
					for(DemographicDetails dto:demographicInfo.getIdentity().getMobileNumber()) {
						dto.getValue();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return messageNotificationResponse;
	}
}
