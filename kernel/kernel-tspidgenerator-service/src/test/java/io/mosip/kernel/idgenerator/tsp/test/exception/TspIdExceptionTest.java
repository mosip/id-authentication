package io.mosip.kernel.idgenerator.tsp.test.exception;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.idgenerator.tsp.repository.TspRepository;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class TspIdExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	TspRepository tspRepository;

	@Test
	public void TspIdServiceFetchExceptionTest() throws Exception {

		when(tspRepository.findMaxTspId())
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(get("/v1.0/tsp").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void TspIdServiceInsertExceptionTest() throws Exception {
		when(tspRepository.save(Mockito.any()))
				.thenThrow(new DataAccessLayerException("", "cannot execute statement", null));
		mockMvc.perform(get("/v1.0/tsp").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}
}
