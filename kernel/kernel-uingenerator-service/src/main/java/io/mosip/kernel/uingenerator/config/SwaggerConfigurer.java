package io.mosip.kernel.uingenerator.config;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

@Component
public class SwaggerConfigurer {
	
	@Value("${uin.swagger.base-url:http://localhost:8080}")
	private String swaggerBaseUrl;
	
	@Autowired
	Environment environment;


	public  Router configure(Router router) {
		OpenAPI openAPIDoc = OpenApiRoutePublisher.publishOpenApiSpec(router, "spec", "UIN Generation Service", "1.0.0",
				swaggerBaseUrl);
		openAPIDoc
				.addTagsItem(new io.swagger.v3.oas.models.tags.Tag().name("Generate UIN").description("Generate UIN"));
		openAPIDoc.addTagsItem(
				new io.swagger.v3.oas.models.tags.Tag().name("UIN Status Update").description("Update UIN status"));

		ImmutableSet<ClassPath.ClassInfo> modelClasses = getClassesInPackage("io.mosip.kernel.uingenerator.dto");

		Map<String, Object> map = new HashMap<>();

		for (ClassPath.ClassInfo modelClass : modelClasses) {

			Field[] fields = FieldUtils.getFieldsListWithAnnotation(modelClass.load(), Required.class)
					.toArray(new Field[0]);
			List<String> requiredParameters = new ArrayList<>();

			for (Field requiredField : fields) {
				requiredParameters.add(requiredField.getName());
			}

			fields = modelClass.load().getDeclaredFields();

			for (Field field : fields) {
				mapParameters(field, map);
			}

			openAPIDoc.schema(modelClass.getSimpleName(), new Schema().title(modelClass.getSimpleName()).type("object")
					.required(requiredParameters).properties(map));

			map = new HashMap<>();
		}
		router.get("/swagger").handler(res -> res.response().setStatusCode(200).end(Json.pretty(openAPIDoc))

		);
		router.route(environment.getProperty(UinGeneratorConstant.SERVER_SERVLET_PATH) + "/*").handler(
				StaticHandler.create().setCachingEnabled(false).setWebRoot("webroot/node_modules/swagger-ui-dist"));
		return router;
	}

	private  ImmutableSet<ClassPath.ClassInfo> getClassesInPackage(String pckgname) {
		try {
			ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
			return classPath.getTopLevelClasses(pckgname);

		} catch (Exception e) {
			return null;
		}
	}

	private  void mapParameters(Field field, Map<String, Object> map) {
		Class type = field.getType();
		Class componentType = field.getType().getComponentType();

		if (isPrimitiveOrWrapper(type)) {
			Schema primitiveSchema = new Schema();
			primitiveSchema.type(field.getType().getSimpleName());
			map.put(field.getName(), primitiveSchema);
		} else {
			HashMap<String, Object> subMap = new HashMap<>();

			if (isPrimitiveOrWrapper(componentType)) {
				HashMap<String, Object> arrayMap = new HashMap<>();
				arrayMap.put("type", componentType.getSimpleName() + "[]");
				subMap.put("type", arrayMap);
			} else {
				subMap.put("$ref", "#/components/schemas/" + componentType.getSimpleName());
			}

			map.put(field.getName(), subMap);
		}
	}

	private  Boolean isPrimitiveOrWrapper(Type type) {
		return type.equals(Double.class) || type.equals(Float.class) || type.equals(Long.class)
				|| type.equals(Integer.class) || type.equals(Short.class) || type.equals(Character.class)
				|| type.equals(Byte.class) || type.equals(Boolean.class) || type.equals(String.class);
	}

}
