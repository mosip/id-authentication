package io.mosip.kernel.uingenerator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Exposes the OpenAPI spec as a vertx route.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public final class OpenApiRoutePublisher {
	
	private OpenApiRoutePublisher() {}

	private  static final Map<String, OpenAPI> generatedSpecs = new HashMap<>();

	public static synchronized  OpenAPI publishOpenApiSpec(Router router, String path, String title, String version,
			String serverUrl) {
		Optional<OpenAPI> spec = Optional.empty();
		if (generatedSpecs.get(path) == null) {
			OpenAPI openAPI = OpenApiSpecGenerator.generateOpenApiSpecFromRouter(router, title, version, serverUrl);
			generatedSpecs.put(path, openAPI);
			spec = Optional.of(openAPI);
		}
		if (spec.isPresent()) {
			Optional<OpenAPI> finalSpec = spec;
			return finalSpec.get();
		} else {
			return new OpenAPI();
		}
	}
}
