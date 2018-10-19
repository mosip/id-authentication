package io.mosip.registration.processor.packet.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.registration.processor.core.abstractverticle.exception.UnsupportedEncodingException;
import io.mosip.registration.processor.core.packet.dto.DemographicInfo;
import io.mosip.registration.processor.core.packet.dto.MetaData;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.packet.storage",
		"io.mosip.registration.processor.packet.manager", 
		"io.mosip.registration.processor.core",
		"io.mosip.kernel.auditmanager"})

@PropertySource({"classpath:packet-meta-application.properties"})
public class PacketInfoManagerApplication implements CommandLineRunner  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PacketInfoManagerApplication.class);
	@Autowired
	private PacketInfoManager<PacketInfo, DemographicInfo,MetaData> packetInfoManager;
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args)  {
				PacketInfo packetInfo;
				DemographicInfo  demograpgicInfo;
				MetaData metaData;
				JsonUtil jsonUtil;
		try {
			File packetInfofile = new File("..\\packet-meta-info\\src\\main\\resources\\PacketMetaInfo.json");
			InputStream packetMetaInfoStream = new FileInputStream(packetInfofile);
			
			File demographicfile = new File("..\\packet-meta-info\\src\\main\\resources\\DemographicInfo.json");
			InputStream demographicInfoStream = new FileInputStream(demographicfile);
			
			File metaDatafile = new File("..\\packet-meta-info\\src\\main\\resources\\MetaData.json");
			InputStream metaDataInfoStream = new FileInputStream(metaDatafile);
			
			jsonUtil = new JsonUtil();
			
			packetInfo = (PacketInfo) jsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,PacketInfo.class);
			demograpgicInfo = (DemographicInfo)jsonUtil.inputStreamtoJavaObject(demographicInfoStream,DemographicInfo.class);
			metaData = (MetaData)jsonUtil.inputStreamtoJavaObject(metaDataInfoStream,MetaData.class);
			
			// packetInfo = (PacketInfo) JsonUtils.jsonFileToJavaObject(PacketInfo.class, "..\\packet-meta-info\\src\\main\\resources\\PacketMetaInfo.json");
			// demograpgicInfo = (DemographicInfo)JsonUtils.jsonFileToJavaObject(DemographicInfo.class, "..\\packet-meta-info\\src\\main\\resources\\DemographicInfo.json");
			 
			packetInfoManager.savePacketData(packetInfo);
			packetInfoManager.saveDemographicData(demograpgicInfo,metaData);
		} catch (UnsupportedEncodingException e) {
					LOGGER.error("Error while parsing JSON file",e);
				} catch (Exception e) {
					LOGGER.error("Error while Storing packetInfo Data",e);
				}
	}
}