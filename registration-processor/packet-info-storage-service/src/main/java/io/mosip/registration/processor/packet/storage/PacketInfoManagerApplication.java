package io.mosip.registration.processor.packet.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

/**
 * The Class PacketInfoManagerApplication.
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.auditmanager", "io.mosip.registration.processor.filesystem.ceph.adapter.impl",
		"io.mosip.registration.processor.rest.client" })

public class PacketInfoManagerApplication implements CommandLineRunner {

	@Autowired
	PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<FieldValue> metaDataList = new ArrayList<>();
		FieldValue regId = new FieldValue();
		regId.setLabel("registrationId");
		regId.setValue("2018782130000103122018100224");

		FieldValue preRegId = new FieldValue();
		preRegId.setLabel("preRegistrationId");
		preRegId.setValue("PEN1345T");

		metaDataList.add(regId);
		metaDataList.add(preRegId);

		File jsonFile = new File("..\\packet-info-storage-service\\src\\main\\resources\\DemographicInfo.json");
		InputStream demoJsonStream = new FileInputStream(jsonFile);
		packetInfoManager.saveDemographicInfoJson(demoJsonStream, metaDataList);

	}
}