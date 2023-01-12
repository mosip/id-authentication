CREATE TABLE ida.kyc_token_store(
    id character varying(36) NOT NULL,
    id_vid_hash character varying(128) NOT NULL,
    kyc_token character varying(128),
    psu_token character varying(128),
    oidc_client_id character varying(128),
    request_trn_id character varying(64),
    token_issued_dtimes timestamp,
    auth_req_dtimes timestamp,
    kyc_token_status character varying(36),
    cr_by character varying(256) NOT NULL,
    cr_dtimes timestamp NOT NULL,
    upd_by character varying(256),
    upd_dtimes timestamp,
    is_deleted boolean DEFAULT FALSE,
    del_dtimes timestamp,
    CONSTRAINT pk_key_id PRIMARY KEY (id),
    CONSTRAINT kyc_token_const UNIQUE (kyc_token)
);
COMMENT ON TABLE ida.kyc_token_store IS 'Kyc Token Store: To store and maintain a token generate after successful authentication of resident for IdP.';
COMMENT ON COLUMN ida.kyc_token_store.id IS 'ID: Kyc Token id is a unique identifier (UUID) used to map uniqueness to the kyc token .';
COMMENT ON COLUMN ida.kyc_token_store.id_vid_hash IS 'IdVidHash: SHA 256 Hash value of the Id/VID.';
COMMENT ON COLUMN ida.kyc_token_store.kyc_token IS 'KYC Token: Random generator token used after successful authentication.';
COMMENT ON COLUMN ida.kyc_token_store.psu_token IS 'PSU Token: Partner Specific User Token will be created using partner details and token details.';
COMMENT ON COLUMN ida.kyc_token_store.oidc_client_id IS 'OIDC Client ID: An Id assigned to specific OIDC Client.';
COMMENT ON COLUMN ida.kyc_token_store.request_trn_id IS 'Request Transaction Id: An Unique Id received for the incoming request.';
COMMENT ON COLUMN ida.kyc_token_store.token_issued_dtimes IS 'Token Issued Datetime: The datatime token is issued after successful authentication.';
COMMENT ON COLUMN ida.kyc_token_store.auth_req_dtimes IS 'Auth Request Datetime: The datatime authentication request received to authenticate.';
COMMENT ON COLUMN ida.kyc_token_store.kyc_token_status IS 'KYC Token Status: To identify token is successfully used for kyc exchange.';
COMMENT ON COLUMN ida.kyc_token_store.cr_by IS 'Created By : ID or name of the user who create / insert record';
COMMENT ON COLUMN ida.kyc_token_store.cr_dtimes IS 'Created DateTimestamp : Date and Timestamp when the record is created/inserted';
COMMENT ON COLUMN ida.kyc_token_store.upd_by IS 'Updated By : ID or name of the user who update the record with new values';
COMMENT ON COLUMN ida.kyc_token_store.upd_dtimes IS 'Updated DateTimestamp : Date and Timestamp when any of the fields in the record is updated with new values.';
COMMENT ON COLUMN ida.kyc_token_store.is_deleted IS 'IS_Deleted : Flag to mark whether the record is Soft deleted.';
COMMENT ON COLUMN ida.kyc_token_store.del_dtimes IS 'Deleted DateTimestamp : Date and Timestamp when the record is soft deleted with is_deleted=TRUE';