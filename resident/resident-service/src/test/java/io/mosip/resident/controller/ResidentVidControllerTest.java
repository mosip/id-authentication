package io.mosip.resident.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.ResidentTestBootApplication;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.ResidentVidRequestDto;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.dto.VidRequestDto;
import io.mosip.resident.dto.VidResponseDto;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.VidCreationException;
import io.mosip.resident.service.impl.IdAuthServiceImpl;
import io.mosip.resident.service.impl.ResidentServiceImpl;
import io.mosip.resident.service.impl.ResidentVidServiceImpl;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private ResidentServiceImpl residentService;

    @MockBean
    private ResidentServiceRestClient residentServiceRestClient;

    @MockBean
    private TokenGenerator tokenGenerator;

    @Mock
    private Environment env;

    @Autowired
    private MockMvc mockMvc;

    private static ResidentVidRequestDto getRequest() {
        VidRequestDto vidRequestDto = new VidRequestDto();
        vidRequestDto.setIndividualId("9072037081");
        vidRequestDto.setIndividualIdType(IdType.UIN.name());
        vidRequestDto.setOtp("974436");
        vidRequestDto.setTransactionID("1111122222");
        vidRequestDto.setVidType("Temporary");

        ResidentVidRequestDto request = new ResidentVidRequestDto();
        request.setId("mosip.resident.vid");
        request.setVersion("v1");
        request.setRequesttime(DateUtils.getUTCCurrentDateTimeString());
        request.setRequest(vidRequestDto);
        return request;
    }


    @Test
    public void vidCreationSuccessTest() throws Exception {

        VidResponseDto dto = new VidResponseDto();
        dto.setVid("12345");
        dto.setMessage("Successful");

        ResponseWrapper<VidResponseDto> responseWrapper = new ResponseWrapper<>();
        responseWrapper.setResponse(dto);

        Mockito.when(
                residentVidService.generateVid(any(VidRequestDto.class)))
                .thenReturn(responseWrapper);



        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(getRequest());

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.response.vid", is("12345")));
    }

    @Test
    public void otpValidationFailureTest() throws Exception {

        Mockito.when(
                residentVidService.generateVid(any(VidRequestDto.class)))
                .thenThrow(new OtpValidationFailedException());

        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(getRequest());

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-004")));
    }

    @Test
    public void vidCreationFailureTest() throws Exception {

        Mockito.when(
                residentVidService.generateVid(any(VidRequestDto.class)))
                .thenThrow(new VidCreationException());

        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(getRequest());

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-007")));
    }

    @Test
    public void invalidId() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.setId(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidVersion() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.setVersion(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidRequest() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.setRequest(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidVidType() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.getRequest().setVidType(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidIndividualIdType() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.getRequest().setIndividualIdType(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidIndividualId() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.getRequest().setIndividualId(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidTransactionId() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.getRequest().setTransactionID(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }

    @Test
    public void invalidOtp() throws Exception {

        ResidentVidRequestDto request = getRequest();
        request.getRequest().setOtp(null);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(request);

        this.mockMvc.perform(post("/vid").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", is("RES-SER-009")));
    }


}
