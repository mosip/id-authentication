package io.mosip.registration.processor.packet.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", 
		"io.mosip.registration.processor.core",
		"io.mosip.kernel.auditmanager"})

@PropertySource({"classpath:packet-meta-application.properties"})
public class PacketInfoManagerApplication implements CommandLineRunner  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerApplication.class);
	@Autowired
	private PacketInfoManager<PacketInfo, DemographicInfo> packetInfoManager;
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args)  {
				PacketInfo packetInfo;
				DemographicInfo  demograpgicInfo;
				try {
					packetInfo = (PacketInfo) JsonUtils.jsonFileToJavaObject(PacketInfo.class,"..\\packet-meta-info\\src\\main\\resources\\PacketMetaInfo.json");
					demograpgicInfo = (DemographicInfo) JsonUtils.jsonFileToJavaObject(DemographicInfo.class,"..\\packet-meta-info\\src\\main\\resources\\DemographicInfo.json");
					packetInfoManager.savePacketData(packetInfo);
					packetInfoManager.saveDemographicData(demograpgicInfo);
				} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException e) {
					LOGGER.error("Error while parsing JSON file",e);
				} catch (Exception e) {
					LOGGER.error("Error while Storing packetInfo Data",e);
				}
	}
}