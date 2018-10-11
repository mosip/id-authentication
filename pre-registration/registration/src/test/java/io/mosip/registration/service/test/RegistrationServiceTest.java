package io.mosip.registration.service.test;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.core.exceptions.TablenotAccessibleException;
import io.mosip.registration.dto.ViewRegistrationResponseDto;
import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.exception.utils.RegistrationErrorCodes;
import io.mosip.registration.repositary.RegistrationRepositary;
import io.mosip.registration.service.RegistrationService;
import io.mosip.registration.service.impl.RegistrationServiceImpl;

/**
 * Test class to test the ViewRegistrationService
 * 
 * @author M1037462 
 * since 1.0.0
 */

@RunWith(SpringRunner.class)

@SpringBootTest
@SpringBootConfiguration

public class RegistrationServiceTest {

	@Mock
	private RegistrationRepositary registrationRepositary;

	@InjectMocks
	private RegistrationService<?,?> viewRegistrationService = new RegistrationServiceImpl();

	List<RegistrationEntity> userDetails = new ArrayList<RegistrationEntity>();
	List<ViewRegistrationResponseDto> response = new ArrayList<ViewRegistrationResponseDto>();
	private ViewRegistrationResponseDto responseDto;
	 private RegistrationEntity registrationEntity;

	@Before
	public void setup() throws ParseException {
		registrationEntity = new RegistrationEntity();

		registrationEntity.setIsPrimary(false);
		registrationEntity.setAddrLine1("aboahr");

		registrationEntity.setAge(20);
		registrationEntity.setApplicantType("adult");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("08/10/2018");
		long time = date.getTime();
		Timestamp times = new Timestamp(time);
		registrationEntity.setCreateDateTime(times);

		registrationEntity.setFirstname("rupika");

		registrationEntity.setGenderCode("f");

		registrationEntity.setGroupId("1232");
		registrationEntity.setStatusCode("Draft");
		registrationEntity.setUpdateDateTime(times);
		registrationEntity.setUserId("9988905444");
		userDetails.add(registrationEntity);

		responseDto = new ViewRegistrationResponseDto();

		responseDto.setFirstname("rupika");
		responseDto.setGroup_id("1232");
		responseDto.setStatus_code("Draft");
		responseDto.setNoOfRecords(1);
		responseDto.setUpd_dtimesz(times.toString());
		response.add(responseDto);
	}

	@Test
	public void getApplicationDetails() {
		String userId = "9988905444";

		List<String> groupIds = new ArrayList<String>();
		groupIds.add("1232");
		Mockito.when(registrationRepositary.noOfGroupIds(ArgumentMatchers.any())).thenReturn(groupIds);
		Mockito.when(registrationRepositary.findBygroupId(ArgumentMatchers.any())).thenReturn(userDetails);

		List<ViewRegistrationResponseDto> actualRes = viewRegistrationService.getApplicationDetails(userId);
		
		assertEqualsList(actualRes ,response);
		//assertThat(actualRes,is(response));
	
	

	}

	@Test
	public void getApplicationStatus() {
		String groupId = "1232";
		
		Mockito.when(registrationRepositary.findBygroupId(ArgumentMatchers.any())).thenReturn(userDetails);
		Map<String, String> response = userDetails.stream()
				.collect(Collectors.toMap(RegistrationEntity::getPreRegistrationId, RegistrationEntity::getStatusCode));
		Map<String, String> actualRes = viewRegistrationService.getApplicationStatus(groupId);
		assertEquals(response, actualRes);

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		TablenotAccessibleException exception = new TablenotAccessibleException(RegistrationErrorCodes.REGISTRATION_TABLE_NOTACCESSIBLE);
				
		Mockito.when(registrationRepositary.noOfGroupIds(ArgumentMatchers.any())).thenThrow(exception);
		viewRegistrationService.getApplicationDetails(userId);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationStatusTransactionFailureCheck() throws Exception {
		String groupId = "1234";
		TablenotAccessibleException exception = new TablenotAccessibleException(RegistrationErrorCodes.REGISTRATION_TABLE_NOTACCESSIBLE);
		Mockito.when(registrationRepositary.findBygroupId(ArgumentMatchers.any())).thenThrow(exception);
		viewRegistrationService.getApplicationStatus(groupId);
	}
	
	public void assertEqualsList(List<ViewRegistrationResponseDto> actual,List<ViewRegistrationResponseDto> expected) {
		for(int i=0;i<expected.size();i++) {
			assertEquals(expected.get(i).toString(), actual.get(i).toString());
		}
	}
}
