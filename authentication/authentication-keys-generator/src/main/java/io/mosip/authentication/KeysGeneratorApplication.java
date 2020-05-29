package io.mosip.authentication;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import io.mosip.authentication.entity.DataEncryptKeystore;
import io.mosip.authentication.entity.DataEncryptKeystoreRepository;
import io.mosip.authentication.entity.KeyAlias;
import io.mosip.authentication.entity.KeyAliasRepository;
import io.mosip.authentication.service.IdAuthSecurityManager;

@SpringBootApplication
@ComponentScan("io.mosip.authentication.*")
public class KeysGeneratorApplication implements CommandLineRunner {

	private static final Logger LOGGER = Logger.getLogger(KeysGeneratorApplication.class.getName());
	
	private static final String PKCS11_KEY_STORE_TYPE = "PKCS11";

	private static final char[] KEY_PROTECT = "1234".toCharArray();
	
	private static final String CREATED_BY = "system";

	private static final String WRAPPING_TRANSFORMATION = "AES/ECB/NoPadding";
	
	@Value("${ida.key.generate.count}")
	private long noOfKeysRequire;	

	@Value("${application.id}")
	private String applicationId;

	@Value("${identity-cache.reference.id}")
	private String cache_referenceId;	
	
	@Autowired
	private Provider provider;
	
	@Autowired
	private DataEncryptKeystoreRepository keysRepo;

	@Autowired
	private KeyAliasRepository keyAliasRepository;
	
	@Autowired
	private IdAuthSecurityManager securityManager;

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(KeysGeneratorApplication.class, args);
		SpringApplication.exit(run);
	}

	@Override
	public void run(String... args) throws Exception {	
		
		LOGGER.info("Keys generation stated......" );
		Key masterKey = getMasterKeyFromHSM(KEY_PROTECT, provider,keysRepo,keyAliasRepository);
		generate10KKeysAndStoreInDB(masterKey, provider,keysRepo);
		LOGGER.info("Keys generated." );
		
		testEncryptionDecryption();
	}

	private void testEncryptionDecryption() throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException,
			KeyStoreException, CertificateException, UnrecoverableEntryException, IOException {
		System.err.println("Encrypting: ");
		String encrypt = new String(securityManager.encryptWithAES("100", "data".getBytes()));
		System.err.println(encrypt);

		System.err.println("Decrypting: ");
		String decrypt = new String(securityManager.decryptWithAES("100", encrypt.getBytes()));
		System.err.println(decrypt);

		System.err.println(decrypt.contentEquals("data"));
	}
	

	
	private Key getMasterKeyFromHSM(char[] storePin, Provider provider,DataEncryptKeystoreRepository keysRepo,KeyAliasRepository keyAliasRepository) throws Exception {
		String alias = UUID.randomUUID().toString();
		String keyAlias = null;
		KeyStore hsmStore = KeyStore.getInstance(PKCS11_KEY_STORE_TYPE, provider);
		hsmStore.load(null, storePin);
		keyAlias = getKeyAlias(keysRepo,keyAliasRepository);
		System.err.println("keyAlias " + keyAlias);
		if(keyAlias == null) {
			System.err.println("keyAlias not found.Key is generating.");
			generateAndStore(alias, hsmStore, storePin,keyAliasRepository);
			keyAlias = alias;
		}
		
		if (hsmStore.isKeyEntry(keyAlias)) {
			LOGGER.info("KeyAlias: " + keyAlias );
			KeyStore.SecretKeyEntry secretEntry = (KeyStore.SecretKeyEntry) hsmStore.getEntry(keyAlias,
					new KeyStore.PasswordProtection(KEY_PROTECT));
			return secretEntry.getSecretKey();
		}
		
		LOGGER.info("SoftHSM - Key Not found for the alias in HSM.");
		throw new IllegalStateException("Key Not found for the alias in HSM");
	}

	private Key generateAndStore(String keyAlias, KeyStore keyStore, char[] storePin,KeyAliasRepository keyAliasRepository) throws Exception {
		SecureRandom rand = new SecureRandom();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256, rand);
		SecretKey sKey = keyGenerator.generateKey();
		System.err.println("Generated SecretKey: " + sKey);
		KeyStore.SecretKeyEntry keyEntry = new KeyStore.SecretKeyEntry(sKey);
		keyStore.setEntry(keyAlias, keyEntry, new KeyStore.PasswordProtection(KEY_PROTECT));
		storeKeyInAlias(applicationId, LocalDateTime.now(), cache_referenceId, keyAlias, LocalDateTime.now().plusDays(365),keyAliasRepository);
		return sKey;
	}
	
	private void storeKeyInAlias(String applicationId, LocalDateTime timeStamp, String referenceId, String alias,
			LocalDateTime expiryDateTime,KeyAliasRepository keyAliasRepository) {
		LOGGER.info("Storing key in KeyAlias");
		KeyAlias keyAlias = new KeyAlias();
		keyAlias.setAlias(alias);
		keyAlias.setApplicationId(applicationId);
		keyAlias.setReferenceId(referenceId);
		keyAlias.setKeyGenerationTime(timeStamp);
		keyAlias.setKeyExpiryTime(expiryDateTime);
		keyAlias.setCreatedBy(CREATED_BY);
		keyAlias.setCreatedtimes(LocalDateTime.now());
		keyAliasRepository.save(keyAlias);
	}
	
	private String getKeyAlias(DataEncryptKeystoreRepository keysRepo,KeyAliasRepository keyAliasRepository) {
		List<KeyAlias> keyAliases = keyAliasRepository.findByApplicationIdAndReferenceId(applicationId, cache_referenceId)
				.stream().sorted((alias1, alias2) -> {
					return alias1.getKeyGenerationTime().compareTo(alias2.getKeyGenerationTime());
				}).collect(Collectors.toList());
		List<KeyAlias> currentKeyAliases = keyAliases.stream().filter((keyAlias) -> {
			return isValidTimestamp(LocalDateTime.now(), keyAlias);
		}).collect(Collectors.toList());

		if (!currentKeyAliases.isEmpty() && currentKeyAliases.size() == 1) {
			LOGGER.warning("CurrentKeyAlias size is one. Will decrypt symmetric key for this alias");
			return currentKeyAliases.get(0).getAlias();
		}

		return null;
	}

	private boolean isValidTimestamp(LocalDateTime timeStamp, KeyAlias keyAlias) {
		return timeStamp.isEqual(keyAlias.getKeyGenerationTime()) || timeStamp.isEqual(keyAlias.getKeyExpiryTime())
				|| timeStamp.isAfter(keyAlias.getKeyGenerationTime())
						&& timeStamp.isBefore(keyAlias.getKeyExpiryTime());
	}
	
	private void generate10KKeysAndStoreInDB(Key masterKey, Provider provider,DataEncryptKeystoreRepository keysRepo) throws Exception {
		SecureRandom rand = new SecureRandom();
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		Cipher cipher = Cipher.getInstance(WRAPPING_TRANSFORMATION, provider);
		
		int noOfActiveKeys = (int)keysRepo.findAll().stream().filter(k->k.getKeyStatus().equals("active")).count();			
		int noOfKeysToGenerate = 0;
		if((noOfKeysRequire-noOfActiveKeys) > 0) {
			noOfKeysToGenerate = (int) (noOfKeysRequire-noOfActiveKeys);
		}
		
		System.out.println("NoOfKeysToGenerate:" + noOfKeysToGenerate);
		
		for (int i = 0; i < noOfKeysToGenerate; i++) {
			keyGenerator.init(256, rand);
			SecretKey sKey = keyGenerator.generateKey();
			cipher.init(Cipher.ENCRYPT_MODE, masterKey);
			byte[] wrappedKey = cipher.doFinal(sKey.getEncoded());
			String encodedKey = Base64.getEncoder().encodeToString(wrappedKey);
			insertKeyIntoTable(i, encodedKey, "active",keysRepo);
			System.out.println("Insert secrets in DB: " + i);
		}
	}

	private void insertKeyIntoTable(int id, String secretData, String status,DataEncryptKeystoreRepository keysRepo) throws Exception {
		DataEncryptKeystore data = new DataEncryptKeystore();
		int maxid = (int)keysRepo.findMaxId();
		data.setId(maxid + 1);
		data.setKey(secretData);
		data.setKeyStatus(status);
		data.setCrBy(CREATED_BY);
		data.setCrDtimes(LocalDateTime.now());
		keysRepo.save(data);
	}

}
