package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.DemoDTO;
import io.mosip.authentication.core.dto.indauth.PersonalAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalFullAddressDTO;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDTO;

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

	@Test
	public void TestAuthUsageDataBit() {
		Map<AuthUsageDataBit, Long> bitsCountMap = Arrays.stream(DemoMatchType.values()).map(dmt -> dmt.getUsedBit())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

//		assertEquals(AuthUsageDataBit.values().length, bitsCountMap.size());
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
	public void TestPriAddressDemoInfoFetcher() {
		String tmpAddress = "no 11 gandhi street";
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		personalFullAddressDTO.setAddrPri("no 11 gandhi street");
		//demodto.setLangPri("english");
		demodto.setFad(personalFullAddressDTO);
		assertEquals(tmpAddress, DemoMatchType.ADDR_PRI.getDemoInfoFetcher().getInfo(demodto));
		assertNotEquals("invalid", DemoMatchType.ADDR_PRI.getDemoInfoFetcher().getInfo(demodto));
	}
	
	@Test
	public void TestSecAddressDemoInfoFetcher() {
		String tmpAddress = "no 11 gandhi street";
		DemoDTO demodto = new DemoDTO();
		PersonalFullAddressDTO personalFullAddressDTO = new PersonalFullAddressDTO();
		personalFullAddressDTO.setAddrPri("no 11 gandhi street");
		//demodto.setLangPri("english");
		demodto.setFad(personalFullAddressDTO);
		assertEquals(tmpAddress, DemoMatchType.ADDR_SEC.getDemoInfoFetcher().getInfo(demodto));
		assertNotEquals("invalid", DemoMatchType.ADDR_SEC.getDemoInfoFetcher().getInfo(demodto));
	}

	@Test
	public void TestFullAddress() {
		String tmpAddress = "no 1 gandhi street chennai india";
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setAddrLine1("no 1");
		demoEntity.setAddrLine2("gandhi street");
		demoEntity.setLocationCode("CHNN");
		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);

		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.CITY, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("chennai"));
		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.COUNTRY, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("india"));
		assertEquals(tmpAddress, DemoMatchType.ADDR_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher)
				.toString().trim());
		assertNotEquals("invalid address", DemoMatchType.ADDR_PRI.getEntityInfoFetcher()
				.getInfo(demoEntity, locationInfoFetcher).toString().trim());
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

	@Test
	public void TestSecondaryAddressStrategy() {
		String tmpAddress = "no 1 gandhi street chennai india 600111";
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setAddrLine1("no 1");
		demoEntity.setAddrLine2("gandhi ");
		demoEntity.setAddrLine3("street");
		demoEntity.setLocationCode("600111");

		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);

		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.CITY, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("chennai"));
		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.COUNTRY, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("india"));
		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.ZIPCODE, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("600111"));
		assertEquals(tmpAddress, DemoMatchType.ADDR_SEC.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher)
				.toString().trim().replaceAll("\\s+", " "));
		assertNotEquals("invalid address", DemoMatchType.ADDR_SEC.getEntityInfoFetcher()
				.getInfo(demoEntity, locationInfoFetcher).toString().trim().replaceAll("\\s+", " "));
	}

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
	public void TestpriName() {
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setFirstName("dinesh");
		demoEntity.setMiddleName("karuppiah");
		demoEntity.setLastName("thiagarajan");
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setNamePri("dinesh karuppiah thiagarajan");
		demoDTO.setPi(personalIdentityDTO);
		assertEquals(DemoMatchType.NAME_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.NAME_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));

		assertNotEquals("ganesh", DemoMatchType.NAME_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestSecondaryNameisNotNull() {
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setFirstName("dinesh");
		demoEntity.setMiddleName("karuppiah");
		demoEntity.setLastName("thiagarajan");
		DemoDTO demoDTO = new DemoDTO();
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setNameSec("dinesh karuppiah thiagarajan");
		demoDTO.setPi(personalIdentityDTO);
		assertEquals(DemoMatchType.NAME_SEC.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.NAME_SEC.getEntityInfoFetcher().getInfo(demoEntity, null));

		assertNotEquals("ganesh", DemoMatchType.NAME_SEC.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestGenderMatchStrategyisNotNull() {
		assertNotNull(GenderMatchingStrategy.EXACT);
	}

	@Test
	public void TestGenderMatchStrategy() {
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setGender("male");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setPi(personalIdentityDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setGenderCode("male");
		assertEquals(DemoMatchType.GENDER.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.GENDER.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals("female", DemoMatchType.GENDER.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestAgeMatchStrategy() throws ParseException {
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setAge(17);
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setPi(personalIdentityDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setDob(sdf.parse("2001-07-16"));
		assertEquals(DemoMatchType.AGE.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.AGE.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals(40, DemoMatchType.AGE.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestDOBMatchStrategy() throws ParseException {
		LocalDateTime localDate = LocalDateTime.of(1998, 01, 21, 07, 22, 11);
		long epochSecond = localDate.toEpochSecond(ZoneOffset.UTC);
		Date reqDate = new Date(epochSecond);
		LocalDateTime localDate2 = LocalDateTime.of(1998, 01, 21, 07, 22, 11);
		long entitiyDateEpochSecond = localDate2.toEpochSecond(ZoneOffset.UTC);
		Date entityDate = new Date(entitiyDateEpochSecond);
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setDob(reqDate.toString());
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setPi(personalIdentityDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setDob(entityDate);
		assertEquals(DemoMatchType.DOB.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.DOB.getEntityInfoFetcher().getInfo(demoEntity, null).toString());
		assertNotEquals(localDate2, DemoMatchType.DOB.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestMobileStrategyisNull() {
		assertNotNull(DemoMatchType.MOBILE);
	}

	@Test
	public void TestMobileMatchStrategy() {
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setPhone("1234567890");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setPi(personalIdentityDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setMobile("1234567890");
		assertEquals(DemoMatchType.MOBILE.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.MOBILE.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals(40, DemoMatchType.MOBILE.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestEmailStrategyisNotNull() {
		assertNotNull(DemoMatchType.EMAIL);
	}

	@Test
	public void TestEmailStrategy() {
		PersonalIdentityDTO personalIdentityDTO = new PersonalIdentityDTO();
		personalIdentityDTO.setEmail("test@test.com");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setPi(personalIdentityDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setEmail("test@test.com");
		assertEquals(DemoMatchType.EMAIL.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.EMAIL.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals("test@Test1.com", DemoMatchType.EMAIL.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestAddrLine1isNotnull() {
		assertNotNull(DemoMatchType.ADDR_LINE1_PRI);
	}

	@Test
	public void TestAddrLine1Strategy() {
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		personalAddressDTO.setAddrLine1Pri("no1 gandhi street");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setAd(personalAddressDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setAddrLine1("no1 gandhi street");
		assertEquals(DemoMatchType.ADDR_LINE1_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.ADDR_LINE1_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals("invalid address", DemoMatchType.ADDR_LINE1_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestAddrLine2isNotnull() {
		assertNotNull(DemoMatchType.ADDR_LINE2_PRI);
	}

	@Test
	public void TestAddrLine2Strategy() {
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		personalAddressDTO.setAddrLine2Pri("kamarajapuram");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setAd(personalAddressDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setAddrLine2("kamarajapuram");
		assertEquals(DemoMatchType.ADDR_LINE2_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.ADDR_LINE2_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals("invalid address", DemoMatchType.ADDR_LINE2_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestAddrLine3isNotnull() {
		assertNotNull(DemoMatchType.ADDR_LINE3_PRI);
	}

	@Test
	public void TestAddrLine3Strategy() {
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		personalAddressDTO.setAddrLine3Pri("chennai");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setAd(personalAddressDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setAddrLine3("chennai");
		assertEquals(DemoMatchType.ADDR_LINE3_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.ADDR_LINE3_PRI.getEntityInfoFetcher().getInfo(demoEntity, null));
		assertNotEquals("invalid address", DemoMatchType.ADDR_LINE3_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestCountryStrategyisNotNull() {
		assertNotNull(DemoMatchType.COUNTRY_PRI);
	}

	@Test
	public void TestCountryStrategy() {
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		personalAddressDTO.setCountryPri("india");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setAd(personalAddressDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setLocationCode("india");
		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);

		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.COUNTRY, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("india"));

		assertEquals(DemoMatchType.COUNTRY_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.COUNTRY_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
		assertNotEquals("invalid address", DemoMatchType.COUNTRY_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

	@Test
	public void TestPincodeStrategyisNotNull() {
		assertNotNull(DemoMatchType.PINCODE_PRI);
	}

	@Test
	public void TestPincodeStrategy() {
		PersonalAddressDTO personalAddressDTO = new PersonalAddressDTO();
		personalAddressDTO.setPinCodePri("600117");
		DemoDTO demoDTO = new DemoDTO();
		demoDTO.setAd(personalAddressDTO);
		DemoEntity demoEntity = new DemoEntity();
		demoEntity.setLocationCode("600117");
		LocationInfoFetcher locationInfoFetcher = Mockito.mock(LocationInfoFetcher.class);

		Mockito.when(locationInfoFetcher.getLocation(LocationLevel.ZIPCODE, demoEntity.getLocationCode()))
				.thenReturn(Optional.of("600117"));
		assertEquals(DemoMatchType.PINCODE_PRI.getDemoInfoFetcher().getInfo(demoDTO),
				DemoMatchType.PINCODE_PRI.getEntityInfoFetcher().getInfo(demoEntity, locationInfoFetcher));
		assertNotEquals("600000", DemoMatchType.PINCODE_PRI.getDemoInfoFetcher().getInfo(demoDTO));
	}

}
