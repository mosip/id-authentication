package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;

import org.apache.commons.codec.digest.DigestUtils;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.kernel.core.util.HMACUtils;

@RestController
public class DigitalSign {

	ObjectMapper mapper = new ObjectMapper();

	@PostMapping(path = "/sign")
	public String sign(@RequestBody String data) throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException, UnrecoverableEntryException, JoseException {
		FileInputStream is = new FileInputStream("lib/Keystore/opkeystore.jks");
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		JsonWebSignature jws = new JsonWebSignature();
		keystore.load(is, "Cpassword".toCharArray());
		Enumeration<?> e = keystore.aliases();
		for (; e.hasMoreElements();) {
			String alias = (String) e.nextElement();
			KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
					new KeyStore.PasswordProtection("Cpassword".toCharArray()));
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
			byte[] digest = HMACUtils.generateHash(data.getBytes());
			String hmac = HMACUtils.digestAsPlainText(digest);
			System.out.println("hmac digital : " +DigestUtils.sha256Hex(data));
			//jws.setPayload(hmac);
			jws.setPayload(DigestUtils.sha256Hex(data));
			jws.setKey(entry.getPrivateKey());
			X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
			byte[] rawCrtText = cert.getEncoded();
			System.out.println(Base64.getEncoder().encodeToString(rawCrtText));
		}

		return jws.getCompactSerialization();
	}
	
	public static void main(String[] args) {
		String req = "{\r\n" + 
				"  \"authType\": {\r\n" + 
				"    \"address\": false,\r\n" + 
				"    \"bio\": true,\r\n" + 
				"    \"otp\": false,\r\n" + 
				"    \"personalIdentity\": false,\r\n" + 
				"    \"pin\": false\r\n" + 
				"  },\r\n" + 
				"  \"bioInfo\": [\r\n" + 
				"    {\r\n" + 
				"      \"bioType\": \"fgrMin\",\r\n" + 
				"      \"deviceInfo\": {\r\n" + 
				"        \"deviceId\": \"123143\",\r\n" + 
				"        \"make\": \"Mantra\",\r\n" + 
				"        \"model\": \"steel\"\r\n" + 
				"      }\r\n" + 
				"    }\r\n" + 
				"  ],\r\n" + 
				"  \"id\": \"mosip.identity.auth\",\r\n" + 
				"  \"idvId\": \"274390482564\",\r\n" + 
				"  \"idvIdType\": \"D\",\r\n" + 
				"  \"tspID\": \"12321\",\r\n" + 
				"  \"key\": {\r\n" + 
				"    \"publicKeyCert\": \"MIIDcTCCAlmgAwIBAgIEL8MbMTANBgkqhkiG9w0BAQsFADBpMQswCQYDVQQGEwJJTjETMBEGA1UECBMKVGFtaWwgTmFkdTEQMA4GA1UEBxMHQ2hlbm5haTENMAsGA1UEChMETWluZDELMAkGA1UECxMCSVQxFzAVBgNVBAMTDnd3dy5nb29nbGUuY29tMB4XDTE4MTAwNDA2MjAxNVoXDTE5MDEwMjA2MjAxNVowaTELMAkGA1UEBhMCSU4xEzARBgNVBAgTClRhbWlsIE5hZHUxEDAOBgNVBAcTB0NoZW5uYWkxDTALBgNVBAoTBE1pbmQxCzAJBgNVBAsTAklUMRcwFQYDVQQDEw53d3cuZ29vZ2xlLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIhJY1ls5Szs/5x4/nbFRiOXy6rF88qdaq3Vi150igNHsVh28gJliSdbtUBIRCl0UySMn6+QtMuKASW1cq9zkaAB9ri3hx4Vpr/bi8T/WJPa48lh2N65kFg5+m4UdOiMcfwc1mMFuIb7EwRmig5Hxw8Q1e7LGU9Jr91UqbD1wNckAp8kisxS1T3hqvUnm9EphFNgEJquEV0wpoicu24rtfa/1/mQz0enWTzJXw3X0rV01CPl5zI09KZYCaOUULbHfP8QSJPaR/hbwAesDJpreoBwPNHNZgfWWjoOI7Jb0YQWGXFTihJZgkRI7uB9oo3UXxSUJsPVjjBDc28veGN/IXsCAwEAAaMhMB8wHQYDVR0OBBYEFLxDcE03e1Uezhj9eNwiFHwedADGMA0GCSqGSIb3DQEBCwUAA4IBAQB+IawNZ5fVcRKP4JuExqV1HoBqLkmSRHLdIIyzAoVF9kJ1SPuCvYGN0g3ezh2OvprlAz+IS0D7eyzxFJ5sRTEbiDY7Dfyr3ltVq51JKcC0RplM8WmcjiNef8+Ou0xp+hGPgKSefjD5g0OFP39ZtidXJfl09fzV+dnakq3oymNnbXxMfsJcdXEGPZB5qVUO4LMIQWg5jN2ryujjLSgmYBEsB1OuKsE+nzRay0TFu+te5al3AIXsEuqoJ8OeiG88FQQ+0hoTlhw+Ee2Ua5eP8u8rrBvIuBOWgvNRmkZvBz8MZIUgPADt+5yRQKgH8UBaNrP27qunOwjUWbHknjNIIn5X\",\r\n" + 
				"    \"sessionKey\": \"6Y215QhrLQNqJ0to4kYdjHikJlshW0sa89yllna+pfOhkDxOC8YI8IJU132rNfrW89soLpncbOnz5kYZTMru5uDfKChN3wx3a9uuwyK+Ht0M2hqC6zqnlm89/T5/hxKQwkwKrdBzJfgU6OnywUC7mnO5s5AA45NjdxBPhkCs+/kPTe/kxuPTMUaHQoluIaesBHY+GJP5MEeHRJL6XSpV1fs8lJocwiwU9O6VCz9s7WyST+j7UvQ58udd2XnOTcNCIu+LbVkDEHE8pll6mGzA2ll/BJY84aGrcFwxhoKIkiXSKl/axKq5uLr2S7ZTBvyEFQ2BEwmbJrgbQ7br7Z47vQ==\"\r\n" + 
				"  },\r\n" + 
				"  \"muaCode\": \"1234567890\",\r\n" + 
				"  \"reqHmac\": \"string\",\r\n" + 
				"  \"reqTime\": \"2018-12-07T08:44:57.086+05:30\",\r\n" + 
				"  \"request\": \"0rkX38QrJQ/FBOe6pdawrhGkC8hQJZcA3n+eOA8ipxcLH046/e+zqBsMnFY6+xL+apbtJxPmfWe/7Ax4vFV/HbE6l5w0QObA4839p9frCl7l5tlwg5JMQb+fUBnw8dqdqS9KgWI9Texcu4iNTW3TGxwDcbMEsBnNr21aB9dFTrwWZiKl96JwDzE+ZE+iBZQTou+pOICdrXQgwPuYAgUyZyUMk5V9gMntY+MTfiabNjcgercO06pIr2MN6up4cg2z5RqMQXHbScvqJ0YQWg2GEWq8CD38atWW6cdYGhpNeRvmNqBU2xUBlD028Re8xrwhp975k5p2r/SHDu7QFlAgC/I7DRvv+QQvkigRWY98v46o7VUz2uDLNZTaeiQyVp5yU3OzKrSGgPPI4d0g6f8koo2SBVMZHhxDWL15jqHE6S+zEwf52xUWbbS856CMpiYWjfDzq6AIfO40EChUb69Esq8yfhH+pSU2JqQpRTRVzrxVmeouZXX4rOHmQ5SzeeRd0dCE//rgi5JS4ZleTxHB1e+Bdgx/LrYIOXX111BViA0T3Akq6hwsI/L4ARk0r5jQucrhiwOYqXlQG0gNAtOSodFZVpVMYz7O6zeA76hwf5RAefQmcrhSQEdD2tmFBB1H92D+gqHV10WTZxwnZjCY/fM/JG41lfJw1LJO4QSGgPfGfC8cbyhBsJnQ4ese+4UDBnN8pegs7+pDjlLtcQIbVl1NltS+r+o3U24+GjbR13N4ICz2pPxvxcV2smOxDqXxLaU4ID0CpYhLraw6pv2PNu3ZLYy0ayCFvpFlRZ4/bun3ec1mIITKHVvfxcgrOVTTtLcRYnYugRZHWRT5XcwHhbUwh/RdUGJgFlKc5C+DuvpbNQRyIqUr/d6MoMTqakS9vrGFTmO5Rcq02m8rWXU+QfEqxEMyuIOLf7V8qPwNPMTLBd671GB3EPepzS4pE5h71WT92CFCpqXNNmAva55L5MxOpu/kLDGpCytbVT2IGxTNCN9THuZVU4kCMLBKSYvUn5aFlMZE9lUwcRkGKNXvyTJdumTH12Zi7kAW7DPxjI7WMaR0K4i5amxIHkYW/tu+nQwXlXyAWMIqDN8ukaF5Asa075Rnuq9mkd8kHbjJjLj3rWY+PtjCYhqbbEItO0VNFnHOrhcpsiCCh/zI4VHChpHYn99pOzoUrpH2PBeZ82k=\",\r\n" + 
				"  \"txnID\": \"1234567890\",\r\n" + 
				"  \"ver\": \"1.0\"\r\n" + 
				"}";
		
		System.out.println(DigestUtils.sha256Hex(req));
	}
}
