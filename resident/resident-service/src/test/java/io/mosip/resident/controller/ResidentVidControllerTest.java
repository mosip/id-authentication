/*
package io.mosip.resident.controller;

import io.mosip.resident.ResidentTestBootApplication;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.dto.VidRequestDto;
import io.mosip.resident.dto.VidResponseDto;
import io.mosip.resident.service.impl.IdAuthServiceImpl;
import io.mosip.resident.service.impl.ResidentVidServiceImpl;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class ResidentVidControllerTest {

    private static final String JSON_STRING_RESPONSE = "";

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ResidentVidServiceImpl residentVidService;

    @MockBean
    private IdAuthServiceImpl idAuthService;

    @MockBean
    private ResidentServiceRestClient residentServiceRestClient;

    @MockBean
    private TokenGenerator tokenGenerator;

    @Mock
    private Environment env;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any())).thenReturn(JSON_STRING_RESPONSE);
    }


    @Test
    public void searchSuccessTest() throws Exception {
        // TODO
    }


}
*/
