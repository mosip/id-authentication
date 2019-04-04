package io.mosip.registration.processor.reprocessor.stage;

import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.impl.Deployment;

@ExtendWith(MockitoExtension.class)
public class ReprocessingSchedulerTest {

	@Mock
	private Vertx vertx;
	
	ReprocessorStage ReprocessorStage = new ReprocessorStage();

	@Test
	public void testDeploySuccess() {
		
	}
}
