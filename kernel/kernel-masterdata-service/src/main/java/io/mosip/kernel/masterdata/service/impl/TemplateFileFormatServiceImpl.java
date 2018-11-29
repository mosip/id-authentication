package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.DocumentCategoryErrorCode;
import io.mosip.kernel.masterdata.dto.RequestDto;
import io.mosip.kernel.masterdata.dto.TemplateFileFormatData;
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
	public CodeAndLanguageCodeId addTemplateFileFormat(RequestDto<TemplateFileFormatData> templateFileFormatRequestDto) {
		TemplateFileFormat entity = metaUtils.setCreateMetaData(
				templateFileFormatRequestDto.getRequest().getTemplateFileFormat(), TemplateFileFormat.class);
		TemplateFileFormat templateFileFormat;
		try {
			templateFileFormat = templateFileFormatRepository.create(entity);
		} catch (DataAccessException e) {
			throw new MasterDataServiceException(
					DocumentCategoryErrorCode.DOCUMENT_CATEGORY_INSERT_EXCEPTION.getErrorCode(), e.getMessage());
		}
		CodeAndLanguageCodeId codeLangCodeId = new CodeAndLanguageCodeId();
		dataMapper.map(templateFileFormat, codeLangCodeId, true, null, null, true);
		return codeLangCodeId;
	}

}
