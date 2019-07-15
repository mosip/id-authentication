package io.mosip.admin.masterdata.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataColumnDto {
	
	List<String> buttons;
	List<Map<Object,Object>> tableFields;
	List<String> pageOptions;
	List<String> actionValues;

}
