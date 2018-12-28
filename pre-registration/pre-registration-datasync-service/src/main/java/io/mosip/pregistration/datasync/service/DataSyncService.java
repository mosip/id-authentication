package io.mosip.pregistration.datasync.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.pregistration.datasync.dto.CreatePreRegistrationDTO;
import io.mosip.pregistration.datasync.dto.DataSyncRequestDTO;
import io.mosip.pregistration.datasync.dto.DataSyncResponseDTO;
import io.mosip.pregistration.datasync.dto.DocumentGetAllDto;
import io.mosip.pregistration.datasync.dto.ExceptionJSONInfoDTO;
import io.mosip.pregistration.datasync.dto.PreRegArchiveDTO;
import io.mosip.pregistration.datasync.dto.PreRegistrationIdsDTO;
import io.mosip.pregistration.datasync.dto.ResponseDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncDTO;
import io.mosip.pregistration.datasync.dto.ReverseDataSyncRequestDTO;
import io.mosip.pregistration.datasync.entity.InterfaceDataSyncTablePK;
import io.mosip.pregistration.datasync.entity.PreRegistrationProcessedEntity;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;
import io.mosip.pregistration.datasync.errorcodes.ErrorMessages;
import io.mosip.pregistration.datasync.exception.DataSyncRecordNotFoundException;
import io.mosip.pregistration.datasync.exception.DemographicGetDetailsException;
import io.mosip.pregistration.datasync.exception.RecordNotFoundForDateRange;
import io.mosip.pregistration.datasync.exception.ReverseDataFailedToStoreException;
import io.mosip.pregistration.datasync.exception.ZipFileCreationException;
import io.mosip.pregistration.datasync.repository.DataSyncRepository;
import io.mosip.pregistration.datasync.repository.ReverseDataSyncRepo;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;

/**
 * DataSync Service
 * 
 * @version 1.0.0
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@Service
public class DataSyncService {

	@Autowired
	@Qualifier("dataSyncRepository")
	private DataSyncRepository dataSyncRepository;

	@Autowired
	private ReverseDataSyncRepo reversedataSyncRepo;

	List<ExceptionJSONInfoDTO> errlist = new ArrayList<>();
	ExceptionJSONInfoDTO exceptionJSONInfo = new ExceptionJSONInfoDTO("", "");
	String status = "true";

	Timestamp resTime = new Timestamp(System.currentTimeMillis());

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Value("${preRegResourceUrl}")
	private String preRegResourceUrl;

	@Value("${docRegResourceUrl}")
	private String docRegResourceUrl;

	/**
	 * @param preId
	 * @return Zipped File
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataSyncResponseDTO<PreRegArchiveDTO> getPreRegistration(String preId) throws Exception {
		DataSyncResponseDTO responseDto = new DataSyncResponseDTO<>();
		PreRegArchiveDTO preRegArchiveDTO = new PreRegArchiveDTO();
		CreatePreRegistrationDTO preRegistrationDTO = callGetPreRegInfoRestService(preId);
		if (preRegistrationDTO != null) {
			System.out.println("Pre id: " + preRegistrationDTO.getPrId());
			List<DocumentGetAllDto> documentlist = callGetDocRestService(preId);
			for (DocumentGetAllDto doc : documentlist) {
				System.out.println("doc: " + doc.getPrereg_id());
			}
			byte[] bytes = DataSyncService.archivingFiles(preRegistrationDTO, documentlist);
			preRegArchiveDTO.setZipBytes(bytes);
			preRegArchiveDTO.setFileName(
					preRegistrationDTO.getPrId().toString() + "_" + preRegistrationDTO.getCreateDateTime().toString());
		} else {
			throw new DataSyncRecordNotFoundException(
					ErrorMessages.RECORDS_NOT_FOUND_FOR_REQUESTED_PREREGID.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);
		responseDto.setResponse(preRegArchiveDTO);
		return responseDto;
	}

	/**
	 * @param preRegistrationEntity
	 * @param documentEntityList
	 * @return zipped file's byte array
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static byte[] archivingFiles(CreatePreRegistrationDTO preRegistrationEntity,
			List<DocumentGetAllDto> documentEntityList) throws IOException {
		FileOutputStream fileOutputStream = null;
		FileOutputStream demofileOutputStream = null;
		File jsonFile = null;
		File fileDoc = null;
		List<String> inputMultiFileList = new ArrayList<>();
		Path pathDoc = null;
		byte[] inputStream = null;
		byte[] returnInputStream = null;
		JSONObject demographicJsonObject = null;
		if (preRegistrationEntity != null) {
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put("Pre-registration Id", preRegistrationEntity.getPrId());
				responseJson.put("Appointment Date", preRegistrationEntity.getCreateDateTime().toString());

				demographicJsonObject = preRegistrationEntity.getDemographicDetails();
				JSONObject demoObj = demographicJsonObject;
				pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
						+ preRegistrationEntity.getPrId().toString() + "_Demographic" + ".json");

				responseJson.put("demographic-details", demoObj);

				jsonFile = new File(pathDoc.toString());
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				jsonFile.createNewFile();

				demofileOutputStream = new FileOutputStream(jsonFile);
				demofileOutputStream.write(responseJson.toJSONString().getBytes());
				demofileOutputStream.close();

				System.out.println("Demography path: " + jsonFile.getAbsolutePath());
				inputMultiFileList.add(jsonFile.getAbsolutePath());
				if (documentEntityList != null && documentEntityList.size() > 0) {
					for (int i = 0; i < documentEntityList.size(); i++) {
						pathDoc = Paths.get(System.getProperty("java.io.tmpdir") + File.separator
								+ documentEntityList.get(i).getPrereg_id().toString() + "_"
								+ documentEntityList.get(i).getDoc_cat_code().toString() + "_"
								+ documentEntityList.get(i).getDoc_name());

						fileDoc = new File(pathDoc.toString());
						byte[] docBytes = documentEntityList.get(i).getMultipartFile();
						if (fileDoc.exists()) {
							fileDoc.delete();
						}
						fileDoc.createNewFile();
						fileOutputStream = new FileOutputStream(fileDoc);
						fileOutputStream.write(docBytes);
						fileOutputStream.close();
						System.out.println("FileDoc path: " + fileDoc.getAbsolutePath());
						inputMultiFileList.add(fileDoc.getAbsolutePath());

					}

				}
				inputStream = getCompressed(inputMultiFileList);
				returnInputStream = inputStream;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ZipFileCreationException(ErrorMessages.FAILED_TO_CREATE_A_ZIP_FILE.toString());
			} finally {
				inputStream = new byte[1024];
				if (inputMultiFileList != null && !inputMultiFileList.equals(null) && inputMultiFileList.size() > 0) {
					for (String s : inputMultiFileList) {
						Path path = Paths.get(s);
						Files.deleteIfExists(path);
					}
				}
				inputMultiFileList.clear();
				;
			}
		}
		return returnInputStream;
	}

	/**
	 * @param zipOut
	 * @param fis
	 * @throws IOException
	 */
	private static void readFile(ZipOutputStream zipOut, FileInputStream fis) throws IOException {
		final byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}

	}

	/**
	 * @param inputFIle
	 * @return compressed Zip
	 * @throws IOException
	 */
	public static byte[] getCompressed(List<String> inputFIle) throws IOException {
		File fileToZip = null;
		List<String> srcFiles = new ArrayList<>();
		srcFiles.addAll(inputFIle);
		FileInputStream fileInputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		ZipOutputStream zipOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		byteArrayOutputStream = new ByteArrayOutputStream();
		zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

		for (String srcFile : srcFiles) {
			fileToZip = new File(srcFile);
			fileInputStream = new FileInputStream(fileToZip);
			bufferedInputStream = new BufferedInputStream(fileInputStream, 1024);
			ZipEntry entry = new ZipEntry(fileToZip.getName());
			zipOutputStream.putNextEntry(entry);
			readFile(zipOutputStream, fileInputStream);

			bufferedInputStream.close();
		}
		byteArrayOutputStream.close();

		zipOutputStream.close();
		fileInputStream.close();

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * @param reverseDto
	 * @return responseDTO
	 */

	public DataSyncResponseDTO<String> storeConsumedPreRegistrations(ReverseDataSyncDTO reverseDto) {
		DataSyncResponseDTO<String> responseDto = new DataSyncResponseDTO<>();
		List<ReverseDataSyncEntity> entityList = new ArrayList<>();
		List<PreRegistrationProcessedEntity> processedEntityList = new ArrayList<>();
		ReverseDataSyncEntity reverseEntity = new ReverseDataSyncEntity();
		PreRegistrationProcessedEntity processedEntity = new PreRegistrationProcessedEntity();

		ReverseDataSyncRequestDTO reverseRequestDTO = reverseDto.getRequest();
		List<String> preIdList = reverseRequestDTO.getPre_registration_ids();
		if (preIdList != null && preIdList.size() > 0 && !preIdList.equals(null)) {
			for (int i = 0; i < preIdList.size(); i++) {
				reverseEntity = new ReverseDataSyncEntity();
				InterfaceDataSyncTablePK ipprlstPK = new InterfaceDataSyncTablePK();
				ipprlstPK.setPreregId(preIdList.get(i));
				ipprlstPK.setReceivedDtimes(new Timestamp(reverseDto.getReqTime().getTime()));
				reverseEntity.setIpprlst_PK(ipprlstPK);
				reverseEntity.setLangCode("AR");
				reverseEntity.setCrBy("5766477466");
				reverseEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
				entityList.add(reverseEntity);

				processedEntity = new PreRegistrationProcessedEntity();
				processedEntity.setPreRegistrationId(preIdList.get(i));
				processedEntity.setReceivedDTime(new Timestamp(reverseDto.getReqTime().getTime()));
				processedEntity.setStatusCode("Processed");
				processedEntity.setStatusComments("Processed by registration processor");
				processedEntity.setLangCode("AR");
				processedEntity.setCrBy("5766477466");
				processedEntity.setCrDate(new Timestamp(System.currentTimeMillis()));
				processedEntityList.add(processedEntity);
			}
		}

		List<ReverseDataSyncEntity> savedList = dataSyncRepository.saveAll(entityList);

		if (savedList != null && !savedList.equals(null) && savedList.size() > 0) {
			for (PreRegistrationProcessedEntity s : processedEntityList) {
				if (!reversedataSyncRepo.existsById(s.getPreRegistrationId()))
					reversedataSyncRepo.save(s);
			}

			status = "true";
			responseDto.setResponse(ErrorMessages.PRE_REGISTRATION_IDS_STORED_SUCESSFULLY.toString());

		} else {
			throw new ReverseDataFailedToStoreException(ErrorMessages.FAILED_TO_STORE_PRE_REGISTRATION_IDS.toString());
		}

		responseDto.setStatus(status);
		responseDto.setResTime(resTime);
		responseDto.setErr(errlist);

		return responseDto;

	}

	public DataSyncResponseDTO<PreRegistrationIdsDTO> retrieveAllPreRegid(DataSyncRequestDTO dataSyncRequestDTO)
			throws ParseException {
		String fromDate = dataSyncRequestDTO.getFromDate();
		String toDate = dataSyncRequestDTO.getToDate();

		PreRegistrationIdsDTO preRegistrationIdsDTO = new PreRegistrationIdsDTO();
		DataSyncResponseDTO<PreRegistrationIdsDTO> responseDto = new DataSyncResponseDTO<>();
		List<String> preRegIdEntitylist;
		List<ExceptionJSONInfoDTO> err = new ArrayList<>();

		try {
			preRegIdEntitylist = callGetPreIdsRestService(fromDate, toDate);
			if (preRegIdEntitylist == null || preRegIdEntitylist.size() == 0) {
				throw new RecordNotFoundForDateRange(ErrorMessages.RECORDS_NOT_FOUND_FOR_DATE_RANGE.toString());
			} else {

				List<String> preregIds = new ArrayList<>();
				for (String preRegistrationEntity : preRegIdEntitylist) {
					preregIds.add(preRegistrationEntity);
				}
				preRegistrationIdsDTO.setPreRegistrationIds(preregIds);
				preRegistrationIdsDTO.setTransactionId(UUIDGeneratorUtil.generateId());
				responseDto.setStatus("True");
				responseDto.setErr(err);
				responseDto.setResTime(new Timestamp(System.currentTimeMillis()));
				responseDto.setResponse(preRegistrationIdsDTO);
			}
		} catch (DataAccessLayerException e) {

			throw new TablenotAccessibleException(ErrorMessages.REGISTRATION_TABLE_NOT_ACCESSIBLE.toString());

		}

		return responseDto;
	}

	@SuppressWarnings({ "rawtypes" })
	public CreatePreRegistrationDTO callGetPreRegInfoRestService(String preId) {

		CreatePreRegistrationDTO responsestatusDto = null;
		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preRegResourceUrl + "/applicationData")
					.queryParam("preRegId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			System.out.println("respEntity: " + respEntity);
			ObjectMapper mapper = new ObjectMapper();
			responsestatusDto = mapper.convertValue(respEntity.getBody().getResponse().get(0),
					CreatePreRegistrationDTO.class);
		} catch (RestClientException e) {
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return responsestatusDto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> callGetPreIdsRestService(String fromDate, String toDate) {

		List<String> responseList = null;
		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromHttpUrl(preRegResourceUrl + "/applicationDataByDateTime").queryParam("fromDate", fromDate)
					.queryParam("toDate", toDate);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			responseList = mapper.convertValue(respEntity.getBody().getResponse(), List.class);
		} catch (RestClientException e) {
			e.printStackTrace();
			throw new DemographicGetDetailsException(ErrorCodes.PRG_DATA_SYNC_007.toString(),
					ErrorMessages.DEMOGRAPHIC_GET_RECORD_FAILED.toString(), e.getCause());
		}
		return responseList;
	}

	@SuppressWarnings({ "rawtypes" })
	public List<DocumentGetAllDto> callGetDocRestService(String preId) {

		List<DocumentGetAllDto> responsestatusDto = new ArrayList<>();
		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(docRegResourceUrl + "/getDocument")
					.queryParam("preId", preId);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<ResponseDTO> httpEntity = new HttpEntity<>(headers);
			String uriBuilder = builder.build().encode().toUriString();
			System.out.println("uriBuilder: " + uriBuilder);
			ResponseEntity<ResponseDTO> respEntity = restTemplate.exchange(uriBuilder, HttpMethod.GET, httpEntity,
					ResponseDTO.class);
			ObjectMapper mapper = new ObjectMapper();
			for (Object obj : respEntity.getBody().getResponse()) {
				responsestatusDto.add(mapper.convertValue(obj, DocumentGetAllDto.class));
			}
		} catch (RestClientException e) {
			return responsestatusDto;
		}
		return responsestatusDto;
	}

}
