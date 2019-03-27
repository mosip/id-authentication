package io.mosip.registration.processor.reprocessor.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class ReprocessorStage extends MosipVerticleManager {

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Autowired
	Environment environment;

	public void deployVerticle() {
		MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl);
		deployScheduler(mosipEventBus.getEventbus());
	}

	private void deployScheduler(Vertx vertx) {
		vertx.deployVerticle("ceylon:herd.schedule.chime/0.2.0", res -> {
			if (res.succeeded()) {
				System.out.println("+++++++++++Scheduler deployed successfully++++++++++++");
				cronScheduling(vertx);
			} else {
				System.out.println("Failed");
			}
		});
	}

	private void cronScheduling(Vertx vertx) {

		EventBus eventBus = vertx.eventBus();
		// listen the timer events
		eventBus.consumer(("scheduler:stage_timer"), message -> {
			System.out.println(((JsonObject) message.body()).encodePrettily());
			process(new MessageDTO());
		});

		// description of timers
		JsonObject timer = (new JsonObject()).put("type", environment.getProperty("type"))
				.put("seconds", environment.getProperty("seconds")).put("minutes", environment.getProperty("minutes"))
				.put("hours", environment.getProperty("hours"))
				.put("days of month", environment.getProperty("days_of_month"))
				.put("months", environment.getProperty("months"))
				.put("days of week", environment.getProperty("days_of_week"));

		// create scheduler
		eventBus.send("chime", (new JsonObject()).put("operation", "create").put("name", "scheduler:stage_timer")
				.put("description", timer), ar -> {
					if (ar.succeeded()) {
						System.out.println("Scheduling started: " + ar.result().body());
					} else {
						System.out.println("Scheduling failed: " + ar.cause());
						vertx.close();
					}
				});

	}

	@Override
	public MessageDTO process(MessageDTO object) {
		System.out.println("++++++++++++++++Process triggered++++++++++++++++++++");
		return null;
	}
}
