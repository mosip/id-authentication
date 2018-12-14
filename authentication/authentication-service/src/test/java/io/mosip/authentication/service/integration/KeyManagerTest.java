package io.mosip.authentication.service.integration;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, DecryptorImpl.class})
@WebMvcTest
public class KeyManagerTest {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private DecryptorImpl decryptor;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Test
	public void requestDataTest() throws IdAuthenticationAppException {
//		KeyManager keyManager = new KeyManager() {
//			@Override
//			public byte[] fileReader(String filename, Environment env) throws IOException {
//				return Base64.getDecoder().decode("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDSHQVNjaQU3mFBs+pCTsPcijcnJ1zjXh8plDMuKd/e6tNfDl19vQmI7gKU/Gc96BcLUYd7MQ4sZFDPu0nrAa/NdQM2rQDigHLZnVoDhAiMP27NHNX3UHJcLvA3g3rtfAHAJKHw7Ja0Ur4j7QHlJs38n6CdDg9qqrD5ccKb548ozaDg8AurtEYpuTANZk0SGapFfH93+c4CD54zw487EZA8jOdj3KiihmGNMvGTu563rAcViRrjVHQiHIipEVkIZibohSJBaKcr9rJSOzdDg7bvCyVHikcISA5VkSHwc2jfg+ZsSRnDtnCSSsOGuUWNNULjHLDF4c/AXHbhcmnJbImHAgMBAAECggEAH65xi2vqjEerHtw72+CthsED0kW3VgSYqqJ72cpFmoK0+Wscl2YxcbIAYKpIVPInG1/2j5GU2LENla0LVxdLhK16nRFsBeXRVip0P/3Lc349vPQhTSfA/qZd4Tj+3Z/qUahpuf6qi1PDSNPRX9XrObFwvI26BF7qzXSk9UYZSiZP/o4z6VzBUc4L3QRK0RFo00WOWtzCk94de0jU9M0sZ9AUK5fET2m4vOSBLpmXLxlxXGnn3llfkIvqzFu5DDjFzAPTilGiWUlQPQOSWpvGaTcBaUz77PDN2UvbC3vjamkcIFpXdozq+TeIYrHy4JpU7dm3kzziPb266mDvGspb5QKBgQDuWnLZLqwxqqHqRQSDQ38G8KrZAYe1L2obIjqAcFVROC+4ESoSj9PPLcCOQVPt+KnKJuE0FJuyGQDqPvx5+8qRuRlK/Jz66bcGtPqA/CFdfbc65NcQJS0sjg1LaTakEdKGS1IAgUtM928nZ8NpB7bSCSsec9dHfUA0dEpYIWiCCwKBgQDhq1XdK1G/gE/dTlfEVxSOFExqhJ0FSUvATNtKYHt5uZluE4BRYfKnwWDvF2NdiPlSsVrln0bqbOCNc6G3eG8GE6Eh9xyPd1fq+N/tBKRwkYgBVJeyPJ0Ep7A3CXyI891pH5w82lo1dH66u1RpnioA2Ico5qcWfOPyzi//JjJf9QKBgQCwIuroD5N0CHIqmuIg1koSqNq4Dmdovycb8gllTJ3frTOmRBjhKqQNX/QBq8kH/FAMcPrO78O2sr94Wx9cTXN+iFhmj5K54Og97pOHqcpGOlajEOTUq4RcfoTYi2GzkPAQCa0JboJk2Byt9AH1pZu2TswsbtJRo/9ERAOEaPu/zQKBgG8SxttqU/0+6ZBS3C4eclaQNSCEj0inz+ohqhnMrVm3eYZNgO4NmMLrEov75gOGxLjn5IZ6xAvkdQ4KaQGF/JdwF/JAz8Tph9N2lbjyfQGPD/MfsN6gqOQ+qSQdvjcmWMdCMMNe8eG3qhy80Yp+t8vcx4HhLUKLTCMZS1R5d3f1AoGAZOT0NArWuWlUQ9W08wqiwsjIqWKxO2Dbm8txQDJEWv4NmubWOUUsgyBT6C67QqN/gH4wFXUYNnt29iPTSF/0DDsqAhRPGsCz1qElsc68QVmZ0BO7lMK4rXoz8f3AsG2EdfFNMKbVkxQNKmmwvOKzPCDQFbsXnaRI5i2wChhZEXg=");
//			}
//		};
//		assertNotEquals(keyManager.requestData(createRequest(), env, decryptor, mapper), createResponse());		
	}
	
	@Test
	public void requestInvalidDataTest() {
//		KeyManager keyManager = new KeyManager();
//		try {
//			keyManager.requestData(createRequest(), env, decryptor, mapper);
//		} catch (IdAuthenticationAppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}
	
	@Test
	public void requestInvalidDataTest2() throws IdAuthenticationAppException, IOException {
//		KeyManager keyManager = new KeyManager() {
//			@Override
//			public byte[] fileReader(String filename, Environment env) throws IOException {
//				return Base64.getDecoder().decode("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDSHQVNjaQU3mFBs+pCTsPcijcnJ1zjXh8plDMuKd/e6tNfDl19vQmI7gKU/Gc96BcLUYd7MQ4sZFDPu0nrAa/NdQM2rQDigHLZnVoDhAiMP27NHNX3UHJcLvA3g3rtfAHAJKHw7Ja0Ur4j7QHlJs38n6CdDg9qqrD5ccKb548ozaDg8AurtEYpuTANZk0SGapFfH93+c4CD54zw487EZA8jOdj3KiihmGNMvGTu563rAcViRrjVHQiHIipEVkIZibohSJBaKcr9rJSOzdDg7bvCyVHikcISA5VkSHwc2jfg+ZsSRnDtnCSSsOGuUWNNULjHLDF4c/AXHbhcmnJbImHAgMBAAECggEAH65xi2vqjEerHtw72+CthsED0kW3VgSYqqJ72cpFmoK0+Wscl2YxcbIAYKpIVPInG1/2j5GU2LENla0LVxdLhK16nRFsBeXRVip0P/3Lc349vPQhTSfA/qZd4Tj+3Z/qUahpuf6qi1PDSNPRX9XrObFwvI26BF7qzXSk9UYZSiZP/o4z6VzBUc4L3QRK0RFo00WOWtzCk94de0jU9M0sZ9AUK5fET2m4vOSBLpmXLxlxXGnn3llfkIvqzFu5DDjFzAPTilGiWUlQPQOSWpvGaTcBaUz77PDN2UvbC3vjamkcIFpXdozq+TeIYrHy4JpU7dm3kzziPb266mDvGspb5QKBgQDuWnLZLqwxqqHqRQSDQ38G8KrZAYe1L2obIjqAcFVROC+4ESoSj9PPLcCOQVPt+KnKJuE0FJuyGQDqPvx5+8qRuRlK/Jz66bcGtPqA/CFdfbc65NcQJS0sjg1LaTakEdKGS1IAgUtM928nZ8NpB7bSCSsec9dHfUA0dEpYIWiCCwKBgQDhq1XdK1G/gE/dTlfEVxSOFExqhJ0FSUvATNtKYHt5uZluE4BRYfKnwWDvF2NdiPlSsVrln0bqbOCNc6G3eG8GE6Eh9xyPd1fq+N/tBKRwkYgBVJeyPJ0Ep7A3CXyI891pH5w82lo1dH66u1RpnioA2Ico5qcWfOPyzi//JjJf9QKBgQCwIuroD5N0CHIqmuIg1koSqNq4Dmdovycb8gllTJ3frTOmRBjhKqQNX/QBq8kH/FAMcPrO78O2sr94Wx9cTXN+iFhmj5K54Og97pOHqcpGOlajEOTUq4RcfoTYi2GzkPAQCa0JboJk2Byt9AH1pZu2TswsbtJRo/9ERAOEaPu/zQKBgG8SxttqU/0+6ZBS3C4eclaQNSCEj0inz+ohqhnMrVm3eYZNgO4NmMLrEov75gOGxLjn5IZ6xAvkdQ4KaQGF/JdwF/JAz8Tph9N2lbjyfQGPD/MfsN6gqOQ+qSQdvjcmWMdCMMNe8eG3qhy80Yp+t8vcx4HhLUKLTCMZS1R5d3f1AoGAZOT0NArWuWlUQ9W08wqiwsjIqWKxO2Dbm8txQDJEWv4NmubWOUUsgyBT6C67QqN/gH4wFXUYNnt29iPTSF/0DDsqAhRPGsCz1qElsc68QVmZ0BO7lMK4rXoz8f3AsG2EdfFNMKbVkxQNKmmwvOKzPCDQFbsXnaRI5i2wChhZEXg=");
//			}
//		};
//		Map<String,Object> map = createRequest();
//		map.remove("key");
//		keyManager.requestData(map, env, decryptor, mapper);
	}
	
	@Test
	public void requestInvalidDataTest3() {
//		KeyManager keyManager = new KeyManager() {
//			@Override
//			public byte[] fileReader(String filename, Environment env) throws IOException {
//				return Base64.getDecoder().decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCzrKrV9AtFLTxCY3xnV7CzTPgq5l");
//			}
//		};
//		try {
//			keyManager.requestData(createRequest(), env, decryptor, mapper);
//		} catch (IdAuthenticationAppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
	}
	
	private  Map<String, Object> createRequest(){
		String data ="{\r\n" + 
				"	\"authType\": {\r\n" + 
				"		\"address\": false,\r\n" + 
				"		\"bio\": true,\r\n" + 
				"        \"fullAddress\": false,\r\n" + 
				"		\"otp\": false,\r\n" + 
				"		\"personalIdentity\": false,\r\n" + 
				"		\"pin\": false\r\n" + 
				"	},\r\n" + 
				"	\"bioInfo\": [{\r\n" + 
				"		\"bioType\": \"fgrMin\",\r\n" + 
				"		\"deviceInfo\": {\r\n" + 
				"			\"deviceId\": \"123143\",\r\n" + 
				"			\"make\": \"Mantra\",\r\n" + 
				"			\"model\": \"steel\"\r\n" + 
				"		}\r\n" + 
				"	}],\r\n" + 
				"	\"id\": \"mosip.identity.auth\",\r\n" + 
				"	\"idvId\": \"312480672934\",\r\n" + 
				"	\"idvIdType\": \"D\",\r\n" + 
				"	\"reqTime\": \"2018-12-12T15:44:57.086+05:30\",\r\n" + 
				"	\"muaCode\": \"1234567890\",\r\n" + 
				"	\"tspID\":\"tsp\",\r\n" + 
				"	\"key\": {\r\n" + 
				"    \"sessionKey\": \"c1OYTyGCXn9FVkJ6o/IVGc+JADaXEXvURRljML4JeeQOjF5Pc2UPfGp7J/eSu9r7ZNFspjNp4aRxejYBQZDfOXHZLEz3DckbKOEyycGNvu3tR3s6u4Ev+SoukkqDIya6sabIJiTVaZoLSezlvmuh7JAUNioB0ygTpsDG1iyshAKHbmKfP9Qw2aHJ+vZvvKqePbukEukhGFlRxSrW00bthKDgVsSVVd20Jh+nZP1391cVE7WVeHiYdfb5kU7v7ArKoKt55T0ggCo1a4fPbIwyfoxgdAmX+4JlhlbXx3gA+Ydw45LJmjk+ROt1wLs7x9Zo1H5A/MuXfK1xdEuJ6cx97A==\"\r\n" + 
				"	},\r\n" + 
				"	\"request\": \"LjOdYJ2u728SauWBvruKf56yYF5gvWypVmWHCzRlRn8qf/c+wKzr1eRKBlC2QNhBJqbxOLv1lTXmECP2pWTPy8o8kgsnZ0O9wd8Ew/Y1wWeoIriccS1DKt7+61aGnIA+kNfRHjZdYEu+O2WgeslbhWG+nwO6K/TXGMsGiYIGG3DHPl0dLB2oenmk60yV8JACvBP8AGXblHi8RE3Nf59AT71NUhkvyHupsldP8kSCWhrQ50JuLmJKyuEQAKxsHvobBhrVftmIJdnkrBVqt2EN3kY8XQZyCCeHa9hEboQeG9jl+M/+wnTPmV5Of01tBoqtiuENrfg5G0UauClZ8ipGKs42aRwmVsJKluZSfrjMI7ZcUYwjv9sKWaQCv8/dsqM9w7sErJbC1H3lRCthytKx7WgAqtyXIPQIxItrCXUgtlAlF3+EpKeNOFxJAsNQOvpwK8mmqjuHifjlky9aWLKLO5pMuZwbQwXPkw0XW3sDpqa8wE786a6udh8HsSY2RcUGU98MbnL1Q8PiOKm6wTjsqp7A3oSzjUsMLgHFMpGFJpk=\",\r\n" + 
				"	\"txnID\": \"1234567890\",\r\n" + 
				"	\"reqHmac\": \"string\",\r\n" + 
				"	\"ver\": \"1.0\"\r\n" + 
				"}";
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.getDecoder().decode((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}
	
	private Map<String, Object> createResponse(){
		String data ="{\\r\\n\\tidentity = {\\r\\n\\t\\tleftIndex = [{\\r\\n\\t\\t\\tvalue = Rk1SACAyMAAAAAEIAAABPAFiAMUAxQEAAAAoJ4CEAOs8UICiAQGXUIBzANXIV4CmARiXUEC6AObFZIB3ALUSZEBlATPYZICIAKUCZEBmAJ4YZEAnAOvBZIDOAKTjZEBCAUbQQ0ARANu0ZECRAOC4NYBnAPDUXYCtANzIXUBhAQ7bZIBTAQvQZICtASqWZEDSAPnMZICaAUAVZEDNAS63Q0CEAVZiSUDUAT + oNYBhAVprSUAmAJyvZICiAOeyQ0CLANDSPECgAMzXQ0CKAR8OV0DEAN \\/ QZEBNAMy9ZECaAKfwZEC9ATieUEDaAMfWUEDJAUA2NYB5AVttSUBKAI + oZECLAG0FZAAA\\r\\n\\t\\t}]\\r\\n\\t}\\r\\n}";
		
		Map<String, Object> readValue=null;
		try {
			readValue = mapper.readValue(
					data,
					new TypeReference<Map<String, Object>>() {
					});
			readValue.put("request",Base64.getDecoder().decode((String) readValue.get("request")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return readValue;
	}

}
