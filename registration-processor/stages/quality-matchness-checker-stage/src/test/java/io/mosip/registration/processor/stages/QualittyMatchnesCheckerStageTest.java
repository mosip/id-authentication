package io.mosip.registration.processor.stages;

import static org.hamcrest.CoreMatchers.anything;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;
import io.mosip.registration.processor.stages.quality.check.assignment.QualityCheckerAssignmentStage;


@RunWith(MockitoJUnitRunner.class)
public class QualittyMatchnesCheckerStageTest {

	@Mock
	QualityMatchnessCheckerStageApplication app;
	
	@InjectMocks
	QualityCheckerAssignmentStage stage = new QualityCheckerAssignmentStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String clusterAddress, String localhost) {
			return new MosipEventBus(vertx);
		}
		
		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {}
	};

	@Mock
	QualityCheckManager<String, QCUserDto> qualityCheckManager;
	QCUserDto qcUserDto=new QCUserDto();
	private MessageDTO dto = new MessageDTO();
	private Logger fooLogger;
	private ListAppender<ILoggingEvent> listAppender;
	@Before
	public void setup() {
		fooLogger = (Logger) LoggerFactory.getLogger(QualityCheckerAssignmentStage.class);
        listAppender = new ListAppender<>();

		dto.setRid("1001");
		dto.setRetryCount(null);
		dto.setIsValid(true);
		dto.setInternalError(false);
		dto.setMessageBusAddress(MessageBusAddress.QUALITY_CHECK_BUS);

		qcUserDto.setDecisionStatus(DecisionStatus.PENDING);
		qcUserDto.setQcUserId("qc001");
		qcUserDto.setRegId(dto.getRid());

	}


	@Test
	public void checkProcessRetry() {
		 listAppender.start();
	        fooLogger.addAppender(listAppender);

			Mockito.when(qualityCheckManager.assignQCUser(anyString())).thenReturn(qcUserDto);


			stage.process(dto);

			Assertions.assertThat(listAppender.list)
	        .extracting( ILoggingEvent::getLevel, ILoggingEvent::getFormattedMessage)
			.containsExactly(Tuple.tuple( Level.INFO, "qc001 - 1001  packet assigned to qcuser successfully"));
	}
	
	@Test
	public void deployVerticalTest() {
		stage.deployVerticle();
	}


}
