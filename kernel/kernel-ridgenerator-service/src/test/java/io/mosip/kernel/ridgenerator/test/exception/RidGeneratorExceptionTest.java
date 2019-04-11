package io.mosip.kernel.ridgenerator.test.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.ridgenerator.dto.RidGeneratorResponseDto;
import io.mosip.kernel.ridgenerator.exception.EmptyInputException;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.service.RidGeneratorService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RidGeneratorExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RidGeneratorService<RidGeneratorResponseDto> ridGeneratorService;

	@Test
	@WithUserDetails("reg-processor")
	public void invalidLengthCenterIdExceptionTest() throws Exception {
		when(ridGeneratorService.generateRid(Mockito.any(), Mockito.any()))
				.thenThrow(new InputLengthException("errorCode", "errorMessage"));
		mockMvc.perform(get("/generate/rid/2/8").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("reg-processor")
	public void emptyLengthCenterIdAndMachineIdExceptionTest() throws Exception {
		when(ridGeneratorService.generateRid(Mockito.any(), Mockito.any()))
				.thenThrow(new EmptyInputException("errorCode", "errorMessage"));
		mockMvc.perform(get("/generate/rid/     /     ").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
