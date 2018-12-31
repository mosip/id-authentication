package io.mosip.kernel.masterdata.config;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * Configuration class for LocalDate, LocalTime LocalDateTime.
 * 
 * @author Sagar Mahapatra
 * @author Urvil Joshi
 *
 */
@Configuration
public class LocalDateLocalTimeConfig {
	public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter UTC_DATE_TIME_FORMAT = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	@Bean
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
		objectMapper.registerModule(javaTimeModule);
		return objectMapper;
	}

	public static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {
		@Override
		public LocalTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
			return LocalTime.parse(jsonParser.getValueAsString(), TIME_FORMAT);
		}

	}

	public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {
		@Override
		public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
				throws IOException {
			jsonGenerator.writeString(localTime.format(TIME_FORMAT));
		}
	}

	public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
		@Override
		public LocalDate deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
			return LocalDate.parse(jsonParser.getValueAsString(), DATE_FORMAT);
		}

	}

	public static class LocalDateSerializer extends JsonSerializer<LocalDate> {
		@Override
		public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
				throws IOException {
			jsonGenerator.writeString(localDate.format(DATE_FORMAT));
		}
	}

	public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
		@Override
		public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
				SerializerProvider serializerProvider) throws IOException {
			jsonGenerator.writeString(localDateTime.format(UTC_DATE_TIME_FORMAT));
		}
	}

	public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
		@Override
		public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
			return LocalDateTime.parse(jsonParser.getValueAsString(), UTC_DATE_TIME_FORMAT);
		}
	}
}
