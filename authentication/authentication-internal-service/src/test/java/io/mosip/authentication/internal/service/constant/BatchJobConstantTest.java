package io.mosip.authentication.internal.service.constant;

import org.junit.Test;

import static org.junit.Assert.*;

public class BatchJobConstantTest {

	@Test
	public void testDbSchemaName() {
		BatchJobConstant constant = BatchJobConstant.DB_SCHEMA_NAME;
		String expectedValue = "mosip.id.authentication.internal-service.schemaName";
		
		assertEquals("Should return correct schema name property", expectedValue, constant.getValue());
	}
	
	@Test
	public void testDbTableName() {
		BatchJobConstant constant = BatchJobConstant.DB_TABLE_NAME;
		String expectedValue = "mosip.id.authentication.internal-service.tableName";
		
		assertEquals("Should return correct table name property", expectedValue, constant.getValue());
	}
	
	@Test
	public void testGetValue() {
		String value = BatchJobConstant.DB_SCHEMA_NAME.getValue();
		
		assertNotNull("Value should not be null", value);
		assertFalse("Value should not be empty", value.isEmpty());
	}
}
