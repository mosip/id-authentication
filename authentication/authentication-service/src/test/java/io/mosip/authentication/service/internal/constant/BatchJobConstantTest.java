package io.mosip.authentication.service.internal.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BatchJobConstantTest {

	@Test
	public void testDbSchemaName() {
		BatchJobConstant constant = BatchJobConstant.DB_SCHEMA_NAME;
		String expectedValue = "mosip.id.authentication.internal-service.schemaName";
		
		assertEquals(expectedValue, constant.getValue(), "Should return correct schema name property");
	}
	
	@Test
	public void testDbTableName() {
		BatchJobConstant constant = BatchJobConstant.DB_TABLE_NAME;
		String expectedValue = "mosip.id.authentication.internal-service.tableName";
		
		assertEquals(expectedValue, constant.getValue(), "Should return correct table name property");
	}
	
	@Test
	public void testGetValue() {
		String value = BatchJobConstant.DB_SCHEMA_NAME.getValue();
		
		assertNotNull(value, "Value should not be null");
		assertFalse(value.isEmpty(), "Value should not be empty");
	}
}
