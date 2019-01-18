package io.mosip.registration.processor.stages.uigenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.ApplicantDocument;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.stages.uingenerator.dto.UinResponseDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.Documents;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdRequestDto;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.stages.uingenerator.util.TriggerNotificationForUIN;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;



@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class, HMACUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class UinGeneratorStageTest {
       
//       @InjectMocks
//       private UinGeneratorStage uinGeneratorStage;
       @InjectMocks
   	private UinGeneratorStage uinGeneratorStage = new UinGeneratorStage() {
   		@Override
   		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
   			vertx = Vertx.vertx();

   			return new MosipEventBus(vertx) {
   			};
   		}

   		@Override
   		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
   				MessageBusAddress toAddress) {
   		}
   	}; 
       
       /** The adapter. */
       @Mock
       private FileSystemAdapter<InputStream, Boolean> adapter;
       
       /** The input stream. */
       @Mock
       private InputStream inputStream;
       
       @Mock
       Object identity;
       
       /** The registration status service. */
       @Mock
       RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;
       
       /** The identity json. */
       @Mock
       JSONObject identityJson;
       
       @Mock
       private AuditLogRequestBuilder auditLogRequestBuilder;
       
       @Mock
       RegistrationProcessorRestClientService<Object> registrationProcessorRestClientService;
       
       @Mock
       private List<Documents> documents;
       
       @Mock
       JSONObject demographicIdentity;
       
       @Mock
       private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;
       
       @Mock
   	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;
       
       /** The registration status dto. */
       InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
       
       /** The id request DTO. */
       IdRequestDto idRequestDTO =  new IdRequestDto();
       
      
       IdResponseDTO idResponseDTO =  new IdResponseDTO();
       
       /** The trigger notification for UIN. */
   	@Mock
   	TriggerNotificationForUIN triggerNotificationForUIN;
       
       @Before
       public void setup() throws Exception{
             
             Field auditLog = AuditLogRequestBuilder.class.getDeclaredField("registrationProcessorRestService");
             auditLog.setAccessible(true);
             @SuppressWarnings("unchecked")
             RegistrationProcessorRestClientService<Object> mockObj = Mockito
                           .mock(RegistrationProcessorRestClientService.class);
             auditLog.set(auditLogRequestBuilder, mockObj);
             AuditResponseDto auditResponseDto = new AuditResponseDto();
       Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
                          "test case description", EventId.RPR_401.toString(), EventName.ADD.toString(),
                          EventType.BUSINESS.toString(), "1234testcase");
             
             
            
             Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
       Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
             


       }
       
       @Test
       public void testUinGenerationSuccesswithoutUIN() throws Exception {
             MessageDTO messageDTO = new MessageDTO();
             messageDTO.setRid("27847657360002520181210094052");
             String value="{\r\n" + 
                     "    \"identity\" : {\r\n" + 
                     "      \"IDSchemaVersion\" : 1.0,\r\n" + 
                     "      \"UIN\" : \"\",\r\n" + 
                     "      \"fullName\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"ابراهيم بن علي\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Ibrahim Ibn Ali\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"dateOfBirth\" : \"1955/04/15\",\r\n" + 
                     "      \"age\" : 45,\r\n" + 
                     "      \"gender\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"الذكر\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"mâle\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine1\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 1\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 1\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine2\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine3\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                     "      } ],\r\n" + 
                      "      \"region\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"طنجة - تطوان - الحسيمة\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Tanger-Tétouan-Al Hoceima\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"province\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"فاس-مكناس\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Fès-Meknès\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"city\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"الدار البيضاء\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Casablanca\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"postalCode\" : \"570004\",\r\n" + 
                     "      \"phone\" : \"9876543210\",\r\n" + 
                     "      \"email\" : \"abc@xyz.com\",\r\n" + 
                     "      \"CNIENumber\" : 6789545678909,\r\n" + 
                     "      \"localAdministrativeAuthority\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"سلمى\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"salma\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"parentOrGuardianRIDOrUIN\" : 212124324784912,\r\n" + 
                     "      \"parentOrGuardianName\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"سلمى\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"salma\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"proofOfAddress\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"drivingLicense\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfIdentity\" : {\r\n" + 
                     "        \"format\" : \"txt\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfRelationship\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfDateOfBirth\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"individualBiometrics\" : {\r\n" + 
                     "        \"format\" : \"cbeff\",\r\n" + 
                     "        \"version\" : 1.0,\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"parentOrGuardianBiometrics\" : {\r\n" + 
                     "        \"format\" : \"cbeff\",\r\n" + 
                     "        \"version\" : 1.0,\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      }\r\n" + 
                     "    },\r\n" + 
                     "    \"documents\" : [ {\r\n" + 
                     "      \"category\" : \"individualBiometrics\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"parentOrGuardianBiometrics\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"proofOfRelationship\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"proofOfDateOfBirth\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    } ]\r\n" + 
                     "  }";
             byte[] data = value.getBytes();
             PowerMockito.mockStatic(IOUtils.class);
             PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);
             UinResponseDto uinResponseDto = new UinResponseDto();
             uinResponseDto.setUin("493410317027");
       Mockito.when(registrationProcessorRestClientService.getApi(any(),any(),any(),any(),any())).thenReturn(uinResponseDto);

             
		String response = new String(
				"{\"id\":\"mosip.id.create\",\"version\":\"1.0\",\"timestamp\":\"2019-01-17T06:29:01.940Z\",\"status\":\"ACTIVATED\",\"response\":{\"entity\":\"https://dev.mosip.io/idrepo/v1.0/identity/203560486746\"}}");


       Mockito.when(registrationProcessorRestClientService.postApi(any(),any(),any(),any(),any(),any())).thenReturn(response);

       Mockito.when(identityJson.get(anyString())).thenReturn(demographicIdentity);
                          List<ApplicantDocument> applicantDocument = new ArrayList<>();
                          ApplicantDocument appDocument = new ApplicantDocument();
                          appDocument.setIsActive(true);
                          applicantDocument.add(appDocument);
       Mockito.when(packetInfoManager.getDocumentsByRegId(Matchers.anyString())).thenReturn(applicantDocument);
		doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		doNothing().when(demographicDedupeRepository).updateUinWrtRegistraionId(any(), any());
		doNothing().when(triggerNotificationForUIN).triggerNotification("test", false);

           MessageDTO result=  uinGeneratorStage.process(messageDTO);
           assertFalse(result.getInternalError());
            
       }
       
       
       
       
       
       
       
       @Test
       public void testUinGenerationSuccesstoElse() throws Exception {
             MessageDTO messageDTO = new MessageDTO();
             messageDTO.setRid("27847657360002520181210094052");
             String value="{\r\n" + 
                     "    \"identity\" : {\r\n" + 
                     "      \"IDSchemaVersion\" : 1.0,\r\n" + 
                     "      \"UIN\" : \"850740361021\",\r\n" + 
                     "      \"fullName\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"ابراهيم بن علي\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Ibrahim Ibn Ali\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"dateOfBirth\" : \"1955/04/15\",\r\n" + 
                     "      \"age\" : 45,\r\n" + 
                     "      \"gender\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"الذكر\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"mâle\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine1\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 1\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 1\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine2\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"addressLine3\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                     "      } ],\r\n" + 
                      "      \"region\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"طنجة - تطوان - الحسيمة\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Tanger-Tétouan-Al Hoceima\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"province\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"فاس-مكناس\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Fès-Meknès\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"city\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"الدار البيضاء\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"Casablanca\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"postalCode\" : \"570004\",\r\n" + 
                     "      \"phone\" : \"9876543210\",\r\n" + 
                     "      \"email\" : \"abc@xyz.com\",\r\n" + 
                     "      \"CNIENumber\" : 6789545678909,\r\n" + 
                     "      \"localAdministrativeAuthority\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"سلمى\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"salma\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"parentOrGuardianRIDOrUIN\" : 212124324784912,\r\n" + 
                     "      \"parentOrGuardianName\" : [ {\r\n" + 
                     "        \"language\" : \"ara\",\r\n" + 
                     "        \"value\" : \"سلمى\"\r\n" + 
                     "      }, {\r\n" + 
                     "        \"language\" : \"fre\",\r\n" + 
                     "        \"value\" : \"salma\"\r\n" + 
                     "      } ],\r\n" + 
                     "      \"proofOfAddress\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"drivingLicense\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfIdentity\" : {\r\n" + 
                     "        \"format\" : \"txt\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfRelationship\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"proofOfDateOfBirth\" : {\r\n" + 
                     "        \"format\" : \"pdf\",\r\n" + 
                     "        \"type\" : \"passport\",\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"individualBiometrics\" : {\r\n" + 
                     "        \"format\" : \"cbeff\",\r\n" + 
                     "        \"version\" : 1.0,\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      },\r\n" + 
                     "      \"parentOrGuardianBiometrics\" : {\r\n" + 
                     "        \"format\" : \"cbeff\",\r\n" + 
                     "        \"version\" : 1.0,\r\n" + 
                     "        \"value\" : \"fileReferenceID\"\r\n" + 
                     "      }\r\n" + 
                     "    },\r\n" + 
                     "    \"documents\" : [ {\r\n" + 
                     "      \"category\" : \"individualBiometrics\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"parentOrGuardianBiometrics\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"proofOfRelationship\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    }, {\r\n" + 
                     "      \"category\" : \"proofOfDateOfBirth\",\r\n" + 
                     "      \"value\" : \"dGVzdA\"\r\n" + 
                     "    } ]\r\n" + 
                     "  }";
             byte[] data = value.getBytes();
             PowerMockito.mockStatic(IOUtils.class);
             PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);
             UinResponseDto uinResponseDto = new UinResponseDto();
             uinResponseDto.setUin("493410317027");
       Mockito.when(registrationProcessorRestClientService.getApi(any(),any(),any(),any(),any())).thenReturn(uinResponseDto);
             
		String response = new String(
				"{\"error\":[{\"errCode\":\"TEST\",\"errMessage\":\"errorMessage\"}],\"id\":\"mosip.id.create\",\"version\":\"1.0\",\"timestamp\":\"2019-01-17T06:29:01.940Z\",\"status\":\"ACTIVATED\",\"response\":null}");
      Mockito.when(registrationProcessorRestClientService.postApi(any(),any(),any(),any(),any())).thenReturn(response);

           MessageDTO result=  uinGeneratorStage.process(messageDTO);
           assertTrue(result.getInternalError());
            
       }
       @Test
       public void testUinGenerationSuccessWithUIN() throws Exception {
    	   String value="{\r\n" + 
                   "    \"identity\" : {\r\n" + 
                   "      \"IDSchemaVersion\" : 1.0,\r\n" + 
                   "      \"UIN\" : \"850740361021\",\r\n" + 
                   "      \"fullName\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"ابراهيم بن علي\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"Ibrahim Ibn Ali\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"dateOfBirth\" : \"1955/04/15\",\r\n" + 
                   "      \"age\" : 45,\r\n" + 
                   "      \"gender\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"الذكر\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"mâle\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"addressLine1\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"عنوان العينة سطر 1\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"exemple d'adresse ligne 1\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"addressLine2\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"addressLine3\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"عنوان العينة سطر 2\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"exemple d'adresse ligne 2\"\r\n" + 
                   "      } ],\r\n" + 
                    "      \"region\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"طنجة - تطوان - الحسيمة\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"Tanger-Tétouan-Al Hoceima\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"province\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"فاس-مكناس\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"Fès-Meknès\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"city\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"الدار البيضاء\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"Casablanca\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"postalCode\" : \"570004\",\r\n" + 
                   "      \"phone\" : \"9876543210\",\r\n" + 
                   "      \"email\" : \"abc@xyz.com\",\r\n" + 
                   "      \"CNIENumber\" : 6789545678909,\r\n" + 
                   "      \"localAdministrativeAuthority\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"سلمى\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"salma\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"parentOrGuardianRIDOrUIN\" : 212124324784912,\r\n" + 
                   "      \"parentOrGuardianName\" : [ {\r\n" + 
                   "        \"language\" : \"ara\",\r\n" + 
                   "        \"value\" : \"سلمى\"\r\n" + 
                   "      }, {\r\n" + 
                   "        \"language\" : \"fre\",\r\n" + 
                   "        \"value\" : \"salma\"\r\n" + 
                   "      } ],\r\n" + 
                   "      \"proofOfAddress\" : {\r\n" + 
                   "        \"format\" : \"pdf\",\r\n" + 
                   "        \"type\" : \"drivingLicense\",\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      },\r\n" + 
                   "      \"proofOfIdentity\" : {\r\n" + 
                   "        \"format\" : \"txt\",\r\n" + 
                   "        \"type\" : \"passport\",\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      },\r\n" + 
                   "      \"proofOfRelationship\" : {\r\n" + 
                   "        \"format\" : \"pdf\",\r\n" + 
                   "        \"type\" : \"passport\",\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      },\r\n" + 
                   "      \"proofOfDateOfBirth\" : {\r\n" + 
                   "        \"format\" : \"pdf\",\r\n" + 
                   "        \"type\" : \"passport\",\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      },\r\n" + 
                   "      \"individualBiometrics\" : {\r\n" + 
                   "        \"format\" : \"cbeff\",\r\n" + 
                   "        \"version\" : 1.0,\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      },\r\n" + 
                   "      \"parentOrGuardianBiometrics\" : {\r\n" + 
                   "        \"format\" : \"cbeff\",\r\n" + 
                   "        \"version\" : 1.0,\r\n" + 
                   "        \"value\" : \"fileReferenceID\"\r\n" + 
                   "      }\r\n" + 
                   "    },\r\n" + 
                   "    \"documents\" : [ {\r\n" + 
                   "      \"category\" : \"individualBiometrics\",\r\n" + 
                   "      \"value\" : \"dGVzdA\"\r\n" + 
                   "    }, {\r\n" + 
                   "      \"category\" : \"parentOrGuardianBiometrics\",\r\n" + 
                   "      \"value\" : \"dGVzdA\"\r\n" + 
                   "    }, {\r\n" + 
                   "      \"category\" : \"proofOfRelationship\",\r\n" + 
                   "      \"value\" : \"dGVzdA\"\r\n" + 
                   "    }, {\r\n" + 
                   "      \"category\" : \"proofOfDateOfBirth\",\r\n" + 
                   "      \"value\" : \"dGVzdA\"\r\n" + 
                   "    } ]\r\n" + 
                   "  }";
    	   String response = new String(
   				"{\"id\":\"mosip.id.create\",\"version\":\"1.0\",\"timestamp\":\"2019-01-17T06:29:01.940Z\",\"status\":\"ACTIVATED\",\"response\":{\"entity\":\"https://dev.mosip.io/idrepo/v1.0/identity/203560486746\"}}");

           Mockito.when(registrationProcessorRestClientService.postApi(any(),any(),any(),any(),any())).thenReturn(response);

    	   byte[] data = value.getBytes();
           PowerMockito.mockStatic(IOUtils.class);
           PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);
             MessageDTO messageDTO = new MessageDTO();
             messageDTO.setRid("27847657360002520181210094052");
             MessageDTO result = uinGeneratorStage.process(messageDTO);
             assertFalse(result.getInternalError()); 
       }
       
       @Test
      	public void testException() throws Exception  {
       	   String value="{\r\n" + 
                      "    \"identity\" : {\r\n" + 
                      "      \"IDSchemaVersion\" : 1.0,\r\n" + 
                      "    }, {\r\n" + 
                      "      \"category\" : \"proofOfDateOfBirth\",\r\n" + 
                      "      \"value\" : \"dGVzdA\"\r\n" + 
                      "    } \r\n" + 
                      "  }";
       	   String response = new String(
      				"{\"id\":\"mosip.id.create\",\"version\":\"1.0\",\"timestamp\":\"2019-01-17T06:29:01.940Z\",\"status\":\"ACTIVATED\",\"response\":{\"entity\":\"https://dev.mosip.io/idrepo/v1.0/identity/203560486746\"}}");

              Mockito.when(registrationProcessorRestClientService.postApi(any(),any(),any(),any(),any())).thenReturn(response);

       	   byte[] data = value.getBytes();
              PowerMockito.mockStatic(IOUtils.class);
              PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);
       	   MessageDTO messageDTO = new MessageDTO();
              messageDTO.setRid("27847657360002520181210094052");
      		 
              uinGeneratorStage.process(messageDTO);

      	}
       @Test
   	public void testDeployVerticle() {
    	   uinGeneratorStage.deployVerticle();
   	}
}
