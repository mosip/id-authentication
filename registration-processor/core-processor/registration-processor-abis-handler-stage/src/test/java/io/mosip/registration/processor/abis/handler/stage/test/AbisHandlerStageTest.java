/*package io.mosip.registration.processor.abis.handler.stage.test;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class AbisHandlerStageTest {

    @InjectMocks
    private AbisHandlerStage abisHandlerStage;

    @Mock
    private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

    @Mock
    private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

    @Mock
    private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

    @Mock
    private InternalRegistrationStatusDto registrationStatusDto;

    List<AbisApplicationDto> abisApplicationDtos = new ArrayList<>();
    List<RegBioRefDto> bioRefDtos = new ArrayList<>();
    List<RegDemoDedupeListDto> regDemoDedupeListDtoList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(abisHandlerStage, "maxResults", 30);
        ReflectionTestUtils.setField(abisHandlerStage, "targetFPIR", 30);

        AbisApplicationDto dto = new AbisApplicationDto();
        abisApplicationDtos.add(dto);

        Field auditLog = AuditLogRequestBuilder.class.getDeclaredField("registrationProcessorRestService");
        auditLog.setAccessible(true);
        @SuppressWarnings("unchecked")
        RegistrationProcessorRestClientService<Object> mockObj = Mockito
                .mock(RegistrationProcessorRestClientService.class);
        auditLog.set(auditLogRequestBuilder, mockObj);
        AuditResponseDto auditResponseDto = new AuditResponseDto();
        ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
        responseWrapper.setResponse(auditResponseDto);
        Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
                "test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
                EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

    }

    @Test
    public void testAbisHandlerTOMiddlewareSuccess(){
        Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
        Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("DEMOGRAPHIC_VERIFICATION");
        Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId()).thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
        Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.FALSE);
        Mockito.when(packetInfoManager.getAllAbisDetails()).thenReturn(abisApplicationDtos);

        Mockito.when(packetInfoManager.getBioRefIdByRegId(any())).thenReturn(bioRefDtos);

        Mockito.doNothing().when(packetInfoManager).saveBioRef(any());
        Mockito.doNothing().when(packetInfoManager).saveAbisRequest(any());

        RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
        regDemoDedupeListDto.setMatchedRegId("10003100030001520190422074511");
        regDemoDedupeListDtoList.add(regDemoDedupeListDto);
        Mockito.when(packetInfoManager.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListDtoList);

        MessageDTO dto = new MessageDTO();
        dto.setRid("10003100030001520190422074511");
        abisHandlerStage.process(dto);
    }


}*/
