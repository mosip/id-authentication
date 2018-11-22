package io.mosip.kernel.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.exception.DataMapperException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.PostResponseDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatRequestDto;
import io.mosip.kernel.masterdata.entity.CodeAndLanguageCodeId;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TemplateFileFormatRepository;
import io.mosip.kernel.masterdata.service.TemplateFileFormatService;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

@Service
public class TemplateFileFormatServiceImpl implements TemplateFileFormatService {

	@Autowired
	private TemplateFileFormatRepository templateFileFormatRepository;

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	@Override
	public PostResponseDto addTemplateFileFormat(TemplateFileFormatRequestDto templateFileFormatRequestDto) {
		List<TemplateFileFormat> entities = metaUtils.setCreateMetaData(
				templateFileFormatRequestDto.getRequest().getTemplateFileFormatDtos(), TemplateFileFormat.class);
		List<TemplateFileFormat> templateFileFormats;
		try {
			templateFileFormats = templateFileFormatRepository.saveAll(entities);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getMessage());
		}
		List<CodeAndLanguageCodeId> codeLangCodeIds = new ArrayList<>();
		templateFileFormats.forEach(templateFileFormat -> {
			CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
			try {
				dataMapper.map(templateFileFormat, codeLangCodeId, true, null, null, true);
			} catch (DataMapperException e) {
				throw new MasterDataServiceException(
						DocumentCategoryErrorCode.DOCUMENT_CATEGORY_MAPPING_EXCEPTION.getErrorCode(), e.getMessage());
			}
			codeLangCodeIds.add(codeLangCodeId);
		});
		PostResponseDto postResponseDto = new PostResponseDto();
		postResponseDto.setResults(codeLangCodeIds);
		return postResponseDto;
	}

}
