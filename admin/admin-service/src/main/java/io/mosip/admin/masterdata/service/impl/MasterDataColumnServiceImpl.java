package io.mosip.admin.masterdata.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.configvalidator.exception.ConfigValidationException;
import io.mosip.admin.configvalidator.exception.PropertyNotFoundException;
import io.mosip.admin.masterdata.constant.MasterDataColumnErrorCode;
import io.mosip.admin.masterdata.dto.MasterDataColumnDto;
import io.mosip.admin.masterdata.service.MasterDataColumnService;
import io.mosip.kernel.core.http.ResponseWrapper;


/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Service
public class MasterDataColumnServiceImpl implements MasterDataColumnService {

	@Value("${mosip.admin.master.resource}")
	private String masterColumns;
	
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public ResponseWrapper<MasterDataColumnDto> getMasterDataColumns(String resource) {
		String propertiesFile = null;
		//Map<String,String> columns = null;
		List<String> buttons = null;
		List<String> actions = null;
		List<String> page = null;
		List<Map<Object, Object>> propList=new ArrayList<>();
		MasterDataColumnDto masterdataColumn = new MasterDataColumnDto();
		try {
			propertiesFile = restTemplate.getForObject(masterColumns, String.class);
		} catch (RestClientException e) {
			throw new PropertyNotFoundException(MasterDataColumnErrorCode.PROPERTY_NOT_FOUND.errorCode(),
					MasterDataColumnErrorCode.PROPERTY_NOT_FOUND.errorMessage());
		}
		
		Properties prop = new Properties();
		try {
			prop.load(new StringReader(propertiesFile));
		} catch (IOException e) {
			throw new ConfigValidationException(MasterDataColumnErrorCode.CONFIG_FILE_NOT_FOUND.errorCode(),
					MasterDataColumnErrorCode.CONFIG_FILE_NOT_FOUND.errorMessage());
		}
		
		List<String> tableFields = Arrays.asList(prop.getProperty("mosip.admin.resource.table."+resource).split("\\s*,\\s*"));
		List<String> jsonName = Arrays.asList(prop.getProperty("mosip.admin.resource."+resource).split("\\s*,\\s*"));
		
		String buttonsArray = prop.getProperty("mosip.admin.resource.buttons");
		buttons = Arrays.asList(buttonsArray.split("\\s*,\\s*"));
		String actionValues = prop.getProperty("mosip.admin.resource.actions");
		actions = Arrays.asList(actionValues.split("\\s*,\\s*"));
		String pagination = prop.getProperty("mosip.admin.resource.pages");
		page = Arrays.asList(pagination.split("\\s*,\\s*"));
		
		for(int i=0;i<tableFields.size();i++) {
			Map<Object,Object> tablefields = new HashMap<>();
			tablefields.put(tableFields.get(i), jsonName.get(i));
			propList.add(tablefields);
		}
		
		masterdataColumn.setTableFields(propList);
		masterdataColumn.setActionValues(actions);
		masterdataColumn.setButtons(buttons);
		masterdataColumn.setPageOptions(page);
		
		ResponseWrapper<MasterDataColumnDto> responseObject = new ResponseWrapper<>();
		responseObject.setResponse(masterdataColumn);
		return responseObject;
	}

}
