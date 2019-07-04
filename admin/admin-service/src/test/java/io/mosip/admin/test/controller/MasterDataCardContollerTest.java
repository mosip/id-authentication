package io.mosip.admin.test.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.admin.TestBootApplication;

@SpringBootTest(classes=TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class MasterDataCardContollerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testMasterdataCardSuccess() throws Exception {
		mockMvc.perform(get("/mastercards/{langCode}","eng")).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("zonal-admin")
	public void testMasterdataCardNoDataFound() throws Exception {
		mockMvc.perform(get("/mastercards/{langCode}","aaa")).andExpect(status().isOk());
	}
}
