package io.mosip.registration.processor.transaction.api.transaction.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.WebUtils;

import io.mosip.registration.processor.core.token.validation.TokenValidator;
import io.mosip.registration.processor.core.token.validation.exception.AccessDeniedException;
import io.mosip.registration.processor.core.token.validation.exception.InvalidTokenException;
import io.mosip.registration.processor.core.util.DigitalSignatureUtility;
import io.mosip.registration.processor.status.dto.RegistrationTransactionDto;
import io.mosip.registration.processor.status.dto.TransactionDto;
import io.mosip.registration.processor.status.exception.RegStatusAppException;
import io.mosip.registration.processor.status.exception.RegTransactionAppException;
import io.mosip.registration.processor.status.exception.TransactionTableNotAccessibleException;
import io.mosip.registration.processor.status.exception.TransactionsUnavailableException;
import io.mosip.registration.processor.status.service.TransactionService;
import io.mosip.registration.processor.transaction.api.controller.RegistrationTransactionController;
import io.mosip.registration.processor.transaction.api.transaction.api.config.RegistrationTransactionBeanConfigTest;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = RegistrationTransactionBeanConfigTest.class)
@TestPropertySource(locations = "classpath:application.properties")
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
public class RegistrationTransactionControllerTest {
	@InjectMocks
	RegistrationTransactionController registrationTransactionController;
	
	@MockBean
	TransactionService<TransactionDto> transactionService;
	
	@Mock
	private Environment env;
	
	@MockBean(name = "tokenValidator")
	private TokenValidator tokenValidator;
	
	@Value("${registration.processor.signature.isEnabled}")
	private Boolean isEnabled;
	
	@MockBean
	private DigitalSignatureUtility digitalSignatureUtility;
	
	@Autowired
	private MockMvc mockMvc;
	
	
	
	@Before
	public void setUp() {
		Mockito.doNothing().when(tokenValidator).validate(ArgumentMatchers.any(), ArgumentMatchers.any());
		Mockito.doReturn("").when(digitalSignatureUtility).getDigitalSignature(ArgumentMatchers.any());
	}
	
	@Test
	public void testSyncController() throws Exception {
		List<RegistrationTransactionDto> dtoList=new ArrayList<>();
		dtoList.add(new RegistrationTransactionDto("id", "registrationId", "transactionTypeCode", "parentTransactionId", "statusCode", "statusComment", null));
		Mockito.when(transactionService.getTransactionByRegId(ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(dtoList);
		this.mockMvc.perform(get("/search/eng/27847657360002520190320095010").accept(MediaType.APPLICATION_JSON_VALUE).cookie(new Cookie("Authorization", "Anything"))).andExpect(status().isOk());
	}
	
	@Test
	public void testInvalidTokenException1() throws Exception {

		Mockito.doThrow(new InvalidTokenException()).when(tokenValidator)
				.validate(ArgumentMatchers.any(), ArgumentMatchers.any());
		this.mockMvc.perform(get("/search/eng/27847657360002520190320095010").accept(MediaType.APPLICATION_JSON_VALUE).cookie(new Cookie("Authorization", null)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testInvalidTokenException() throws Exception {

		Mockito.doThrow(new InvalidTokenException()).when(tokenValidator)
				.validate(ArgumentMatchers.any(), ArgumentMatchers.any());
		this.mockMvc.perform(get("/search/eng/27847657360002520190320095010").accept(MediaType.APPLICATION_JSON_VALUE).cookie(new Cookie("Authorization", "Anything")))
				.andExpect(status().isOk());
	}
	
	
	@Test
	public void testTransactionsUnavailableException() throws Exception {

		Mockito.doThrow(new TransactionsUnavailableException("","")).when(transactionService)
				.getTransactionByRegId(ArgumentMatchers.any(), ArgumentMatchers.any());
		this.mockMvc.perform(get("/search/eng/27847657360002520190320095010").accept(MediaType.APPLICATION_JSON_VALUE).cookie(new Cookie("Authorization", "Anything")))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testRegTransactionAppException() throws Exception {

		Mockito.doThrow(new RegTransactionAppException("","")).when(transactionService)
				.getTransactionByRegId(ArgumentMatchers.any(), ArgumentMatchers.any());
		this.mockMvc.perform(get("/search/eng/27847657360002520190320095010").accept(MediaType.APPLICATION_JSON_VALUE).cookie(new Cookie("Authorization", "Anything")))
				.andExpect(status().isOk());
	}
}
