package io.mosip.registration.processor.reprocessor.stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

@RunWith(MockitoJUnitRunner.class)
public class ReprocessorStageTest {

	MessageDTO dto = new MessageDTO();
	@InjectMocks
	private ReprocessorStage reprocessorStage = new ReprocessorStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Before
	public void setup() throws Exception {
		 ReflectionTestUtils.setField(reprocessorStage, "fetchSize", 2);
         ReflectionTestUtils.setField(reprocessorStage, "elapseTime", 21600);
         ReflectionTestUtils.setField(reprocessorStage, "reprocessCount", 3);
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
	public void testProcessValid() {

		List<InternalRegistrationStatusDto> dtolist = new ArrayList<>();
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

		registrationStatusDto.setRegistrationId("2018701130000410092018110735");
		registrationStatusDto.setRegistrationStageName("PacketValidatorStage");
	
		registrationStatusDto.setRegistrationType("NEW");
		registrationStatusDto.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.REPROCESS.toString());
		dtolist.add(registrationStatusDto);
		InternalRegistrationStatusDto registrationStatusDto1 = new InternalRegistrationStatusDto();

		registrationStatusDto1.setRegistrationId("2018701130000410092018110734");
		registrationStatusDto1.setRegistrationStageName("PacketValidatorStage");
		registrationStatusDto1.setReProcessRetryCount(1);
		registrationStatusDto1.setRegistrationType("NEW");
		registrationStatusDto1.setLatestTransactionStatusCode(RegistrationTransactionStatusCode.SUCCESS.toString());
		dtolist.add(registrationStatusDto1);
		Mockito.when(registrationStatusService.getUnProcessedPacketsCount(anyLong(), anyInt(), anyList()))
				.thenReturn(1);
		Mockito.when(registrationStatusService.getUnProcessedPackets(anyInt(), anyLong(), anyInt(), anyList()))
				.thenReturn(dtolist);
		dto = reprocessorStage.process(dto);
		assertTrue(dto.getIsValid());
	}

	/**
	 * Exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void exceptionTest() throws Exception {
		Mockito.when(registrationStatusService.getUnProcessedPacketsCount(anyLong(), anyInt(), anyList()))
				.thenReturn(null);
		dto = reprocessorStage.process(dto);
		assertEquals(true, dto.getInternalError());

	}

	@Test
	public void TablenotAccessibleExceptionTest() throws Exception {
		Mockito.when(registrationStatusService.getUnProcessedPacketsCount(anyLong(), anyInt(), anyList()))
				.thenThrow(new TablenotAccessibleException("") {
				});

		dto = reprocessorStage.process(dto);
		assertEquals(true, dto.getInternalError());

	}

}
