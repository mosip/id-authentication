package io.mosip.kernel.masterdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.constant.TemplateTypeErrorCode;
import io.mosip.kernel.masterdata.dto.TemplateTypeDto;
import io.mosip.kernel.masterdata.entity.TemplateType;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.TemplateTypeRepository;
import io.mosip.kernel.masterdata.service.TemplateTypeService;
import io.mosip.kernel.masterdata.utils.ExceptionUtils;
import io.mosip.kernel.masterdata.utils.MetaDataUtils;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Service
public class TemplateTypeServiceImpl implements TemplateTypeService {

	@Autowired
	private MetaDataUtils metaUtils;

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	private TemplateTypeRepository templateTypeRepository;

	@Override
	public CodeAndLanguageCodeID createTemplateType(TemplateTypeDto tempalteType) {
		TemplateType entity = metaUtils.setCreateMetaData(tempalteType, TemplateType.class);
		TemplateType templateType;
		try {
			templateType = templateTypeRepository.create(entity);

		} catch (DataAccessLayerException e) {
			throw new MasterDataServiceException(TemplateTypeErrorCode.TEMPLATE_TYPE_INSERT_EXCEPTION.getErrorCode(),
					TemplateTypeErrorCode.TEMPLATE_TYPE_INSERT_EXCEPTION.getErrorMessage() + "  "
							+ ExceptionUtils.parseException(e));
		}

		CodeAndLanguageCodeID codeLangCodeId = new CodeAndLanguageCodeID();
		dataMapper.map(templateType, codeLangCodeId, true, null, null, true);

		return codeLangCodeId;
	}

}
