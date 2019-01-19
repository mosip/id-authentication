package io.mosip.kernel.keygenerator.bouncycastle.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
@RunWith(SpringRunner.class)
@SpringBootTest
public class KeyGeneratorTest {

	@Autowired
	KeyGenerator keyGenerator; 
	
	@Test
	public void testGetSymmetricKey() {
		assertThat(keyGenerator.getSymmetricKey(), isA(SecretKey.class));
	}

	@Test
	public void testGetAsymmetricKey() {
		assertThat(keyGenerator.getAsymmetricKey(), isA(KeyPair.class));
		
	}

}
