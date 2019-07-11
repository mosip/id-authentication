package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.masterdata.constant.ApplicantTypeErrorCode;
import io.mosip.kernel.masterdata.dto.ApplicantValidDocumentDto;
import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import io.mosip.kernel.masterdata.dto.getresponse.DocumentCategoryAndTypeResponseDto;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ApplicantValidDocumentRepository;
import io.mosip.kernel.masterdata.service.ApplicantValidDocumentService;

@Service
public class ApplicantValidDocumentServiceImpl implements ApplicantValidDocumentService {

	@Autowired
	private ApplicantValidDocumentRepository applicantValidDocumentRepository;

	@Override
	public ApplicantValidDocumentDto getDocumentCategoryAndTypes(String applicantTypeCode, List<String> languages) {

		ApplicantValidDocumentDto dto = new ApplicantValidDocumentDto();
		dto.setDocumentCategories(new ArrayList<>());// to avoid NPE
		try {
			List<Object[]> list = applicantValidDocumentRepository
					.getDocumentCategoryAndTypesForApplicantCode(applicantTypeCode, languages);

			if (EmptyCheckUtils.isNullEmpty(list)) {
				throw new DataNotFoundException(
						ApplicantTypeErrorCode.APPLICANT_TYPE_NOT_FOUND_EXCEPTION.getErrorCode(),
						ApplicantTypeErrorCode.APPLICANT_TYPE_NOT_FOUND_EXCEPTION.getErrorMessage());
			}

			boolean isActive = true;
			dto.setAppTypeCode(applicantTypeCode);
			dto.setIsActive(isActive);
			boolean isFirstTime = true;

			Map<DocumentCategoryAndTypeResponseDto, List<DocumentTypeDto>> map = new HashMap<>();

			for (Object[] obj : list) {
				DocumentCategoryAndTypeResponseDto dc = new DocumentCategoryAndTypeResponseDto();
				dc.setIsActive(isActive);
				dc.setLangCode((String) obj[4]);
				dc.setCode((String) obj[5]);
				dc.setName((String) obj[6]);
				dc.setDescription((String) obj[7]);

				DocumentTypeDto dt = new DocumentTypeDto();
				dt.setIsActive(isActive);
				dt.setLangCode((String) obj[0]);
				dt.setCode((String) obj[1]);
				dt.setName((String) obj[2]);
				dt.setDescription((String) obj[3]);

				if (map.containsKey(dc)) {
					map.get(dc).add(dt);
				} else {
					List<DocumentTypeDto> docTypeList = new ArrayList<>();
					docTypeList.add(dt);
					map.put(dc, docTypeList);
				}

				if (isFirstTime) {
					dto.setLangCode((String) obj[8]);
					isFirstTime = false;
				}
			}

			for (Entry<DocumentCategoryAndTypeResponseDto, List<DocumentTypeDto>> entry : map.entrySet()) {
				DocumentCategoryAndTypeResponseDto dcr = entry.getKey();
				dcr.setDocumentTypes(entry.getValue());
				dto.getDocumentCategories().add(dcr);
			}

		} catch (DataAccessLayerException | DataAccessException e) {
			throw new MasterDataServiceException(ApplicantTypeErrorCode.APPLICANT_TYPE_FETCH_EXCEPTION.getErrorCode(),
					ApplicantTypeErrorCode.APPLICANT_TYPE_FETCH_EXCEPTION.getErrorMessage());
		}

		return dto;
	}

}
