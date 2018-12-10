package io.mosip.kernel.idgenerator.tsp.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.idgenerator.spi.TspIdGenerator;
import io.mosip.kernel.idgenerator.tsp.dto.TspResponseDTO;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class TspIdControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private TspIdGenerator<TspResponseDTO> tspIdGeneratorService;
	
	@Test
	public void generateIdTest() throws Exception {
		TspResponseDTO dto=new TspResponseDTO();
		dto.setTspId(1000);
		when(tspIdGeneratorService.generateId()).thenReturn(dto);
		mockMvc.perform(get("/v1.0/tsp").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$.tspId", is(1000)));
	}
	
}
