package io.mosip.preregistration.core.test.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.core.common.dto.CryptoManagerResponseDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.util.CryptoUtil;

/**
 * CryptoUtil Test
 * 
 * @version 1.0.0
 * @author M1043226
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoUtilTest {

	@Autowired
	CryptoUtil crypto;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

	@Test
	public void encryptSuccessTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		CryptoManagerResponseDTO cryptoRes = new CryptoManagerResponseDTO();
		cryptoRes.setData("fuyftwfd");
		ResponseEntity<CryptoManagerResponseDTO> res = new ResponseEntity<>(cryptoRes, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(CryptoManagerResponseDTO.class))).thenReturn(res);
		assertNotNull(crypto.encrypt("hello".getBytes(), LocalDateTime.now()));

	}

	@Test
	public void decryptSuccessTest() {
		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		Mockito.when(restTemplateBuilder.build()).thenReturn(restTemplate);
		CryptoManagerResponseDTO cryptoRes1 = new CryptoManagerResponseDTO();
		cryptoRes1.setData(
				"fGKe7i36VWALNj889wy1DTU4PII_J3IlQuC-CEtj_HWakHf3NNIHdTK8pP33uhH-lstOZM35dnC9-piqby3eiB9msEMHgItzzK2pKRSTqz-q0e7521hHGV_J9yKQxLSuRunE6rukY4gwkq7l3Q-jLl7cAE29Pz4ReYCLExDDQ7Wcq03xp_cabJ2pYbRsSYvF4bKLkS8BAyDhZo1Nk5sd5012Hv-khB7ePKxkGlDCVhgyS0PCT80f75ADSCsCfSrnKNqjOA_gjWvc_Oips9uddaD0o2NPpHqDYJa6hg3Z4KzgIwZ4dd62VwO8aInITItLwB1wQtGcK9gDaPqZ1s21oCNLRVlfU1BMSVRURVIj3co-62Kc0b3NUwB4n01-3ZxwmLehGpPFOPi1XNRK1Sw");
		ResponseEntity<CryptoManagerResponseDTO> res = new ResponseEntity<>(cryptoRes1, HttpStatus.OK);
		Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(CryptoManagerResponseDTO.class))).thenReturn(res);
		byte[] encodedvalue = crypto.encrypt("hello".getBytes(), LocalDateTime.now());
		assertNotNull(crypto.decrypt(encodedvalue,LocalDateTime.now()));

	}

}
