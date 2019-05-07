package io.mosip.registration.processor.abis.handler.stage;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.code.RegistrationTransactionStatusCode;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.*;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AbisHandlerStage extends MosipVerticleManager {

    /** The cluster manager url. */
    @Value("${vertx.cluster.configuration}")
    private String clusterManagerUrl;

    /** The url. */
    @Value("${registration.processor.biometric.reference.url}")
    private String url;

    /** The max results. */
    @Value("${registration.processor.abis.maxResults}")
    private Integer maxResults;

    /** The target FPIR. */
    @Value("${registration.processor.abis.targetFPIR}")
    private Integer targetFPIR;
    
    /** The registration status service. */
	@Autowired
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

    @Autowired
    private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

    public void deployVerticle() {
        MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
        this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_HANDLER_BUS_IN,
                MessageBusAddress.ABIS_HANDLER_BUS_OUT);
    }

    @Override
    public MessageDTO process(MessageDTO object)  {
        String regId = object.getRid();
        InternalRegistrationStatusDto registrationStatusDto = registrationStatusService.getRegistrationStatus(regId);
        String transactionTypeCode = registrationStatusDto.getLatestTransactionTypeCode();
        String transactionStatusCode = registrationStatusDto.getLatestTransactionStatusCode();
        String transactionId = registrationStatusDto.getLatestRegistrationTransactionId();

        String bioRefId = getUUID();

        //check for identify in abis_request table for above transactionId
        Boolean isIdentifyRequestPresent = packetInfoManager.getIdentifyByTransactionId(transactionId);
        List<AbisApplicationDto> abisApplicationDtoList = packetInfoManager.getAllAbisDetails();

        if(!isIdentifyRequestPresent){
            List<RegBioRefDto> bioRefDtos = packetInfoManager.getBioRefIdByRegId(regId);
            if(bioRefDtos.isEmpty()) {
                insertInBioRef(regId, bioRefId);
                createInsertRequest(abisApplicationDtoList, transactionId,
                        bioRefId, regId);
                createIdentifyRequest(abisApplicationDtoList, transactionId,
                        bioRefId, regId, transactionTypeCode);
            }

        }

        //isIdentifyPresent =  false:
            // check if bio id of regid in reg_abisref table

            //if No: create in table abis_ref: create insert request in Abis request: create identify request in Abis
                //request

            //if yes:  Check if the Insert Request for the Bio ID is “PROCESSED” for all the Active ABISs
                //if insert has FAILED or ERRORS: create insert request and identify request

        //isIdentifyPresent = true
            //send it to bio or demo dedupe

        return object;
    }

    private void createIdentifyRequest(List<AbisApplicationDto> abisApplicationDtoList, String transactionId,
                                       String bioRefId, String regId, String transactionTypeCode) {
        List<RegDemoDedupeListDto> regDemoDedupeListDtoList = packetInfoManager.
                getDemoListByTransactionId(transactionId);
        List<ReferenceIdDto> referenceIdDtos = new ArrayList<>();
        for(RegDemoDedupeListDto dedupeListDto : regDemoDedupeListDtoList){
        	ReferenceIdDto dto = new ReferenceIdDto();
            dto.setReferenceId(dedupeListDto.getRegistrationTransaction().getReferenceId());
        }
        

        AbisIdentifyRequestDto abisIdentifyRequestDto = new AbisIdentifyRequestDto();
        abisIdentifyRequestDto.setId("mosip.abis.identify");
        abisIdentifyRequestDto.setVer("1.0");
        abisIdentifyRequestDto.setRequestId(getUUID());
        abisIdentifyRequestDto.setReferenceId(bioRefId);
        abisIdentifyRequestDto.setTimestamp(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L));
        abisIdentifyRequestDto.setMaxResults(maxResults);
        abisIdentifyRequestDto.setTargetFPIR(targetFPIR);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(abisIdentifyRequestDto);
        oos.flush();
        } catch(IOException e){

        }
        byte [] abisIdentifyRequestBytes = bos.toByteArray();

        String batchId = getUUID();
        for(AbisApplicationDto applicationDto : abisApplicationDtoList) {
            AbisRequestDto abisRequestDto = new AbisRequestDto();

            abisRequestDto.setId(getUUID());
            abisRequestDto.setAbisAppCode(applicationDto.getCode());
            abisRequestDto.setBioRefId(bioRefId);
            abisRequestDto.setRequestType("IDENTIFY");
            abisRequestDto.setReqBatchId(batchId);
            abisRequestDto.setRefRegtrnId(transactionId);
            abisRequestDto.setReqText(abisIdentifyRequestBytes);
            abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
            abisRequestDto.setStatusComment("");
            abisRequestDto.setLangCode("eng");
            abisRequestDto.setCrBy("MOSIP");
            abisRequestDto.setCrDtimes(LocalDateTime.now());
            abisRequestDto.setUpdBy("");
            abisRequestDto.setUpdDtimes(LocalDateTime.now());
            abisRequestDto.setIsDeleted(Boolean.FALSE);

            packetInfoManager.saveAbisRequest(abisRequestDto);
        }

        //req_text - Identify Request body to be sent to ABIS using Gallary as per ABIS API Spec in Git.
    }

    private void insertInBioRef(String regId, String bioRefId) {
        RegBioRefDto regBioRefDto = new RegBioRefDto();
        regBioRefDto.setBioRefId(bioRefId);
        regBioRefDto.setCrBy("MOSIP");
        regBioRefDto.setCrDtimes(LocalDateTime.now());
        regBioRefDto.setDelDtimes(null);
        regBioRefDto.setIsActive(Boolean.TRUE);
        regBioRefDto.setIsDeleted(Boolean.FALSE);
        regBioRefDto.setRegId(regId);
        regBioRefDto.setUpdBy(null);
        regBioRefDto.setUpdDtimes(LocalDateTime.now());
        packetInfoManager.saveBioRef(regBioRefDto);
    }

    private void createInsertRequest(List<AbisApplicationDto> abisApplicationDtoList, String transactionId,
    		String bioRefId, String regId){
        AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
        abisInsertRequestDto.setId("mosip.abis.insert");
        abisInsertRequestDto.setReferenceId(getUUID());
        abisInsertRequestDto.setReferenceURL(url + regId);
        abisInsertRequestDto.setRequestId(getUUID());
        abisInsertRequestDto.setTimestamp(String.valueOf(new Timestamp(System.currentTimeMillis()).getTime() / 1000L));
        abisInsertRequestDto.setVer("1.0");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(abisInsertRequestDto);
            oos.flush();
        } catch(IOException e){

        }
        byte [] abisInsertRequestBytes = bos.toByteArray();
        String batchId = getUUID();
        for(AbisApplicationDto applicationDto : abisApplicationDtoList) {
            AbisRequestDto abisRequestDto = new AbisRequestDto();

            abisRequestDto.setId(getUUID());
            abisRequestDto.setAbisAppCode(applicationDto.getCode());
            abisRequestDto.setBioRefId(bioRefId);
            abisRequestDto.setRequestType("INSERT");
            abisRequestDto.setReqBatchId(batchId);
            abisRequestDto.setRefRegtrnId(transactionId);
            abisRequestDto.setReqText(abisInsertRequestBytes);
            abisRequestDto.setStatusCode(RegistrationTransactionStatusCode.IN_PROGRESS.toString());
            abisRequestDto.setStatusComment("");
            abisRequestDto.setLangCode("eng");
            abisRequestDto.setCrBy("MOSIP");
            abisRequestDto.setCrDtimes(LocalDateTime.now());
            abisRequestDto.setUpdBy("");
            abisRequestDto.setUpdDtimes(LocalDateTime.now());
            abisRequestDto.setIsDeleted(Boolean.FALSE);

            packetInfoManager.saveAbisRequest(abisRequestDto);
        }
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }
}
