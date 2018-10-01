package org.mosip.registration.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AESEncryptionTest.class,
	AESKeyManagerTest.class,
	AESSeedGeneratorTest.class,
	PacketZipCreatorAPITest.class,
	PacketLocalStorageTest.class,
	RSAEncryptionTest.class,
	RSAEncryptionManagerTest.class,
	RSAKeyGenerationTest.class,
	PacketHandlerAPITest.class})
public class PacketCreationTestSuite {
	
}
