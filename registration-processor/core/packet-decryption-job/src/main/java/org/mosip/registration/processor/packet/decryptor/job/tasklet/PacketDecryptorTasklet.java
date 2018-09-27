package org.mosip.registration.processor.packet.decryptor.job.tasklet;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.mosip.kernel.core.utils.exception.MosipIOException;
import org.mosip.registration.processor.filesystem.adapter.FileSystemAdapter;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.FilesystemCephAdapterImpl;
import org.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import org.mosip.registration.processor.packet.decryptor.job.Decryptor;
import org.mosip.registration.processor.packet.decryptor.job.MosipEventBus;
import org.mosip.registration.processor.status.code.RegistrationStatusCode;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

@Component
public class PacketDecryptorTasklet implements Tasklet {
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecryptorTasklet.class);

	private static final String LOGDISPLAY = "{} - {}";
	
	@Autowired
	RegistrationStatusService<String, RegistrationStatusDto> registrationStatusService;
	
	private FileSystemAdapter<InputStream, PacketFiles, Boolean> adapter = new FilesystemCephAdapterImpl();
	@Autowired
	private Decryptor decryptor;
	
	Vertx vertx = Vertx.vertx();
	EventBus eb = vertx.eventBus();
	MosipEventBus mosipEventBus=new MosipEventBus(eb);
	DeliveryOptions options = new DeliveryOptions();
	
	private static final String DFS_NOT_ACCESSIBLE = "The DFS Path set by the System is not accessible";
	
	private static final String  REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE = "The Registration Status table "
			+ "is not accessible";
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		List<RegistrationStatusDto> dtolist=null;
		
		try {
		dtolist=registrationStatusService.getByStatus(
				RegistrationStatusCode.PACKET_UPLOADED_TO_DFS.toString());
		
		if (!(dtolist.isEmpty())) {
			dtolist.forEach(dto -> {
				try {
				
					adapter.unpackPacket(dto.getRegistrationId());
					
					InputStream encryptedPacket=adapter.getPacket(dto.getRegistrationId());
					InputStream decryptedData=decryptor.decrypt(encryptedPacket,dto.getRegistrationId());
					
					if(decryptedData !=null ) {
						
						encryptedPacket.close();
						
						adapter.storePacket(dto.getRegistrationId(), decryptedData);
						
						adapter.unpackPacket(dto.getRegistrationId());
						
						dto.setStatusCode(RegistrationStatusCode.
								DECRYPTED_PACKET_STORED_IN_DFS.toString());
						registrationStatusService.updateRegistrationStatus(dto);
						
						
						options.addHeader("enrolmentId", dto.getRegistrationId());
						mosipEventBus.getEventBus().send("RegistrationProcessor", 
								"Packet Unpacked ", options);
						
						
						
						LOGGER.info(LOGDISPLAY,dto.getRegistrationId(),
								" Packet decrypted and extracted encrypted files stored in DFS.");
				}
					else {
						encryptedPacket.close();
						
						
						
						
						LOGGER.info(LOGDISPLAY,dto.getRegistrationId(),
								" Packet could not be  decrypted ");
					}
						
				}
				catch (TablenotAccessibleException e) {
					
					LOGGER.error(LOGDISPLAY,REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
					
				}catch(SdkClientException |IOException  e) {
					
					LOGGER.error(LOGDISPLAY, DFS_NOT_ACCESSIBLE, e);
				
				} 
			});
		} else if (dtolist.isEmpty()) {
			
			LOGGER.info("There are currently no files to be decrypted");
		}
		}
		catch (TablenotAccessibleException e) {
			
			LOGGER.error(LOGDISPLAY,REGISTRATION_STATUS_TABLE_NOT_ACCESSIBLE, e);
		}
		return RepeatStatus.FINISHED;
	}
}
