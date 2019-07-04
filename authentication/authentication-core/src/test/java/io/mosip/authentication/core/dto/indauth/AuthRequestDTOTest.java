package io.mosip.authentication.core.dto.indauth;
/*package org.mosip.auth.core.dto.indauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

*//**
 * {@code AuthRequestDTOTest} is check the functionality for add
 * {@code annotation} to attribute level validation for {@link AuthRequestDTO}
 * 
 * @author Rakesh Roshan
 *//*
public class AuthRequestDTOTest {

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	*//**
	 * Success test: Test for conversion of Json to Object and vice-versa
	 * 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 *//*
	@Test
	public void testValidateJsonToObjectConversion() throws JsonGenerationException, JsonMappingException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setType(IDType.UIN);
		authRequestDTO.setUniqueID("1234567890");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertTrue(violations.isEmpty());

		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		mapper.writeValue(byteArrayOutputStream, authRequestDTO);

		String json = byteArrayOutputStream.toString();
		AuthRequestDTO authRequestDTO1 = mapper.readValue(json, AuthRequestDTO.class);

		assertEquals(authRequestDTO1.toString(), authRequestDTO.toString());
	}

	*//**
	 * Failure test: Test for conversion of Json to Object and vice-versa with
	 * mismatched input. Expected exception is {@link InvalidFormatException}
	 * 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 *//*
	@Test(expected = InvalidFormatException.class)
	public void testValidateJsonToObjectConversionWithMismatchedInput()
			throws JsonGenerationException, JsonMappingException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setType(IDType.UIN);
		authRequestDTO.setUniqueID("1234567890");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertTrue(violations.isEmpty());
		ObjectMapper mapper = new ObjectMapper();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		mapper.writeValue(byteArrayOutputStream, authRequestDTO);
		String json = byteArrayOutputStream.toString();
		json = json.replace("\"type\":\"D\"", "\"type\":\"A\"");

		AuthRequestDTO authRequestDTO1 = mapper.readValue(json, AuthRequestDTO.class);

		assertNotEquals(authRequestDTO1, authRequestDTO);

	}

	*//**
	 * Success test: Test for conversion of XML to Object and vice-versa.
	 * 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 *//*
	@Test()
	public void testValidateXMLToObjectConversion() throws JsonGenerationException, JsonMappingException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setType(IDType.UIN);
		authRequestDTO.setUniqueID("1234567890");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertTrue(violations.isEmpty());

		XmlMapper xmlMapper = new XmlMapper();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		xmlMapper.writeValue(byteArrayOutputStream, authRequestDTO);

		String xml = byteArrayOutputStream.toString();

		AuthRequestDTO authRequestDTO1 = xmlMapper.readValue(xml, AuthRequestDTO.class);

		assertEquals(authRequestDTO1, authRequestDTO);
	}

	*//**
	 * Failure test: Test for conversion of XML to Object and vice-versa with
	 * mismatched input. Expected exception is {@link InvalidFormatException}
	 * 
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 *//*
	@Test(expected = InvalidFormatException.class)
	public void testValidateXMLToObjectConversionWithMismatchedInput()
			throws JsonGenerationException, JsonMappingException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setType(IDType.UIN);
		authRequestDTO.setUniqueID("1234567890");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertTrue(violations.isEmpty());

		XmlMapper xmlMapper = new XmlMapper();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		xmlMapper.writeValue(byteArrayOutputStream, authRequestDTO);

		String xml = byteArrayOutputStream.toString();
		xml = xml.replace("<type>D</type>", "<type>A</type>");

		AuthRequestDTO authRequestDTO1 = xmlMapper.readValue(xml, AuthRequestDTO.class);

		assertNotEquals(authRequestDTO1, authRequestDTO);
	}

	*//**
	 * Success Test: Test to validate {@code uniqueID} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For valid
	 * {@code uniqueID}, {@link ConstraintViolation} size is 0 i.e empty.
	 *//*
	@Test
	public void testValidUniqueID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertTrue(violations.isEmpty());

	}

	*//**
	 * Failure Test: Test to validate {@code uniqueID} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For invalid
	 * {@code uniqueID}, {@link ConstraintViolation} size is 1.
	 *//*
	@Test
	public void testInvalidUniqueID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("123456789");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("UniqeID size must be 10", violation.getMessage());
	}

	*//**
	 * Success Test: Test to validate {@code auaCode} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For valid
	 * {@code auaCode}, {@link ConstraintViolation} size is 0 i.e empty.
	 *//*
	@Test
	public void testValidAuaCode() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setAuaCode("1ABCdeF1099864");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 0);

	}

	*//**
	 * Failure Test: Test to validate {@code auaCode} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For invalid
	 * {@code auaCode}, {@link ConstraintViolation} size is 1.
	 *//*
	@Test
	public void testInvalidAuaCode() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setAuaCode("10");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("auaCode size should be min 10", violation.getMessage());
	}

	*//**
	 * Success Test: Test to validate {@code txnID} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For valid
	 * {@code txnID}, {@link ConstraintViolation} size is 0 i.e empty.
	 *//*
	@Test
	public void testValidTxnID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setTxnID("6789azxt6f");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 0);

	}

	*//**
	 * Failure Test: Test to validate {@code txnID} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For invalid
	 * {@code txnID}, {@link ConstraintViolation} size is 1.
	 *//*
	@Test
	public void testInvalidTxnID() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setTxnID("6789az");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("txnID size must be 10", violation.getMessage());

	}

	*//**
	 * Success Test: Test to validate {@code asaLicenseKey} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For valid
	 * {@code asaLicenseKey}, {@link ConstraintViolation} size is 0 i.e empty.
	 *//*
	@Test
	public void testValidAsaLicenseKey() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setAsaLicenseKey("6789azxt6f");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 0);

	}

	*//**
	 * Failure Test: Test to validate {@code asaLicenseKey} against declared
	 * {@code annotation} at this attribute in {@link AuthRequestDTO}. For invalid
	 * {@code asaLicenseKey}, {@link ConstraintViolation} size is 1.
	 *//*
	@Test
	public void testInvalidAsaLicenseKey() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setAsaLicenseKey("6789az");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("asaLicenseKey size must be 10", violation.getMessage());

	}

	*//**
	 * Set Date as past or present date
	 *//*
	@Test
	public void testValidRequestTime() throws ParseException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "31/09/2001"; // add past or present date
		Date date = sdf.parse(dateInString);
		authRequestDTO.setRequestTime(date);

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 0);

	}

	*//**
	 * Set invalid date as future date
	 *//*
	@Test
	public void testInvalidRequestTime() throws ParseException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		String dateInString = "31/09/2020"; // add feature date
		Date date = sdf.parse(dateInString);
		authRequestDTO.setRequestTime(date);

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("Date should be past or present date", violation.getMessage());

	}

	*//**
	 * Set version with range integer(1) and fraction(1) digit
	 *//*
	@Test
	public void testValidVersion() throws ParseException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setVersion("1.0");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 0);

	}

	*//**
	 * Set invalid version with range integer(more than 1) and fraction(1) digit
	 *//*
	@Test
	public void testInvalidVersion() throws ParseException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUniqueID("1234567890");
		authRequestDTO.setVersion("11.0");

		Set<ConstraintViolation<AuthRequestDTO>> violations = validator.validate(authRequestDTO);
		assertEquals(violations.size(), 1);

		ConstraintViolation<AuthRequestDTO> violation = violations.iterator().next();
		assertEquals("version range max integer 1 digit and max fraction 1 digit only", violation.getMessage());

	}

}
*/