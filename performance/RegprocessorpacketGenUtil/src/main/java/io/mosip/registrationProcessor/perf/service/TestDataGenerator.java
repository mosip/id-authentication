/**
 * 
 */
package io.mosip.registrationProcessor.perf.service;

import java.io.IOException;
import java.util.*;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import io.mosip.registrationProcessor.perf.dto.DOBDto;
import io.mosip.registrationProcessor.perf.dto.RegDataCSVDto;
import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.registrationProcessor.perf.util.CSVUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TestDataUtility;

/**
 * @author Gaurav Sharan
 *
 */
public class TestDataGenerator {

	TestDataUtility testDataUtil;

	public TestDataGenerator() {
		testDataUtil = new TestDataUtility();
	}

//	public void readDataFromCSV() {
//		String filePath = "E:\\MOSIP_PT\\Data\\reg_data_sample.csv";
//		CSVUtil.loadObjectsFromCSV(filePath);
//	}

	public void generateTestDataInCSV(String csvPath) {

		List<RegDataCSVDto> csvDtoList = new ArrayList<>();
		List<String[]> csvDtoData = new ArrayList<String[]>();

		for (int i = 0; i <= PropertiesUtil.NUMBER_OF_TEST_PACKETS; i++) {
			if (i == 0) {
				String[] header = new String[] { "fullName", "dateOfBirth", "age", "gender", "residenceStatus",
						"addressLine1", "addressLine2", "addressLine3", "region", "province", "city", "postalCode",
						"phone", "email", "localAdministrativeAuthority", "cnieNumber" };
				csvDtoData.add(header);
			}
			RegDataCSVDto csvDto = new RegDataCSVDto();

			String fullName = testDataUtil.generateFullName();
			csvDto.setFullName(fullName);

			DOBDto dobDto = testDataUtil.generateDOB();
			String dateOfBirth = dobDto.getDate();
			csvDto.setDateOfBirth(dateOfBirth);
			Integer age = dobDto.getAge();
			csvDto.setAge(age.toString());

			String gender = testDataUtil.genrateGenderEng();
			csvDto.setGender(gender);
			String residenceStatus = testDataUtil.generateResidenceStatusEng();
			csvDto.setResidenceStatus(residenceStatus);
			String addressLine1 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine1(addressLine1);
			String addressLine2 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine2(addressLine2);
			String addressLine3 = testDataUtil.generateAddressLine();
			csvDto.setAddressLine3(addressLine3);

			String countryCode = testDataUtil.getCountryCode();
			Location region = testDataUtil.getLocation(countryCode, 1);
			String regionName = region.getName();
			csvDto.setRegion(regionName);

			Location province = testDataUtil.getLocation(region.getCode(), 2);
			String provinceName = province.getName();
			csvDto.setProvince(provinceName);

			Location city = testDataUtil.getLocation(province.getCode(), 3);
			String cityName = city.getName();
			csvDto.setCity(cityName);

			Location localAdministrativeAuthority = testDataUtil.getLocation(city.getCode(), 4);
			String localAdministrativeAuthName = localAdministrativeAuthority.getName();
			csvDto.setLocalAdministrativeAuthority(localAdministrativeAuthName);

			Location postalCode = testDataUtil.getLocation(localAdministrativeAuthority.getCode(), 5);
			String postalCodeStr = postalCode.getName();
			csvDto.setPostalCode(postalCodeStr);
			String phone = testDataUtil.generatePhoneNumber().toString();
			csvDto.setPhone(phone);
			String email = testDataUtil.generateEmailAddress();
			csvDto.setEmail(email);
			String cnieNumber = testDataUtil.generateCnieNumber();
			csvDto.setCnieNumber(cnieNumber);
			String[] rowData = new String[] { fullName, dateOfBirth, age.toString(), gender, residenceStatus,
					addressLine1, addressLine2, addressLine3, regionName, provinceName, cityName, postalCodeStr, phone,
					email, localAdministrativeAuthName, cnieNumber };

			csvDtoList.add(csvDto);
			csvDtoData.add(rowData);
			if (csvDtoData.size() >= 2) {

				try {
					CSVUtil.writeObjectsToCsv(csvDtoData, csvPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
				csvDtoData.clear();
			}

			System.out.println("Iteration " + i + " over");
		}

//		String path = "E:\\MOSIP_PT\\Data\\reg_data_1.csv";
//		try {
//			CSVUtil.writeObjectListToCsv(csvDtoList, path);
//		} catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException | IOException e) {
//			e.printStackTrace();
//		}
	}

}
