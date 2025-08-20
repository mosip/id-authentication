package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

@Repository
public interface IdentityCacheRepository extends BaseRepository<IdentityEntity, String> {

    /* ============================
     * Keep existing methods (as requested)
     * ============================ */

    /**
     * Fetch only required fields for demo data by Id.
     * Avoids fetching unnecessary large objects.
     */
    @Query("SELECT i.id, i.demographicData, i.expiryTimestamp, i.transactionLimit, i.token, " +
            "i.crBy, i.crDTimes, i.updBy, i.updDTimes, i.isDeleted, i.delDTimes " +
            "FROM IdentityEntity i WHERE i.id = :id")
    List<Object[]> findDemoDataById(@Param("id") String id);

    /**
     * Fetch only transaction limit by Id.
     */
    @Query("SELECT i.id, i.expiryTimestamp, i.transactionLimit FROM IdentityEntity i WHERE i.id = :id")
    List<Object[]> findTransactionLimitById(@Param("id") String id);

    /**
     * Inherited existsById from BaseRepository is preferred (index-only scans).
     * We keep this redeclaration for compatibility, without a custom @Query.
     */
    boolean existsById(String id);

    /**
     * Optional: boolean existence via COUNT for DBs where you want a single scalar query.
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM IdentityEntity i WHERE i.id = :id")
    boolean existsByIdCount(@Param("id") String id);

    /* ============================
     * Closed projections (enhancements)
     * ============================ */
    interface DemoIdentityView {
        String getId();
        byte[] getDemographicData();
        LocalDateTime getExpiryTimestamp();
        Integer getTransactionLimit();
        String getToken();
    }

    interface FullIdentityView extends DemoIdentityView {
        byte[] getBiometricData();
    }

    interface IdKeyBindingView {
        LocalDateTime getExpiryTimestamp();
        Integer getTransactionLimit();
    }

    /* ============================
     * Targeted, column-only JPQL selects
     * ============================ */
    @Query("""
        select i.id as id,
               i.demographicData as demographicData,
               i.expiryTimestamp as expiryTimestamp,
               i.transactionLimit as transactionLimit,
               i.token as token
          from IdentityEntity i
         where i.id = :id
    """)
    Optional<DemoIdentityView> findDemoViewById(@Param("id") String id);

    @Query("""
        select i.id as id,
               i.demographicData as demographicData,
               i.biometricData as biometricData,
               i.expiryTimestamp as expiryTimestamp,
               i.transactionLimit as transactionLimit,
               i.token as token
          from IdentityEntity i
         where i.id = :id
    """)
    Optional<FullIdentityView> findFullViewById(@Param("id") String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    with dec as (
      update ida.identity_cache
         set transaction_limit = transaction_limit - 1
       where id = :id
         and transaction_limit > 1
       returning 1
    )
    delete from ida.identity_cache
     where id = :id
       and transaction_limit = 1
       and not exists (select 1 from dec)
    """, nativeQuery = true)
    int consumeVidOnce(@Param("id") String id);
}