package io.mosip.registration.processor.packet.storage.config;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.AuthUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

@Configuration
@PropertySource("classpath:bootstrap.properties")
@Import({ HibernateDaoConfig.class })
@EnableJpaRepositories(basePackages = "io.mosip.registration.processor", repositoryBaseClass = HibernateRepositoryImpl.class)
public class PacketStorageBeanConfig {
	
	@Bean
	public PacketInfoManager<Identity, ApplicantInfoDto> getPacketInfoManager() {
		return new PacketInfoManagerImpl();
	}
	
	@Bean
	public PacketInfoDao getPacketInfoDao() {
		return new PacketInfoDao();
	}
	
	@Bean
	public Utilities getUtilities() {
		return new Utilities();
	}
	
	@Bean
	public ABISHandlerUtil getABISHandlerUtil() {
		return new ABISHandlerUtil();
	}
	
	@Bean
	public AuthUtil getAuthUtil() {
		return new AuthUtil();
	}
	
	@Bean
	public KeyGenerator getKeyGenerator() {
		return new KeyGenerator();
	}

	@Bean
	@Primary
	public Encryptor<PrivateKey, PublicKey, SecretKey> getEncryptor() {
		return new EncryptorImpl();
	}

}
