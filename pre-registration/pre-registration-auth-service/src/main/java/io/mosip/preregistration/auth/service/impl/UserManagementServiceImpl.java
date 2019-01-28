package io.mosip.preregistration.auth.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.auth.constants.StatusCodes;
import io.mosip.preregistration.auth.dao.UserManagementRepository;
import io.mosip.preregistration.auth.dto.UserDto;
import io.mosip.preregistration.auth.entity.UserManagmentEntity;
import io.mosip.preregistration.auth.exceptions.DatabaseOperationException;
import io.mosip.preregistration.auth.exceptions.UserAlreadyExistsException;
import io.mosip.preregistration.auth.exceptions.UserNameNotValidException;
import io.mosip.preregistration.auth.service.UserManagementService;
import io.mosip.preregistration.auth.util.ValidationUtil;

@Service
@Configuration
public class UserManagementServiceImpl implements UserManagementService {

	@Autowired
	private UserManagementRepository userManagementRepository;

	private RestTemplate restTemplate;

	//private MosipFileAppender mosipFileAppender = new MosipFileAppender();

	/*@Value("${resource.url}")
	String resourceUrl;
*/
	/*@SuppressWarnings("unused")
	private static MosipLogger LOGGER;

	public MosipLogger getLogger() {
		mosipFileAppender.setAppenderName("userManagementServiceImpl");
		mosipFileAppender.setFileName("UserManagementService.txt");
		return MosipLogfactory.getMosipDefaultFileLogger(mosipFileAppender, UserManagementServiceImpl.class);
	}
*/
	@Autowired
	public void restTemplateBeanBuilder(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	@Override
	public Map<String, StatusCodes> userUpdation(String userName, UserDto userDto) throws UserNameNotValidException {

		Map<String, StatusCodes> responseMap = new HashMap<>();

		UserManagmentEntity userManagmentEntity = new UserManagmentEntity();

		if (userDto == null) {
			UserManagmentEntity userManagmentEntity1 = userManagementRepository.getUserByUserName(userName);
			if (userManagmentEntity1 == null) {
				userManagmentEntity.setUserName(userName);
				if (ValidationUtil.emailValidator(userName)) {
					userManagmentEntity.setType("email");
				} else if (ValidationUtil.phoneValidator(userName)) {
					userManagmentEntity.setType("phone");
				} else {
					throw new UserNameNotValidException(StatusCodes.USER_ID_INVALID.toString());
				}
				try {
					userManagementRepository.save(userManagmentEntity);
					responseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
				} catch (Exception e) {
					throw new DatabaseOperationException(StatusCodes.USER_INSERTION_FAILED.toString());
				}
			} else {
				responseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
			}
		} else {
			userManagmentEntity = userManagementRepository.getUserByUserName(userName);
			UserManagmentEntity userManagmentEntityNew = userManagementRepository
					.getUserByUserName(userDto.getUserName());

			if (userManagmentEntity != null && userManagmentEntityNew == null) {
				userManagmentEntity.setUserName(userDto.getUserName());
				if (ValidationUtil.emailValidator(userDto.getUserName())) {
					userManagmentEntity.setType("email");
				} else if (ValidationUtil.phoneValidator(userDto.getUserName())) {
					userManagmentEntity.setType("phone");
				} else {
					throw new UserNameNotValidException(StatusCodes.USER_ID_INVALID.toString());
				}
				try {
					userManagementRepository.save(userManagmentEntity);
					responseMap.put("ok", StatusCodes.USER_UPDATED);
				} catch (Exception e) {
					responseMap.put("error", StatusCodes.USER_UPDATION_FAILED);
				}
			} else {
				throw new UserAlreadyExistsException(StatusCodes.USER_ALREADY_EXIST.toString());
			}
		}
		return responseMap;
	}

	@Override
	public Map<String, StatusCodes> userLogin(String userName) {
		//LOGGER = getLogger();
		// LOGGER.info(applicationId, moduleId, componentId, idType, id, description);

		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("ok", StatusCodes.USER_OTP_GENERATED);
		// Map<String, String> map = new HashMap<String, String>();
		// map.put("key", userName);
		//
		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		//
		// final HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String,
		// String>>(map, headers);
		//
		// try {
		// ResponseEntity<String> responseEntity = restTemplate.exchange(resourceUrl +
		// "otps", HttpMethod.POST, entity,
		// String.class);
		// if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
		// responseMap.put("ok", StatusCodes.USER_OTP_GENERATED);
		// } else {
		// responseMap.put("error", StatusCodes.USER_OTP_GENERATION_FAILED);
		// }
		// } catch (Exception exception) {
		// responseMap.put("error", StatusCodes.SERVER_CONNECTION_FAILED);
		// }
		return responseMap;
	}

	@Override
	public Map<String, StatusCodes> userValidation(String userName, String otp) {

		Map<String, StatusCodes> responseMap = new HashMap<>();
		// responseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
		// UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(resourceUrl +
		// "otps")
		// .queryParam("key", userName).queryParam("userInput", otp);
		//
		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity<String> entity = new HttpEntity<String>(headers);
		// try {
		// ResponseEntity<String> responseEntity =
		// restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
		// String.class, "");
		// if (responseEntity.getStatusCode() == HttpStatus.OK) {
		// responseMap.putAll(userUpdation(userName, null));
		// } else {
		// responseMap.put("error", StatusCodes.OTP_VALIDATION_FAILED);
		// }
		// } catch (Exception e) {
		// responseMap.put("error", StatusCodes.SERVER_CONNECTION_FAILED);
		// }
		responseMap.putAll(userUpdation(userName, null));
		return responseMap;
	}

}
