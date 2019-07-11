package io.mosip.registrationProcessor.perf.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.registrationProcessor.perf.util.TestDataUtility;

public class TestDataGenerationCheck {

	TestDataUtility testDataUtil;

	@Before
	public void setUp() throws Exception {
		testDataUtil = new TestDataUtility();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateAddressLine() {
		testDataUtil.generateAddressLine();
	}

	@Test
	public void testGetCountry() {
		String countryCode = testDataUtil.getCountryCode();
		Location region = testDataUtil.getLocation(countryCode, 1);
		System.out.println("region Code:- " + region.getCode());
		System.out.println("region Name:- " + region.getName());
		Location province = testDataUtil.getLocation(region.getCode(), 2);
		System.out.println("province Code:- " + province.getCode());
		System.out.println("province Name:- " + province.getName());
		Location city = testDataUtil.getLocation(province.getCode(), 3);
		System.out.println("city Code:- " + city.getCode());
		System.out.println("city Name:- " + city.getName());
		Location localAdministrativeAuthority = testDataUtil.getLocation(city.getCode(), 4);
		System.out.println("localAdministrativeAuthority Code:- " + localAdministrativeAuthority.getCode());
		System.out.println("localAdministrativeAuthority Name:- " + localAdministrativeAuthority.getName());
		Location postalCode = testDataUtil.getLocation(localAdministrativeAuthority.getCode(), 5);
		System.out.println("postalCode Code:- " + postalCode.getCode());
		System.out.println("postalCode Name:- " + postalCode.getName());

	}

}
