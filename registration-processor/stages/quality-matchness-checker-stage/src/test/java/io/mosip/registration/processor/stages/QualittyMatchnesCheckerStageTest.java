package io.mosip.registration.processor.stages;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.stages.quality.check.assignment.QualityCheckerAssignmentStage;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class QualittyMatchnesCheckerStageTest {
	
	
	private Vertx vertx;
	
	QualityCheckerAssignmentStage stage =new QualityCheckerAssignmentStage();
	
	private MessageDTO dto=new MessageDTO();
	
	@Before
	public void setup(TestContext context) {
		stage.deployVerticle();
		vertx=stage.getEventBus(QualityCheckerAssignmentStage.class).getEventbus();
		
		dto.setRid("1001");
		dto.setRetryCount(null);
		dto.setIsValid(true);
		dto.setInternalError(false);
		dto.setMessageBusAddress( MessageBusAddress.QUALITY_CHECK_BUS);
		
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
		
	}
	@Test
	public void checkProcessRetry(TestContext testContext) {
		
		final Async async = testContext.async();
		JsonObject jsonObject = JsonObject.mapFrom(dto);
		vertx.eventBus().send(MessageBusAddress.QUALITY_CHECK_BUS.getAddress(), jsonObject);
		
				async.complete();
		
		async.awaitSuccess();
	}
	
	

}
