\echo 'Upgrade Queries not required for transition from $CURRENT_VERSION to $UPGRADE_VERSION'

DROP TABLE IF EXISTS ida.cred_subject_id_store CASCADE;

DROP INDEX IF EXISTS ida.ind_csid_key_hash;

DELETE FROM ida.key_policy_def WHERE app_id='IDA_VCI_EXCHANGE';