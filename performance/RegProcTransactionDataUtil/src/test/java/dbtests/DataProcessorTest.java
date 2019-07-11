package dbtests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import app.service.DataProcessor;

public class DataProcessorTest {

	private static DataProcessor dataProcessor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		dataProcessor = new DataProcessor();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcessData() {
		
		dataProcessor.processData();

	}

}
