package io.mosip.preregistration.documents.test.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.documents.dto.DocumentDto;
import io.mosip.preregistration.documents.entity.DocumentEntity;
import io.mosip.preregistration.documents.repository.DocumentRepository;
import io.mosip.preregistration.documents.service.DocumentUploadService;
import io.mosip.preregistration.documents.service.impl.DocumentUploaderServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentUploadServiceTest {

	@InjectMocks
	private DocumentUploadService documentUploaderServiceImpl = new DocumentUploaderServiceImpl() {
		@Override
		public String getFileExtension() {
			// Document extension PDF
			return "PDF";
		}
		@Override
		public long getMaxFileSize() {
			// Max document size 5 Mb
			return 5*1024*1024;
		}
	};

	@Mock
	private DocumentRepository documentRepository;


	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private MockMultipartFile mockMultipartFile;

	DocumentDto documentDto = new DocumentDto("89076543215674",
			"address",
			"POA",
			"PDF",
			"Draft",
			"ENG",
			"Kishan",
			"Kishan");
	
	private DocumentEntity entity;
	
	private Map<String, String> map=new HashMap<String,String>();

	@Before
	public void setUp() {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("sample.pdf").getFile());
		byte[] bFile = null;
		try {
			bFile = Files.readAllBytes(file.toPath());
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		

		try {
			mockMultipartFile = new MockMultipartFile("sample.pdf", "sample.pdf", "mixed/multipart",
					new FileInputStream(file));
			entity = new DocumentEntity(1, "89076543215674", "sample.pdf", "address", "POA", "PDF", mockMultipartFile.getBytes(), "Draft", "ENG",
					"Kishan", new Timestamp(System.currentTimeMillis()), "Kishan",
					new Timestamp(System.currentTimeMillis()));
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		map.put("DocumentId", "1");
		map.put("Status", "Draft");

	}

	@Test
	public void uploadDocument() {
		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		list.add(entity);
		List<String> list2 = new ArrayList<String>();
		list2.add("98745632155997");
		list2.add("78996741258596");
		Mockito.when(documentRepository.save(Mockito.any())).thenReturn(entity);
		//Mockito.when(registrationRepositary.findBygroupIds(ArgumentMatchers.any())).thenReturn(list2);
		//Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).thenReturn(list);
		Map<String, String> success = documentUploaderServiceImpl.uploadDoucment(mockMultipartFile, documentDto);

		assertEquals(map, success);
	}

	/*@Test
	public void documentCopy() {
		DocumentEntity documentEntity = entity;
		List<DocumentEntity> list = new ArrayList<DocumentEntity>();
		list.add(entity);
		List<String> list2 = new ArrayList<String>();
		list2.add("98745632155997");
		Mockito.when(documentRepository.save(entity)).thenReturn(documentEntity);
		Mockito.when(registrationRepositary.findBygroupIds(ArgumentMatchers.any())).thenReturn(list2);
		Mockito.when(documentRepository.findBypreregId(ArgumentMatchers.any())).thenReturn(list);
		Map<String, String> success = documentUploaderServiceImpl.uploadDoucment(mockMultipartFile, documentDto);

		assertEquals(map, success);

	}*/
}
