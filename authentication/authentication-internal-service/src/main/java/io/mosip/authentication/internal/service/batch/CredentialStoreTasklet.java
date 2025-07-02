package io.mosip.authentication.internal.service.batch;

import io.mosip.authentication.common.service.entity.CredentialEventStore;
import io.mosip.authentication.common.service.repository.CredentialEventStoreRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.spi.idevent.CredentialStoreService;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.RetryingBeforeRetryIntervalException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

@Component
public class CredentialStoreTasklet implements Tasklet {

    @Value("${ida.batch.credential.store.thread.count:10}")
    private int threadCount;

    /** The logger. */
    private static Logger LOGGER = IdaLogger.getLogger(CredentialStoreTasklet.class);

    ForkJoinPool forkJoinPool;

    private static final String IDA_CREDENTIAL_ITEM_TASKLET = "CredentialStoreTasklet";

    @Autowired
    private IdAuthSecurityManager securityManager;

    @Autowired
    private CredentialEventStoreRepository credentialEventRepo;

    @Autowired
    private IdentityCacheRepository identityCacheRepo;

    @Value("${ida.batch.credential.store.page.size:100}")
    private int pageSize;

    /** The credential store service. */
    @Autowired
    private CredentialStoreService credentialStoreService;

    @PostConstruct
    public void init() {
        forkJoinPool = new ForkJoinPool(threadCount);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        String batchId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        LOGGER.info(securityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                "Inside CredentialStoreTasklet.execute() method");

        List<CredentialEventStore> credentialEventStoreList = credentialEventRepo.findNewOrFailedEvents(pageSize);
        try {
            forkJoinPool.submit(() -> credentialEventStoreList.parallelStream().forEach(credential -> {
                try {
                    credentialStoreService.storeIdentityEntity(credentialStoreService.processCredentialStoreEvent(credential));

                 } catch (IdAuthenticationBusinessException e) {
                    LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                            "IdAuthenticationBusinessException : " + ExceptionUtils.getStackTrace(e));
                } catch (RetryingBeforeRetryIntervalException e) {
                    LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                            "RetryingBeforeRetryIntervalException : " + ExceptionUtils.getStackTrace(e));
                } catch (RuntimeException e) {
                    LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                            "RuntimeException : " + ExceptionUtils.getStackTrace(e));
                }  catch (Exception e) {
                    LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                            "Exception : " + ExceptionUtils.getStackTrace(e));
                }
            })).get();
            long endTime = System.currentTimeMillis();
            LOGGER.debug(IdRepoSecurityManager.getUser(), "Perform" + IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                    "Total time taken to complete process of " + credentialEventStoreList.size() + " records (" + (endTime - startTime) + "ms)");
        } catch (InterruptedException e) {
            LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                    "InterruptedException : " + ExceptionUtils.getStackTrace(e));
            throw e;
        } catch (ExecutionException e) {
            LOGGER.error(IdRepoSecurityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                    "ExecutionException : " + ExceptionUtils.getStackTrace(e));
        }

        if (!CollectionUtils.isEmpty(credentialEventStoreList)) {
            credentialEventRepo.saveAll(credentialEventStoreList);
         }
        LOGGER.info(securityManager.getUser(), IDA_CREDENTIAL_ITEM_TASKLET, "batchid = " + batchId,
                "CredentialStoreTasklet Existing Batch");

        return RepeatStatus.FINISHED;
    }
}
