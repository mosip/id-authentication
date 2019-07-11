package io.mosip.registrationProcessor.perf.util;

import java.text.DecimalFormat;
import java.util.*;
import java.util.Random;

import io.mosip.registrationProcessor.perf.dto.DOBDto;
import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.resgistrationProcessor.perf.dbaccess.RegProcPerfDaoImpl;

public class TestDataUtility {

	private RegProcPerfDaoImpl dao;
	private String firstName;
	private String lastName;

	public Map<String, String> arabicTexts;
	public Map<String, String> frenchTexts;

	public TestDataUtility() {
		arabicTexts = new HashMap<>();
		frenchTexts = new HashMap<>();
		dao = new RegProcPerfDaoImpl();
		populateArabicTexts();
		populateFrenchTexts();
	}

	private void populateArabicTexts() {
		arabicTexts.put("Foreigner", "أجنبي");
		arabicTexts.put("Non-Foreigner", "غير أجنبي");
		arabicTexts.put("Male", "الذكر");
		arabicTexts.put("Female", "أنثى");
	}

	private void populateFrenchTexts() {
		frenchTexts.put("Foreigner", "Étranger");
		frenchTexts.put("Non-Foreigner", "Non-étranger");
		frenchTexts.put("Male", "Mâle");
		frenchTexts.put("Female", "Femelle");
	}

	public Long generatePhoneNumber() {
		Long phone = 9999999999L;
		String phoneStr = "";
		// phone number can start with 6,7,8,9
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(4);
		phoneStr += 6 + randomInt;
		for (int i = 0; i < 9; i++) {
			randomInt = randomGenerator.nextInt(10);
			phoneStr += randomInt;
		}
		phone = Long.parseLong(phoneStr);
		return phone;
	}

	public String generateEmailAddress() {
		String email = firstName + "." + lastName + "@test.com";
		return email;
	}

	public String generateResidenceStatusEng() {
		String[] residenceStatusArr = new String[] { "Foreigner", "Non-Foreigner" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return residenceStatusArr[randomInt];
	}

	public String generateResidenceStatusFra() {
		String[] residenceStatusArr = new String[] { "Étranger", "Non-étranger" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return residenceStatusArr[randomInt];
	}

	public String generateResidenceStatusAra() {
		String[] residenceStatusArr = new String[] { "أجنبي", "غير أجنبي" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return residenceStatusArr[randomInt];
	}

	public String genrateGenderEng() {
		String[] genderEngArr = new String[] { "Male", "Female" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return genderEngArr[randomInt];
	}

	public String genrateGenderFre() {
		String[] genderEngArr = new String[] { "Mâle", "Femelle" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return genderEngArr[randomInt];
	}

	public String genrateGenderAra() {
		String[] genderEngArr = new String[] { "الذكر", "أنثى" };
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(2);
		return genderEngArr[randomInt];
	}

	public DOBDto generateDOB() {
		// YYYY/mm/DD
		DOBDto dobDto = new DOBDto();
		DecimalFormat formatter = new DecimalFormat("00");
		int YYYY = generateYear();
		String MM = formatter.format(generateMonth());
		String dd = formatter.format(generateDate());

		String date = YYYY + "/" + MM + "/" + dd;
		int age = 2019 - YYYY;
		dobDto.setDate(date);
		dobDto.setAge(age);
		return dobDto;
	}

	private int generateYear() {
		int minYear = 1975;
		int yearLimit = 35;
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(yearLimit) + 1;
		int year = minYear + randomInt;
		return year;
	}

	private int generateMonth() {
		int monthLimit = 11;
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(monthLimit) + 1;
		int month = 1 + randomInt;
		return month;

	}

	private int generateDate() {
		int dateLimit = 26;
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(dateLimit) + 1;
		int date = 1 + randomInt;
		return date;
	}

	public String generateFullName() {
		firstName = generateRandomName();
		lastName = generateRandomName();
		String fullName = firstName + " " + lastName;
		return fullName;
	}

	private String generateRandomName() {
		String name = "";
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(26);
		int capitalAscii = 65 + randomInt;
		name += (char) capitalAscii;
		randomInt = randomGenerator.nextInt(3);
		int nameLength = 3 + randomInt;
		for (int i = 0; i < nameLength; i++) {
			randomInt = randomGenerator.nextInt(26);
			int randomAscii = 97 + randomInt;
			name += (char) randomAscii;
		}
		return name;
	}

	public String generateAddressLine() {
		String addressLine = "";
		int min = 97;
		int max = 122;
		int addresslength = 5;
		for (int i = 0; i < addresslength; i++) {
			// Generate a rando number from 1 to 26
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(26);
			int randomAscii = 97 + randomInt;
			char randomChar = (char) randomAscii;
			addressLine += randomChar;

		}
		return addressLine;
	}

	public String getCountryCode() {
		// String result = "";
		// List<Location> countryCodes = dao.getCountry();
		List<Location> countries = dao.getCountry();

		Location result = obtainRandonLocation(countries);
		return result.getCode();
	}

	public Location getLocation(String parentLocationCode, int hierarchy) {
		List<Location> locations = dao.getLocations(parentLocationCode, hierarchy);
		Location location = obtainRandonLocation(locations);
		return location;
	}

	private Location obtainRandonLocation(List<Location> locations) {
		Random randomGenerator = new Random();
		int randomIndex = randomGenerator.nextInt(locations.size());
		// System.out.println("randomIndex: " + randomIndex);
		return locations.get(randomIndex);
	}

	private String obtainRandonElementFromList(List<String> strings) {
		Random randomGenerator = new Random();
		int randomIndex = randomGenerator.nextInt(strings.size());
		// System.out.println("randomIndex : " + randomIndex);
		return strings.get(randomIndex);
	}

	public String generateCnieNumber() {
		String cnie = "";
		// phone number can start with 6,7,8,9
		Random randomGenerator = new Random();
		int randomInt = 0;

		for (int i = 0; i < 10; i++) {
			randomInt = randomGenerator.nextInt(10);
			cnie += randomInt;
		}
		return cnie;
	}

}
