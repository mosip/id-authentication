package io.mosip.registration.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.entity.DocumentEntity;
import io.mosip.registration.repositary.DocumentRepository;
import io.mosip.registration.repositary.RegistrationRepositary;
import io.mosip.registration.service.DocumentUploadService;

@Component
public class DocumentUploaderServiceImpl implements DocumentUploadService {

	@Autowired
	private DocumentEntity documentEntity;

	@Autowired
	@Qualifier("documentRepositoery")
	private DocumentRepository documentRepository;

	@Autowired
	@Qualifier("registrationRepository")
	private RegistrationRepositary registrationRepositary;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mosip.practice.fileUploader.serviceImpl.DocumentUploadService#uploadFile(
	 * org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	public Boolean uploadDoucment(MultipartFile file, DocumentDto documentDto) throws Exception {

		boolean saveFlag = false;

		if (documentDto.is_primary()) {

			documentEntity.setPreregId(documentDto.getPrereg_id());
			documentEntity.setDoc_name(file.getName());
			documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
			documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
			documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
			documentEntity.setDoc_store(file.getBytes());
			documentEntity.setStatus_code(documentDto.getStatus_code());
			documentEntity.setLang_code(documentDto.getLang_code());
			documentEntity.setCr_by(documentDto.getCr_by());
			documentEntity.setCr_dtimesz(documentDto.getCr_dtimesz());
			documentEntity.setUpd_by(documentDto.getUpd_by());
			documentEntity.setUpd_dtimesz(documentDto.getUpd_dtimesz());

			documentRepository.save(documentEntity);

			List<String> preIdList =null /*registrationRepositary.findByGroupIds(documentDto.getGroup_id())*/;

			for (int counter = 0; counter < preIdList.size(); counter++) {
				if (preIdList.get(counter).equals(documentDto.getPrereg_id())) {
					preIdList.remove(counter);
				}
			}

			if (preIdList.size() > 0) {
				for (int counter = 0; counter < preIdList.size(); counter++) {

					List<DocumentEntity> entity =null /*documentRepository.findBypreregId(preIdList.get(counter))*/;

					for (int ecount = 0; ecount < entity.size(); ecount++) {
						if (entity.get(ecount).getDoc_cat_code().equalsIgnoreCase(documentDto.getDoc_cat_code())) {
							entity.get(ecount).setDoc_store(file.getBytes());
						} else {
							entity.get(ecount).setPreregId(preIdList.get(counter));
							entity.get(ecount).setDoc_name(file.getName());
							entity.get(ecount).setDoc_cat_code(documentDto.getDoc_cat_code());
							entity.get(ecount).setDoc_typ_code(documentDto.getDoc_typ_code());
							entity.get(ecount).setDoc_file_format(documentDto.getDoc_file_format());
							entity.get(ecount).setDoc_store(file.getBytes());
							entity.get(ecount).setStatus_code(documentDto.getStatus_code());
							entity.get(ecount).setLang_code(documentDto.getLang_code());
							entity.get(ecount).setCr_by(documentDto.getCr_by());
							entity.get(ecount).setCr_dtimesz(documentDto.getCr_dtimesz());
							entity.get(ecount).setUpd_by(documentDto.getUpd_by());
							entity.get(ecount).setUpd_dtimesz(documentDto.getUpd_dtimesz());

							documentRepository.save(entity.get(ecount));
						}
					}

				}
			}

			saveFlag = true;

		}

		else if (!documentDto.is_primary()) {

			//List<String> preIdList = getRegistrationRepositary().findByGroupIds(documentDto.getGroup_id());
				
				documentEntity.setPreregId(documentDto.getPrereg_id());
				documentEntity.setDoc_name(file.getName());
				documentEntity.setDoc_cat_code(documentDto.getDoc_cat_code());
				documentEntity.setDoc_typ_code(documentDto.getDoc_typ_code());
				documentEntity.setDoc_file_format(documentDto.getDoc_file_format());
				documentEntity.setDoc_store(file.getBytes());
				documentEntity.setStatus_code(documentDto.getStatus_code());
				documentEntity.setLang_code(documentDto.getLang_code());
				documentEntity.setCr_by(documentDto.getCr_by());
				documentEntity.setCr_dtimesz(documentDto.getCr_dtimesz());
				documentEntity.setUpd_by(documentDto.getUpd_by());
				documentEntity.setUpd_dtimesz(documentDto.getUpd_dtimesz());
				
				documentRepository.save(documentEntity);
			
			

		}

		return saveFlag;

	}



}
