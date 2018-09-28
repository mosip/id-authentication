package org.mosip.registration.service;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.assertj.core.util.Sets;
import org.mosip.registration.constants.RegClientStatusCode;
import org.mosip.registration.entity.Registration;
import org.mosip.registration.entity.RegistrationTransaction;
import org.mosip.registration.repositories.RegTransactionRepository;
import org.mosip.registration.repositories.RegistrationRepository;
import org.mosip.registration.util.common.PacketUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PacketUploadService {
	
	private static final Set<String> PACKET_STATUS = Sets.newHashSet(Arrays.asList("I","H","A","S"));

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private RegTransactionRepository regTransactionRepository;

	public List<File> verifyPacket(List<String> packetNames, Map<String, File> packetMap) {
		List<Registration> packetList = registrationRepository.findByIdIn(packetNames);
		List<File> verifiedPackets = new ArrayList<>();
		for (Registration packetDet : packetList) {
			if (PACKET_STATUS.contains(packetDet.getClientStatusCode())) {
				verifiedPackets.add(packetMap.get(packetDet.getId()));
			}
		}
		return verifiedPackets;
	}

	public Boolean updateStatus(List<File> uploadedPackets) {
		List<String> fileNames = new ArrayList<>();
		List<RegistrationTransaction> registrationTransactions = new ArrayList<>();
		PacketUtil packetUtil = new PacketUtil();
		fileNames = packetUtil.getPacketNames(uploadedPackets);
		for(String packetName: fileNames) {
			updateRegStatus(packetName);
		}
		for (String id : fileNames) {
			OffsetDateTime time = OffsetDateTime.now();
			registrationTransactions.add(buildRegTrans(id, time));
		}
		regTransactionRepository.saveAll(registrationTransactions);
		return true;

	}
	
	private void updateRegStatus(String regId) {
		Registration reg = registrationRepository.getOne(regId);
		reg.setClientStatusCode("P");
		registrationRepository.update(reg);
	}

	private RegistrationTransaction buildRegTrans(String regId, OffsetDateTime time) {
		RegistrationTransaction regTransaction = new RegistrationTransaction();
		regTransaction.setId(String.valueOf(UUID.randomUUID().getMostSignificantBits()));
		regTransaction.setRegId(regId);
		regTransaction.setTrnTypeCode(RegClientStatusCode.CREATED.toString());
		regTransaction.setStatusCode(RegClientStatusCode.CREATED.toString());
		regTransaction.setCrBy("mosip");
		regTransaction.setCrDtime(time);
		return regTransaction;
	}
}
