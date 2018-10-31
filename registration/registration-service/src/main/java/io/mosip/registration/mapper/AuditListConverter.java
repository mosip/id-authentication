package io.mosip.registration.mapper;

import java.util.LinkedList;
import java.util.List;

import io.mosip.registration.dto.AuditDTO;
import io.mosip.registration.dto.json.metadata.Audit;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/** class is  audit List Converter
 * @author YASWANTH S
 *
 */
public class AuditListConverter extends CustomConverter<List<AuditDTO>, List<Audit>>{

	

	@Override
	public List<Audit> convert(List<AuditDTO> source, Type<? extends List<Audit>> destinationType) {
		LinkedList<Audit> auditList=new LinkedList<Audit>();
		source.forEach((auditDTO)->{
			
			Audit audit=new Audit();
			audit.setEventId(auditDTO.getEventId());
			auditList.add(audit);
			
			
		});
		 return auditList;
		
	}

}
