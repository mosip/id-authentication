package io.mosip.registration.service.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import io.mosip.registration.entity.DocumentEntity;
import io.mosip.registration.entity.RegistrationEntity;
import io.mosip.registration.exception.OperationNotAllowedException;
import io.mosip.registration.exception.utils.RegistrationErrorCodes;
import io.mosip.registration.repositary.DocumentRepository;
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
	
	@Mock
	private DocumentRepository documentRepository;

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
	

	
	@Test
	public void deleteIndividualTest() {
		
		String groupId="33";
		List<String> preregIds=Arrays.asList("1");
		
		RegistrationEntity applicant_Demographic=new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(false);
		applicant_Demographic.setStatusCode("Draft");
		
		Mockito.when(registrationRepositary.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
			ArgumentMatchers.any())).thenReturn(applicant_Demographic);
		
		doNothing().when(registrationRepositary).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(), applicant_Demographic.getPreRegistrationId());
		viewRegistrationService.deleteIndividual(groupId, preregIds);
		
		
	}
	@Test(expected=OperationNotAllowedException.class)
	public void deleteDraftTest() {
		
		String groupId="33";
		List<String> preregIds=Arrays.asList("1");
		RegistrationEntity applicant_Demographic=new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(true);
		applicant_Demographic.setStatusCode("update");

		
		Mockito.when(registrationRepositary.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
			ArgumentMatchers.any())).thenReturn(applicant_Demographic);
		doNothing().when(documentRepository).deleteAllByPreregId(applicant_Demographic.getPreRegistrationId());
		doNothing().when(registrationRepositary).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(), applicant_Demographic.getPreRegistrationId());
		viewRegistrationService.deleteIndividual(groupId, preregIds);
		
		
	}
	
	
	@Test(expected=OperationNotAllowedException.class)
	public void deletePrimaryMemberTest() {
		
		String groupId="33";
		List<String> preregIds=Arrays.asList("1");
		RegistrationEntity applicant_Demographic=new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(true);
		applicant_Demographic.setStatusCode("Draft");

		
		Mockito.when(registrationRepositary.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
			ArgumentMatchers.any())).thenReturn(applicant_Demographic);
		
		doNothing().when(registrationRepositary).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(), applicant_Demographic.getPreRegistrationId());
		viewRegistrationService.deleteIndividual(groupId, preregIds);
		
		
	}
	
	@Test
	public void deleteGroupWithoutPrimaryTest() {
		String groupId="33";
		
		RegistrationEntity applicant_Demographic1=new RegistrationEntity();
		applicant_Demographic1.setGroupId("33");
		applicant_Demographic1.setPreRegistrationId("1");
		applicant_Demographic1.setIsPrimary(true);
		applicant_Demographic1.setStatusCode("Draft");
		
		RegistrationEntity applicant_Demographic2=new RegistrationEntity();
		applicant_Demographic2.setGroupId("33");
		applicant_Demographic2.setPreRegistrationId("2");
		applicant_Demographic2.setIsPrimary(false);
		applicant_Demographic2.setStatusCode("Draft");
		
		RegistrationEntity applicant_Demographic3=new RegistrationEntity();
		applicant_Demographic3.setGroupId("33");
		applicant_Demographic3.setPreRegistrationId("3");
		applicant_Demographic3.setIsPrimary(false);
		applicant_Demographic3.setStatusCode("Draft");
		
		List<RegistrationEntity> lists=new ArrayList<RegistrationEntity>();
		
		lists.add(applicant_Demographic1);
		lists.add(applicant_Demographic2);
		lists.add(applicant_Demographic3);
		
		List<RegistrationEntity> list= new ArrayList<RegistrationEntity>();
		list.add(applicant_Demographic1);
		
		
		Mockito.when(registrationRepositary.findByGroupIdAndIsPrimary(groupId,
				true)).thenReturn(list);
		
		doNothing().when(registrationRepositary).deleteAllBygroupId(groupId);
		viewRegistrationService.deleteGroup(groupId);
	}
	
}
