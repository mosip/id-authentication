package io.mosip.preregistration.application.test.service;

import static org.junit.Assert.assertEquals;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.spi.idgenerator.PridGenerator;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;
import io.mosip.kernel.dataaccess.hibernate.exception.DataAccessLayerException;
import io.mosip.preregistration.application.dao.RegistrationDao;
import io.mosip.preregistration.application.dto.AddressDto;
import io.mosip.preregistration.application.dto.ContactDto;
import io.mosip.preregistration.application.dto.NameDto;
import io.mosip.preregistration.application.dto.RegistrationDto;
import io.mosip.preregistration.application.dto.ResponseDto;
import io.mosip.preregistration.application.dto.ViewRegistrationResponseDto;
import io.mosip.preregistration.application.entity.RegistrationEntity;
import io.mosip.preregistration.application.exception.OperationNotAllowedException;
import io.mosip.preregistration.application.exception.utils.RegistrationErrorMessages;
import io.mosip.preregistration.application.repository.DocumentRepository;
import io.mosip.preregistration.application.repository.RegistrationRepository;
import io.mosip.preregistration.application.service.RegistrationService;
import io.mosip.preregistration.application.service.impl.RegistrationServiceImpl;
import io.mosip.preregistration.core.exceptions.TablenotAccessibleException;

/**
 * Test class to test the ViewRegistrationService
 * 
 * @author M1037462 M1037717 since 1.0.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest
//@SpringBootConfiguration
public class PreRegistrationServiceTest {

	@Mock
	private RegistrationRepository registrationRepository;

	@Mock
	private DocumentRepository documentRepository;
	
	@Mock
	private RegistrationDao registrationDao;

	@Mock
	private PridGenerator<String> pridGenerator;

	private RegistrationDto regDto = new RegistrationDto();
	private NameDto nameDto = new NameDto();
	private ContactDto contactDto = new ContactDto();
	private AddressDto addrDto = new AddressDto();

	@InjectMocks
	private RegistrationService<String, RegistrationDto> registrationService = new RegistrationServiceImpl();

	List<RegistrationEntity> userDetails = new ArrayList<RegistrationEntity>();
	List<ViewRegistrationResponseDto> response = new ArrayList<ViewRegistrationResponseDto>();
	private ViewRegistrationResponseDto responseDto;
	private RegistrationEntity registrationEntity;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before
	public void setup() throws ParseException {
		nameDto.setFirstname("Rajath");
		nameDto.setFullname("Rajath Kumar");

		contactDto.setEmail("rajath.kr1249@gmail.com");
		contactDto.setMobile("9480548558");

		addrDto.setAddrLine1("global");
		addrDto.setAddrLine2("Village");
		addrDto.setLocationCode("1234");

		regDto.setAddress(addrDto);
		regDto.setContact(contactDto);
		regDto.setName(nameDto);

		regDto.setAge(30);
		regDto.setIsPrimary(false);
		regDto.setGroupId("");
		regDto.setPreRegistrationId("");

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

		logger.info("Entity "+registrationEntity);
		
		responseDto = new ViewRegistrationResponseDto();

		responseDto.setFirstname("rupika");
		responseDto.setGroup_id("1232");
		responseDto.setStatus_code("Draft");
		responseDto.setNoOfRecords(1);
		responseDto.setUpd_dtimesz(times.toString());
		response.add(responseDto);
	}

	@Test
	public void successSaveImplTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		Mockito.when(pridGenerator.generateId()).thenReturn("67547447647457");
		ResponseDto res = registrationService.addRegistration(regDto,"125467364864");
		assertEquals(res.getPrId(), "67547447647457");
	}
	
	@Test
	public void successUpdateTest() throws Exception {
		logger.info("----------successful save of application in impl-------");
		regDto.setPreRegistrationId("67547447647457");
		regDto.setNationalid("Indian");
		ResponseDto res = registrationService.addRegistration(regDto,"125467364864");
		assertEquals(res.getPrId(), "67547447647457");
	}
	
	@Test(expected = TablenotAccessibleException.class)
	public void saveFailureCheck() throws Exception {
		DataAccessLayerException exception = new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE,RegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, null);

		Mockito.when(registrationDao.save(Mockito.any())).thenThrow(exception);
		registrationService.addRegistration(regDto,"125467364864");
	}

	@Test
	public void getApplicationDetails() {
		String userId = "9988905444";

		List<String> groupIds = new ArrayList<String>();
		groupIds.add("1232");
		Mockito.when(registrationRepository.noOfGroupIds(ArgumentMatchers.any())).thenReturn(groupIds);
		Mockito.when(registrationRepository.findBygroupId(ArgumentMatchers.any())).thenReturn(userDetails);

		List<ViewRegistrationResponseDto> actualRes = registrationService.getApplicationDetails(userId);

		assertEqualsList(actualRes, response);
		// assertThat(actualRes,is(response));

	}

	@Test
	public void getApplicationStatus() {
		String groupId = "1232";

		Mockito.when(registrationRepository.findBygroupId(ArgumentMatchers.any())).thenReturn(userDetails);
		Map<String, String> response = userDetails.stream()
				.collect(Collectors.toMap(RegistrationEntity::getPreRegistrationId, RegistrationEntity::getStatusCode));
		Map<String, String> actualRes = registrationService.getApplicationStatus(groupId);
		assertEquals(response, actualRes);

	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationDetailsTransactionFailureCheck() throws Exception {
		String userId = "9988905444";
		DataAccessLayerException exception = 
				new DataAccessLayerException(HibernateErrorCode.ERR_DATABASE,RegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE, null);

		Mockito.when(registrationRepository.noOfGroupIds(ArgumentMatchers.any())).thenThrow(exception);
		registrationService.getApplicationDetails(userId);
	}

	@Test(expected = TablenotAccessibleException.class)
	public void getApplicationStatusTransactionFailureCheck() throws Exception {
		String groupId = "1234";
		TablenotAccessibleException exception = new TablenotAccessibleException(
				RegistrationErrorMessages.REGISTRATION_TABLE_NOTACCESSIBLE);
		Mockito.when(registrationRepository.findBygroupId(ArgumentMatchers.any())).thenThrow(exception);
		registrationService.getApplicationStatus(groupId);
	}

	public void assertEqualsList(List<ViewRegistrationResponseDto> actual, List<ViewRegistrationResponseDto> expected) {
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).toString(), actual.get(i).toString());
		}
	}

	@Test
	public void deleteIndividualTest() {

		String groupId = "33";
		List<String> preregIds = Arrays.asList("1");

		RegistrationEntity applicant_Demographic = new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(false);
		applicant_Demographic.setStatusCode("Draft");

		Mockito.when(registrationRepository.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(applicant_Demographic);

		doNothing().when(registrationRepository).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(),
				applicant_Demographic.getPreRegistrationId());
		registrationService.deleteIndividual(groupId, preregIds);

	}

	@Test(expected = OperationNotAllowedException.class)
	public void deleteDraftTest() {

		String groupId = "33";
		List<String> preregIds = Arrays.asList("1");
		RegistrationEntity applicant_Demographic = new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(true);
		applicant_Demographic.setStatusCode("update");

		Mockito.when(registrationRepository.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(applicant_Demographic);
		doNothing().when(documentRepository).deleteAllByPreregId(applicant_Demographic.getPreRegistrationId());
		doNothing().when(registrationRepository).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(),
				applicant_Demographic.getPreRegistrationId());
		registrationService.deleteIndividual(groupId, preregIds);

	}

	@Test(expected = OperationNotAllowedException.class)
	public void deletePrimaryMemberTest() {

		String groupId = "33";
		List<String> preregIds = Arrays.asList("1");
		RegistrationEntity applicant_Demographic = new RegistrationEntity();
		applicant_Demographic.setGroupId("33");
		applicant_Demographic.setPreRegistrationId("1");
		applicant_Demographic.setIsPrimary(true);
		applicant_Demographic.setStatusCode("Draft");

		Mockito.when(registrationRepository.findByGroupIdAndPreRegistrationId(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(applicant_Demographic);

		doNothing().when(registrationRepository).deleteByGroupIdAndPreRegistrationId(applicant_Demographic.getGroupId(),
				applicant_Demographic.getPreRegistrationId());
		registrationService.deleteIndividual(groupId, preregIds);

	}

	@Test
	public void deleteGroupWithoutPrimaryTest() {
		String groupId = "33";

		RegistrationEntity applicant_Demographic1 = new RegistrationEntity();
		applicant_Demographic1.setGroupId("33");
		applicant_Demographic1.setPreRegistrationId("1");
		applicant_Demographic1.setIsPrimary(true);
		applicant_Demographic1.setStatusCode("Draft");

		RegistrationEntity applicant_Demographic2 = new RegistrationEntity();
		applicant_Demographic2.setGroupId("33");
		applicant_Demographic2.setPreRegistrationId("2");
		applicant_Demographic2.setIsPrimary(false);
		applicant_Demographic2.setStatusCode("Draft");

		RegistrationEntity applicant_Demographic3 = new RegistrationEntity();
		applicant_Demographic3.setGroupId("33");
		applicant_Demographic3.setPreRegistrationId("3");
		applicant_Demographic3.setIsPrimary(false);
		applicant_Demographic3.setStatusCode("Draft");

		List<RegistrationEntity> lists = new ArrayList<RegistrationEntity>();

		lists.add(applicant_Demographic1);
		lists.add(applicant_Demographic2);
		lists.add(applicant_Demographic3);

		List<RegistrationEntity> list = new ArrayList<RegistrationEntity>();
		list.add(applicant_Demographic1);

		Mockito.when(registrationRepository.findByGroupIdAndIsPrimary(groupId, true)).thenReturn(list);

		doNothing().when(registrationRepository).deleteAllBygroupId(groupId);
		registrationService.deleteGroup(groupId);
	}
	
	@Test
	public void deleteGroupTest() {
		String groupId = "33";

		RegistrationEntity applicant_Demographic1 = new RegistrationEntity();
		applicant_Demographic1.setGroupId("33");
		applicant_Demographic1.setPreRegistrationId("1");
		applicant_Demographic1.setIsPrimary(false);
		applicant_Demographic1.setStatusCode("Draft");

		RegistrationEntity applicant_Demographic2 = new RegistrationEntity();
		applicant_Demographic2.setGroupId("33");
		applicant_Demographic2.setPreRegistrationId("2");
		applicant_Demographic2.setIsPrimary(false);
		applicant_Demographic2.setStatusCode("Draft");

		RegistrationEntity applicant_Demographic3 = new RegistrationEntity();
		applicant_Demographic3.setGroupId("33");
		applicant_Demographic3.setPreRegistrationId("3");
		applicant_Demographic3.setIsPrimary(false);
		applicant_Demographic3.setStatusCode("Draft");

		List<RegistrationEntity> lists = new ArrayList<RegistrationEntity>();

		lists.add(applicant_Demographic1);
		lists.add(applicant_Demographic2);
		lists.add(applicant_Demographic3);

		List<RegistrationEntity> list = new ArrayList<RegistrationEntity>();
		list.add(applicant_Demographic1);
		
		Mockito.when(registrationRepository.findBygroupId(groupId)).thenReturn(list);
		doNothing().when(documentRepository).deleteAllByPreregId(Mockito.anyString());
		List<ResponseDto> res =registrationService.deleteGroup(groupId);
		assertEquals(res.get(0).getPrId(), "1");
	}

}
