package io.mosip.kernel.masterdata.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Sagar Mahapatra
 * @author Urvil Joshi
 *
 */
@Configuration
public class LocalDateLocalTimeConfig {
	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Bean
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		objectMapper.registerModule(javaTimeModule);
		return objectMapper;
	}

	public class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

		@Override
		public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			return LocalTime.parse(p.getValueAsString(), TIME_FORMAT);
		}

	}

	public class LocalTimeSerializer extends JsonSerializer<LocalTime> {
		@Override
		public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.format(TIME_FORMAT));
		}
	}

	public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

		@Override
		public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			return LocalDate.parse(p.getValueAsString(), DATE_FORMAT);
		}

	}

	public class LocalDateSerializer extends JsonSerializer<LocalDate> {
		@Override
		public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.format(DATE_FORMAT));
		}
	}

}
