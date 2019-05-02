package io.mosip.registartion.processor.abis.middleware.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.packet.dto.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

@Service
@Transactional
public class AbisMiddleWareProcessor implements Runnable {

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	public MessageDTO process(MessageDTO object, String stageName) throws InterruptedException {
		AbisMiddleWareProcessor processor = new AbisMiddleWareProcessor();
		List<String> abisRefList = packetInfoManager.getReferenceIdByRid(object.getRid());
		if (abisRefList != null && !abisRefList.isEmpty()) {
			String abisRefId = abisRefList.get(0);
			List<AbisRequestDto> abisInsertRequestList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
					"INSERT");

			Thread t = null;
			for (AbisRequestDto dto : abisInsertRequestList) {
				t = new Thread(processor);
				t.start();
				// send dto to queue
			}
			t.join();

			List<AbisRequestDto> abisIdentifyRequestList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
					"IDENTIFY");
			for (AbisRequestDto dto : abisIdentifyRequestList) {
				t = new Thread(processor);
				t.start();
				// send dto to queue
			}
			t.join();
		}
		return null;
	}

	@Override
	public void run() {

	}

}
