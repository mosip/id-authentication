package io.mosip.admin.iddefinition;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;

import io.mosip.admin.iddefinition.constant.JsonIdentitySchemaErrorConstant;
import io.mosip.admin.iddefinition.exception.JsonSchemaException;

/**
 * Identity Json Schema Validation
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@Component
public class JsonIdentitySchemaValidator {
	private static final String IDENTITY = "identity";
	private static final String PROPERTIES = "properties";
	private static final String ID = "$id";
	private static final String TITLE = "title";
	private static final String SCHEMA = "$schema";

	/**
	 * This method performs the validation of identity json schema
	 * 
	 * @param content
	 *            json schema
	 * @return true if json schema is valid, otherwise false.
	 * @throws IOException
	 */
	public boolean validateIdentitySchema(String content) throws IOException {
		Boolean isValid = false;
		Objects.requireNonNull(content, JsonIdentitySchemaErrorConstant.INVALID_JSON.getErrorMessage());
		JsonNode rootJsonNode = JsonLoader.fromString(content);
		validateSchemaAttributes(rootJsonNode);
		JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.byDefault();
		SyntaxValidator validator = jsonSchemaFactory.getSyntaxValidator();
		ProcessingReport report = validator.validateSchema(rootJsonNode);
		if (report.isSuccess()) {
			JsonNode propertiesNode = rootJsonNode.get(PROPERTIES);
			if (propertiesNode.has(IDENTITY)) {
				JsonNode identityNode = propertiesNode.get(IDENTITY);
				if (identityNode.has(PROPERTIES)) {
					JsonNode identityProperties = identityNode.get(PROPERTIES);
					Iterator<Entry<String, JsonNode>> fields = identityProperties.fields();
					if (fields.hasNext()) {
						isValid = true;
					} else {
						throw new JsonSchemaException(
								JsonIdentitySchemaErrorConstant.IDENTITY_PROPS_ATTR_MISSING.getErrorCode(),
								JsonIdentitySchemaErrorConstant.IDENTITY_PROPS_ATTR_MISSING.getErrorMessage());
					}
				} else {
					throw new JsonSchemaException(
							JsonIdentitySchemaErrorConstant.IDENTITY_PROPS_ATTR_MISSING.getErrorCode(),
							JsonIdentitySchemaErrorConstant.IDENTITY_PROPS_ATTR_MISSING.getErrorMessage());
				}
			} else {
				throw new JsonSchemaException(JsonIdentitySchemaErrorConstant.IDENTITY_ATTR_MISSING.getErrorCode(),
						JsonIdentitySchemaErrorConstant.IDENTITY_ATTR_MISSING.getErrorMessage());
			}
		}
		return isValid;
	}

	private void validateSchemaAttributes(JsonNode node) {
		Objects.requireNonNull(node);
		if (!node.has(TITLE)) {
			throw new JsonSchemaException(JsonIdentitySchemaErrorConstant.TITLE_ATTR_MISSING.getErrorCode(),
					JsonIdentitySchemaErrorConstant.TITLE_ATTR_MISSING.getErrorMessage());
		}
		if (!node.has(SCHEMA)) {
			throw new JsonSchemaException(JsonIdentitySchemaErrorConstant.SCHEMA_ATTR_MISSING.getErrorCode(),
					JsonIdentitySchemaErrorConstant.SCHEMA_ATTR_MISSING.getErrorMessage());
		}
		if (!node.has(ID)) {
			throw new JsonSchemaException(JsonIdentitySchemaErrorConstant.ID_ATTR_MISSING.getErrorCode(),
					JsonIdentitySchemaErrorConstant.ID_ATTR_MISSING.getErrorMessage());
		}
		if (!node.has(PROPERTIES)) {
			throw new JsonSchemaException(JsonIdentitySchemaErrorConstant.PROPERTIES_ATTR_MISSING.getErrorCode(),
					JsonIdentitySchemaErrorConstant.PROPERTIES_ATTR_MISSING.getErrorMessage());
		}
	}

}
