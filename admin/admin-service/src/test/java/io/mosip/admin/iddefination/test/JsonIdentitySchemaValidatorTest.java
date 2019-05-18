package io.mosip.admin.iddefination.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.iddefinition.JsonIdentitySchemaValidator;
import io.mosip.admin.iddefinition.exception.JsonSchemaException;

@SpringBootTest(classes=TestBootApplication.class)
@RunWith(SpringRunner.class)
public class JsonIdentitySchemaValidatorTest {

	@Autowired
	private JsonIdentitySchemaValidator validator;

	private String validJsonSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{\"title\":\"identity\",\"description\":\"ThisholdsalltheattributesofanIdentity\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"firstName\":{\"$ref\":\"#/definitions/values\"},\"middleName\":{\"$ref\":\"#/definitions/values\"},\"lastName\":{\"$ref\":\"#/definitions/values\"},\"dateOfBirth\":{\"$ref\":\"#/definitions/values\"},\"gender\":{\"$ref\":\"#/definitions/values\"},\"addressLine1\":{\"$ref\":\"#/definitions/values\"},\"addressLine2\":{\"$ref\":\"#/definitions/values\"},\"addressLine3\":{\"$ref\":\"#/definitions/values\"},\"region\":{\"$ref\":\"#/definitions/values\"},\"province\":{\"$ref\":\"#/definitions/values\"},\"city\":{\"$ref\":\"#/definitions/values\"},\"localAdministrativeAuthority\":{\"$ref\":\"#/definitions/values\"},\"mobileNumber\":{\"$ref\":\"#/definitions/values\"},\"emailId\":{\"$ref\":\"#/definitions/values\"},\"CNEOrPINNumber\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianName\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianRIDOrUIN\":{\"$ref\":\"#/definitions/values\"},\"leftEye\":{\"$ref\":\"#/definitions/values\"},\"rightEye\":{\"$ref\":\"#/definitions/values\"},\"biometricScan1\":{\"$ref\":\"#/definitions/values\"},\"biometricScan2\":{\"$ref\":\"#/definitions/values\"},\"biometricScan3\":{\"$ref\":\"#/definitions/values\"}}}},\"definitions\":{\"values\":{\"type\":\"array\",\"additionalItems\":false,\"uniqueItems\":true,\"items\":{\"type\":\"object\",\"required\":[\"language\",\"label\",\"value\"],\"additionalProperties\":false,\"properties\":{\"language\":{\"type\":\"string\"},\"label\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}}}";
	private String titleAttrMissingSchema = "{\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{\"title\":\"identity\",\"description\":\"ThisholdsalltheattributesofanIdentity\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"firstName\":{\"$ref\":\"#/definitions/values\"},\"middleName\":{\"$ref\":\"#/definitions/values\"},\"lastName\":{\"$ref\":\"#/definitions/values\"},\"dateOfBirth\":{\"$ref\":\"#/definitions/values\"},\"gender\":{\"$ref\":\"#/definitions/values\"},\"addressLine1\":{\"$ref\":\"#/definitions/values\"},\"addressLine2\":{\"$ref\":\"#/definitions/values\"},\"addressLine3\":{\"$ref\":\"#/definitions/values\"},\"region\":{\"$ref\":\"#/definitions/values\"},\"province\":{\"$ref\":\"#/definitions/values\"},\"city\":{\"$ref\":\"#/definitions/values\"},\"localAdministrativeAuthority\":{\"$ref\":\"#/definitions/values\"},\"mobileNumber\":{\"$ref\":\"#/definitions/values\"},\"emailId\":{\"$ref\":\"#/definitions/values\"},\"CNEOrPINNumber\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianName\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianRIDOrUIN\":{\"$ref\":\"#/definitions/values\"},\"leftEye\":{\"$ref\":\"#/definitions/values\"},\"rightEye\":{\"$ref\":\"#/definitions/values\"},\"biometricScan1\":{\"$ref\":\"#/definitions/values\"},\"biometricScan2\":{\"$ref\":\"#/definitions/values\"},\"biometricScan3\":{\"$ref\":\"#/definitions/values\"}}}},\"definitions\":{\"values\":{\"type\":\"array\",\"additionalItems\":false,\"uniqueItems\":true,\"items\":{\"type\":\"object\",\"required\":[\"language\",\"label\",\"value\"],\"additionalProperties\":false,\"properties\":{\"language\":{\"type\":\"string\"},\"label\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}}}";
	private String schemaAttrMissingSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{\"title\":\"identity\",\"description\":\"ThisholdsalltheattributesofanIdentity\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"firstName\":{\"$ref\":\"#/definitions/values\"},\"middleName\":{\"$ref\":\"#/definitions/values\"},\"lastName\":{\"$ref\":\"#/definitions/values\"},\"dateOfBirth\":{\"$ref\":\"#/definitions/values\"},\"gender\":{\"$ref\":\"#/definitions/values\"},\"addressLine1\":{\"$ref\":\"#/definitions/values\"},\"addressLine2\":{\"$ref\":\"#/definitions/values\"},\"addressLine3\":{\"$ref\":\"#/definitions/values\"},\"region\":{\"$ref\":\"#/definitions/values\"},\"province\":{\"$ref\":\"#/definitions/values\"},\"city\":{\"$ref\":\"#/definitions/values\"},\"localAdministrativeAuthority\":{\"$ref\":\"#/definitions/values\"},\"mobileNumber\":{\"$ref\":\"#/definitions/values\"},\"emailId\":{\"$ref\":\"#/definitions/values\"},\"CNEOrPINNumber\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianName\":{\"$ref\":\"#/definitions/values\"},\"parentOrGuardianRIDOrUIN\":{\"$ref\":\"#/definitions/values\"},\"leftEye\":{\"$ref\":\"#/definitions/values\"},\"rightEye\":{\"$ref\":\"#/definitions/values\"},\"biometricScan1\":{\"$ref\":\"#/definitions/values\"},\"biometricScan2\":{\"$ref\":\"#/definitions/values\"},\"biometricScan3\":{\"$ref\":\"#/definitions/values\"}}}},\"definitions\":{\"values\":{\"type\":\"array\",\"additionalItems\":false,\"uniqueItems\":true,\"items\":{\"type\":\"object\",\"required\":[\"language\",\"label\",\"value\"],\"additionalProperties\":false,\"properties\":{\"language\":{\"type\":\"string\"},\"label\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}}}";
	private String propertiesAttrMissingSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false}";
	private String invalidJsonSchema="{}";
	private String propertiesMissingSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{}}}";
	private String identityAttrMissingSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{}}";
	private String noIdentityPropertiesJsonSchema = "{\"$id\":\"http://mosip.io/id_object/1.0/id_object.json\",\"$schema\":\"http://json-schema.org/draft-07/schema#\",\"title\": \"MOSIP ID schema\",\"description\":\"TestIDschematoreferto\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"identity\":{\"title\":\"identity\",\"description\":\"ThisholdsalltheattributesofanIdentity\",\"type\":\"object\",\"additionalProperties\":false,\"properties\":{}}},\"definitions\":{\"values\":{\"type\":\"array\",\"additionalItems\":false,\"uniqueItems\":true,\"items\":{\"type\":\"object\",\"required\":[\"language\",\"label\",\"value\"],\"additionalProperties\":false,\"properties\":{\"language\":{\"type\":\"string\"},\"label\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}}}";
	
	@Test
	public void validJsonSchema() throws IOException {
		validator.validateIdentitySchema(validJsonSchema);
	}
	
	@Test(expected=JsonSchemaException.class)
	public void invalidJsonSchema() throws IOException {
		validator.validateIdentitySchema(invalidJsonSchema);
	}

	@Test(expected = JsonSchemaException.class)
	public void validateSchemaFailureTitleAttrMissing() throws IOException {
		validator.validateIdentitySchema(titleAttrMissingSchema);
	}
	
	@Test(expected = JsonSchemaException.class)
	public void validateSchemaFailureSchemaAttrMissing() throws IOException {
		validator.validateIdentitySchema(schemaAttrMissingSchema);
	}
	
	@Test(expected = JsonSchemaException.class)
	public void validateSchemaFailureIdentityPropertiesAttrMissing() throws IOException {
		validator.validateIdentitySchema(propertiesMissingSchema);
	}
	
	@Test(expected = JsonSchemaException.class)
	public void validateSchemaFailureIdentityMissing() throws IOException {
		validator.validateIdentitySchema(identityAttrMissingSchema);
	}
	
	@Test(expected = JsonSchemaException.class)
	public void validateSchemaFailurePropertiesMissing() throws IOException {
		validator.validateIdentitySchema(propertiesAttrMissingSchema);
	}
	
	@Test(expected = JsonSchemaException.class)
	public void validateSchemaNoIdentityProperties() throws IOException {
		validator.validateIdentitySchema(noIdentityPropertiesJsonSchema);
	}
}
