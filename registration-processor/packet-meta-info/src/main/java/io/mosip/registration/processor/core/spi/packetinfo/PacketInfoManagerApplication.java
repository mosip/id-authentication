package io.mosip.registration.processor.core.spi.packetinfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.processor.core.packet.dto.BiometericData;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.PacketInfo;
import io.mosip.registration.processor.core.spi.packetinfo.service.PacketInfoManager;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.core.spi.packetinfo"})
@PropertySource({"classpath:packet-meta-application.properties"})
public class PacketInfoManagerApplication implements CommandLineRunner  {
	
	@Autowired
	private PacketInfoManager packetInfoManager;
	public static void main(String[] args) {
		SpringApplication.run(PacketInfoManagerApplication.class, args);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(String... args)  {
				PacketInfo packetInfo;
				try {
					packetInfo = (PacketInfo) JsonUtils.jsonFileToJavaObject(PacketInfo.class,"..\\packet-meta-info\\src\\main\\resources\\PacketMetaInfo.json");
					packetInfoManager.savePacketData(packetInfo);
				} catch (MosipJsonParseException | MosipJsonMappingException | MosipIOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
	}
}