package io.mosip.registration.processor.core.kernel.beans;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.fsadapter.hdfs.impl.HDFSAdapterImpl;
import io.mosip.kernel.fsadapter.hdfs.util.ConnectionUtils;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.responsesignature.impl.SignatureUtilImpl;

@Configuration
public class KernelConfig {

	@Bean
	@Primary
	public RidValidator<String> getRidValidator() {
		return new RidValidatorImpl();
	}

	@Bean
	@Primary
	public FileSystemAdapter getFileSystemAdapter() {
		return new HDFSAdapterImpl(this.getConnectionUtil());
	}

	@Bean
	@Primary
	public ConnectionUtils getConnectionUtil() {
		return new ConnectionUtils();
	}

	@Bean
	@Primary
	public CbeffUtil getCbeffUtil() {
		return new CbeffImpl();
	}
	
	@Bean
	@Primary
	public SignatureUtil getSignatureUtil() {
		return new SignatureUtilImpl();
	}
	
	@Bean
	@Primary
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	@Primary
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}
	
	@Bean
	@Primary
	public Decryptor<PrivateKey, PublicKey, SecretKey> getDecryptor() {
		return new DecryptorImpl();
	}
	
	@Bean
	@Primary
	public Encryptor<PrivateKey, PublicKey, SecretKey> getEncryptor() {
		return new EncryptorImpl();
	}
	
	@Bean
	@Primary
	public KeyGenerator getKeyGenerator() {
		return new KeyGenerator();
	}


}
