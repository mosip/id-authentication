package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.LanguageType;
import io.mosip.authentication.service.config.IDAMappingConfig;


/**
 * DemoMatchTypeTest
 *
 * @author Rakesh Roshan
 */
public class DemoMatchTypeTest {

	SimpleDateFormat sdf = null;

	@Before
	public void setup() {
		sdf = new SimpleDateFormat("yyyy-MM-dd");
	}

	@Test
	public void TestPriAddressisNotNull() {
		assertNotNull(DemoMatchType.ADDR_PRI.getAllowedMatchingStrategy(FullAddressMatchingStrategy.EXACT.getType()));
		assertNotNull(DemoMatchType.ADDR_PRI.getMatchedBit());
		assertNotNull(DemoMatchType.ADDR_PRI.getMatchedBit());
	}

	@Test
	public void TestValidPriAddressExact() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.ADDR_PRI
				.getAllowedMatchingStrategy(FullAddressMatchingStrategy.EXACT.getType());
		assertEquals(matchStrategy.get(), FullAddressMatchingStrategy.EXACT);
	}

	@Ignore
	@Test
	public void TestAuthUsageDataBit() {
		Map<AuthUsageDataBit, Long> bitsCountMap = Arrays.stream(DemoMatchType.values()).map(dmt -> dmt.getUsedBit())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		assertEquals(AuthUsageDataBit.values().length, bitsCountMap.size());
		assertTrue(bitsCountMap.values().stream().allMatch(c -> c == 1));
	}

	@Test
	public void TestAuthUsageMatchedBit() {
		Map<AuthUsageDataBit, Long> bitsCountMap = Arrays.stream(DemoMatchType.values()).map(dmt -> dmt.getMatchedBit())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//		assertEquals(AuthUsageDataBit.values().length, bitsCountMap.size());
		assertTrue(bitsCountMap.values().stream().allMatch(c -> c == 1));
	}

	@Test
	public void TestInvalidPriAddressExact() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.ADDR_PRI
				.getAllowedMatchingStrategy(FullAddressMatchingStrategy.EXACT.getType());
		assertNotEquals(matchStrategy.get(), FullAddressMatchingStrategy.PARTIAL);
	}

	@Test
	public void TestPriAddressDemoInfoFetcherisNotNull() {
		assertNotNull(DemoMatchType.ADDR_PRI);
	}

	@Test
	public void TestAgeisNotNull() {
		assertNotNull(DemoMatchType.AGE);
	}

	@Test
	public void TestAgeIsExist() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.AGE
				.getAllowedMatchingStrategy(AgeMatchingStrategy.EXACT.getType());
		assertEquals(matchStrategy.get(), AgeMatchingStrategy.EXACT);
	}

	@Test
	public void testAgeIsExistBetweenPeriod() throws ParseException {

		String dob = "2000-09-25";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(dob));
		int dobYear = calendar.get(Calendar.YEAR);
		int curYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentAge = curYear - dobYear;

		Function<String, String> entityInfoFetcher = DemoMatchType.AGE.getEntityInfoMapper();
		String age = entityInfoFetcher.apply(dob);
		System.out.println(entityInfoFetcher);
		assertEquals(age, String.valueOf(currentAge));
	}
	
	@Ignore
	@Test
	public void TestFullAddress() {
		String tmpAddress = "exemple d'adresse ligne 1 exemple d'adresse ligne 2 exemple d'adresse ligne 3 Casablanca Tanger-Tétouan-Al Hoceima Fès-Meknès";
		Map<String, List<IdentityInfoDTO>> demoEntity = new HashMap<>();
		IdentityInfoDTO identityInfo1 = new IdentityInfoDTO();
		identityInfo1.setLanguage("FR");
		identityInfo1.setValue("exemple d'adresse ligne 1");
		List<IdentityInfoDTO> addressLine1 = new ArrayList<>();
		addressLine1.add(identityInfo1);
		demoEntity.put("addressLine1", addressLine1);

		IdentityInfoDTO identityInfoDTO2 = new IdentityInfoDTO();
		identityInfoDTO2.setLanguage("FR");
		identityInfoDTO2.setValue("exemple d'adresse ligne 2");
		List<IdentityInfoDTO> addressLine2 = new ArrayList<>();
		addressLine2.add(identityInfoDTO2);
		demoEntity.put("addressLine2", addressLine2);
		
		IdentityInfoDTO identityInfoDTO3 = new IdentityInfoDTO();
		identityInfoDTO3.setLanguage("FR");
		identityInfoDTO3.setValue("exemple d'adresse ligne 3");
		List<IdentityInfoDTO> addressLine3 = new ArrayList<>();
		addressLine3.add(identityInfoDTO2);
		demoEntity.put("addressLine3", addressLine3);
		
		IdentityInfoDTO location1 = new IdentityInfoDTO();
		location1.setLanguage("FR");
		location1.setValue("Casablanca");
		List<IdentityInfoDTO> location1list = new ArrayList<>();
		location1list.add(identityInfoDTO2);
		demoEntity.put("location1", location1list);
		
		IdentityInfoDTO location2 = new IdentityInfoDTO();
		location2.setLanguage("FR");
		location2.setValue("Tanger-Tétouan-Al Hoceima");
		List<IdentityInfoDTO> location2list = new ArrayList<>();
		location2list.add(identityInfoDTO2);
		demoEntity.put("location2", location2list);
		
		IdentityInfoDTO location3 = new IdentityInfoDTO();
		location3.setLanguage("FR");
		location3.setValue("Fès-Meknès");
		List<IdentityInfoDTO> location3list = new ArrayList<>();
		location3list.add(identityInfoDTO2);
		demoEntity.put("location3", location3list);
		
		
		
		Function<LanguageType, String> languageCodeFetcher = obj -> "FR";
		Function<String, Optional<String>> languageNameFetcher = obj -> Optional.of("french");

		IDAMappingConfig idMappingConfig = new IDAMappingConfig();
		List<String> fullAddress = new ArrayList<>();
		fullAddress.add(tmpAddress);
		idMappingConfig.setFullAddress(fullAddress);
		//FIXME fix this
//		assertEquals(tmpAddress, DemoMatchType.ADDR_SEC.getEntityInfo(demoEntity, languageCodeFetcher,
//				languageNameFetcher, locationInfoFetcher, idMappingConfig));
	}

	@Test
	public void TestPriAddressPartialisNotNull() {
		assertNotNull(DemoMatchType.ADDR_PRI.getAllowedMatchingStrategy(FullAddressMatchingStrategy.PARTIAL.getType()));
	}

	@Test
	public void TestValidPriAddressPartial() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.ADDR_PRI
				.getAllowedMatchingStrategy(FullAddressMatchingStrategy.PARTIAL.getType());
		assertEquals(matchStrategy.get(), FullAddressMatchingStrategy.PARTIAL);

	}

	@Test
	public void TestInvalidPriAddressPartial() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.ADDR_PRI
				.getAllowedMatchingStrategy(FullAddressMatchingStrategy.PARTIAL.getType());
		assertNotEquals(matchStrategy.get(), FullAddressMatchingStrategy.EXACT);
	}

	@Test
	public void TestSecondaryAddressisNotNull() {
		assertNotNull(DemoMatchType.ADDR_SEC);
	}
//
//	@Test
//	public void TestSecondaryAddressStrategy() {
//		String tmpAddress = "no 1 gandhi street chennai india 600111";
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setAddrLine1("no 1");
//		demoEntity.setAddrLine2("gandhi ");
//		demoEntity.setAddrLine3("street");
//		demoEntity.setLocationCode("600111");
//
//		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);
//
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.CITY, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("chennai"));
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.COUNTRY, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("india"));
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.ZIPCODE, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("600111"));
//		assertEquals(tmpAddress, DemoMatchType.ADDR_SEC.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher)
//				.toString().trim().replaceAll("\\s+", " "));
//	}

	@Test
	public void TestValidpriNameType() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.NAME_PRI
				.getAllowedMatchingStrategy(NameMatchingStrategy.EXACT.getType());
		assertEquals(matchStrategy.get(), NameMatchingStrategy.EXACT);
	}

	@Test
	public void TestInValidpriNameType() {
		Optional<MatchingStrategy> matchStrategy = DemoMatchType.NAME_PRI
				.getAllowedMatchingStrategy(NameMatchingStrategy.EXACT.getType());
		assertNotEquals(matchStrategy.get(), NameMatchingStrategy.PARTIAL);
	}

	@Test
	public void testPrimaryNameWithSecondaryLanguage() {
		LanguageType languageType = DemoMatchType.NAME_PRI.getLanguageType();
		assertEquals(languageType.name(), LanguageType.PRIMARY_LANG.name());
	}
	@Test
	public void testPrimaryNameWithUnmatchedLanguage() {
		LanguageType languageType = DemoMatchType.NAME_PRI.getLanguageType();
		assertNotEquals(languageType.name(), LanguageType.SECONDARY_LANG.name());
	}
	
	@Test
	public void TestpriName() {
////		DemoEntity demoEntity = new DemoEntity();
////		demoEntity.setFirstName("dinesh");
////		demoEntity.setMiddleName("karuppiah");
////		demoEntity.setLastName("thiagarajan");
////		DemoDTO demoDTO = new DemoDTO();
////		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
////		personalIdentityDTO.setNamePri("dinesh karuppiah thiagarajan");
////		demoDTO.setPi(personalIdentityDTO);
//		assertEquals(DemoMatchType.NAME_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.NAME_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));

	}

//	@Test
//	public void TestSecondaryNameisNotNull() {
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setFirstName("dinesh");
//		demoEntity.setMiddleName("karuppiah");
//		demoEntity.setLastName("thiagarajan");
//		DemoDTO demoDTO = new DemoDTO();
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setNameSec("dinesh karuppiah thiagarajan");
//		demoDTO.setPi(personalIdentityDTO);
//		assertEquals(DemoMatchType.NAME_SEC.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.NAME_SEC.getEntityInfoFetcher().getInfo(demoEntity, null));
//
//	}

	@Test
	public void testSecondaryNameisExact() {
		Optional<MatchingStrategy> allowedMatchingStrategy = DemoMatchType.NAME_SEC.
				getAllowedMatchingStrategy(NameMatchingStrategy.EXACT.getType());
		assertEquals(allowedMatchingStrategy.get(), NameMatchingStrategy.EXACT);
	}
	
	@Test
	public void testSecondaryNameForUnmatchedMatchingStrategy() {
		Optional<MatchingStrategy> allowedMatchingStrategy = DemoMatchType.NAME_SEC.
				getAllowedMatchingStrategy(NameMatchingStrategy.EXACT.getType());
		assertNotEquals(allowedMatchingStrategy.get(), NameMatchingStrategy.PARTIAL);
	}
	@Test
	public void testSecondaryNameWithSecondaryLanguage() {
		LanguageType languageType = DemoMatchType.NAME_SEC.getLanguageType();
		assertEquals(languageType.name(), LanguageType.SECONDARY_LANG.name());
	}
	@Test
	public void testSecondaryNameWithUnmatchedLanguage() {
		LanguageType languageType = DemoMatchType.NAME_SEC.getLanguageType();
		assertNotEquals(languageType.name(), LanguageType.PRIMARY_LANG.name());
	}
	
	@Test
	public void TestGenderMatchStrategyisNotNull() {
		assertNotNull(GenderMatchingStrategy.EXACT);
	}

	@Test
	public void TestGenderMatchStrategy() {
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setGender("male");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setPi(personalIdentityDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setGenderCode("male");
//		assertEquals(DemoMatchType.GENDER.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.GENDER.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestAgeMatchStrategy() throws ParseException {
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setAge(17);
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setPi(personalIdentityDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setDob(sdf.parse("2001-07-16"));
//		assertEquals(DemoMatchType.AGE.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.AGE.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestDOBMatchStrategy() throws ParseException {
//		LocalDateTime localDate = LocalDateTime.of(1998, 01, 21, 07, 22, 11);
//		long epochSecond = localDate.toEpochSecond(ZoneOffset.UTC);
//		Date reqDate = new Date(epochSecond);
//		LocalDateTime localDate2 = LocalDateTime.of(1998, 01, 21, 07, 22, 11);
//		long entitiyDateEpochSecond = localDate2.toEpochSecond(ZoneOffset.UTC);
//		Date entityDate = new Date(entitiyDateEpochSecond);
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setDob(reqDate.toString());
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setPi(personalIdentityDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setDob(entityDate);
//		assertEquals(DemoMatchType.DOB.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.DOB.getEntityInfoFetcher().getInfo(demoEntity, null).toString());
	}

	@Test
	public void TestMobileStrategyisNull() {
//		assertNotNull(DemoMatchType.MOBILE);
	}

	@Test
	public void TestMobileMatchStrategy() {
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setPhone("1234567890");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setPi(personalIdentityDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setMobile("1234567890");
//		assertEquals(DemoMatchType.MOBILE.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.MOBILE.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestEmailStrategyisNotNull() {
//		assertNotNull(DemoMatchType.EMAIL);
	}

	@Test
	public void TestEmailStrategy() {
//		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
//		personalIdentityDTO.setEmail("test@test.com");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setPi(personalIdentityDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setEmail("test@test.com");
//		assertEquals(DemoMatchType.EMAIL.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.EMAIL.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestAddrLine1isNotnull() {
//		assertNotNull(DemoMatchType.ADDR_LINE1_PRI);
	}

	@Test
	public void TestAddrLine1Strategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setAddrLine1Pri("no1 gandhi street");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setAddrLine1("no1 gandhi street");
//		assertEquals(DemoMatchType.ADDR_LINE1_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.ADDR_LINE1_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestAddrLine2isNotnull() {
//		assertNotNull(DemoMatchType.ADDR_LINE2_PRI);
	}

	@Test
	public void TestAddrLine2Strategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setAddrLine2Pri("kamarajapuram");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setAddrLine2("kamarajapuram");
//		assertEquals(DemoMatchType.ADDR_LINE2_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.ADDR_LINE2_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestAddrLine3isNotnull() {
//		assertNotNull(DemoMatchType.ADDR_LINE3_PRI);
	}

	@Test
	public void TestAddrLine3Strategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setAddrLine3Pri("chennai");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setAddrLine3("chennai");
//		assertEquals(DemoMatchType.ADDR_LINE3_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.ADDR_LINE3_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
	}

	@Test
	public void TestCountryStrategyisNotNull() {
//		assertNotNull(DemoMatchType.COUNTRY_PRI);
	}

	@Test
	public void TestCountryStrategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setCountryPri("india");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setLocationCode("india");
//		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);
//
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.COUNTRY, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("india"));
//
//		assertEquals(DemoMatchType.COUNTRY_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.COUNTRY_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
	}

	@Test
	public void TestPincodeStrategyisNotNull() {
//		assertNotNull(DemoMatchType.PINCODE_PRI);
	}

	@Test
	public void TestPincodeStrategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setPinCodePri("600117");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setLocationCode("600117");
//		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);
//
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.ZIPCODE, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("600117"));
//		assertEquals(DemoMatchType.PINCODE_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.PINCODE_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
	}

	@Test
	public void TestCityStrategyisNotNull() {
//		assertNotNull(DemoMatchType.CITY_PRI);
	}

	@Test
	public void TestCityStrategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setCityPri("Chennai");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setLocationCode("600117");
//		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);
//
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.CITY, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("Chennai"));
//		assertEquals(DemoMatchType.CITY_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.CITY_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
	}

	@Test
	public void TestStateStrategyisNotNull() {
//		assertNotNull(DemoMatchType.STATE_PRI);
	}

	@Test
	public void TestStateStrategy() {
//		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
//		personalAddressDTO.setStatePri("TamilNadu");
//		DemoDTO demoDTO = new DemoDTO();
//		demoDTO.setAd(personalAddressDTO);
//		DemoEntity demoEntity = new DemoEntity();
//		demoEntity.setLocationCode("600117");
//		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);
//
//		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.STATE, demoEntity.getLocationCode()))
//				.thenReturn(Optional.of("TamilNadu"));
//		assertEquals(DemoMatchType.STATE_PRI.getDemoInfoFetcher().apply(demoDTO).get(),
//				DemoMatchType.STATE_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
	}

}
