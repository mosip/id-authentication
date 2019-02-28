package io.mosip.kernel.applicanttype.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.applicanttype.exception.DataNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ApplicantTypeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
	}

	@Test
	public void getApplicantType() throws Exception {
		mockMvc.perform(get(
				"/v1.0/applicanttype/getApplicantType?biometricAvailable=false&dateofbirth=1933-01-27T05:00:00.000Z&genderCode=MLE&individualTypeCode=FR&languagecode=eng"))
				.andExpect(status().isOk()).andReturn();

	}

	@Test
	public void getApplicantTypeDataNotFoundException() throws Exception {
		mockMvc.perform(get(
				"/v1.0/applicanttype/getApplicantType?biometricAvailable=false&dateofbirth=1933-01-27T05:00:00.000Z&genderCode=MLERTE&individualTypeCode=FR&languagecode=eng"))
				.andExpect(status().isOk());

	}
	
	@Test
	public void getApplicantTypeInvalidApplicantArgumentException() throws Exception {
		mockMvc.perform(get(
				"/v1.0/applicanttype/getApplicantType?biometricAvailable=false&dateofbirth=1933-01-27T05%23400:00.000Z&genderCode=MLERTE&individualTypeCode=FR&languagecode=eng"))
		.andExpect(status().isOk());
		
	}

}
