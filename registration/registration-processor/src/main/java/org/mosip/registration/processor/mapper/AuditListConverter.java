package org.mosip.registration.processor.mapper;

import java.util.LinkedList;
import java.util.List;

import org.mosip.registration.processor.dto.AuditDTO;
import org.mosip.registration.processor.dto.json.metadata.Audit;

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
			//audit.setEventId(auditDTO.getEventId());
			audit.setEndTimestamp(auditDTO.getEndTimestamp());
			audit.setStartTimestamp(auditDTO.getStartTimestamp());
			auditList.add(audit);
			
			
		});
		 return auditList;
		
	}

}
