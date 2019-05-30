
-- Foreign Key Constraints Same DB/Schema tables.
-- FOREIGN KEY CONSTRAINTS : mosip_regprc database/schema.
alter table regprc.registration_transaction add constraint fk_regtrn_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.registration_transaction add constraint fk_regtrn_trntyp foreign key (trn_type_code, lang_code) references regprc.transaction_type(code, lang_code) on delete no action on update no action ;
alter table regprc.individual_demographic_dedup add constraint fk_idemogd_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;

alter table regprc.reg_manual_verification add constraint fk_rmnlver_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.reg_manual_verification add constraint fk_rmnlver_trntyp foreign key (trntyp_code, lang_code) references regprc.transaction_type(code, lang_code) on delete no action on update no action ;


-- object: fk_regded_regtrn | type: CONSTRAINT --
-- ALTER TABLE regprc.reg_demo_dedupe_list DROP CONSTRAINT IF EXISTS fk_regded_regtrn CASCADE;
ALTER TABLE regprc.reg_demo_dedupe_list ADD CONSTRAINT fk_regded_regtrn FOREIGN KEY (regtrn_id)
REFERENCES regprc.registration_transaction (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


-- object: fk_regref_reg | type: CONSTRAINT --
-- ALTER TABLE regprc.reg_bio_ref DROP CONSTRAINT IF EXISTS fk_regref_reg CASCADE;
ALTER TABLE regprc.reg_bio_ref ADD CONSTRAINT fk_regbrf_reg FOREIGN KEY (reg_id)
REFERENCES regprc.registration (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


-- object: fk_abisresp_abisreq | type: CONSTRAINT --
-- ALTER TABLE regprc.abis_response DROP CONSTRAINT IF EXISTS fk_abisresp_abisreq CASCADE;
ALTER TABLE regprc.abis_response ADD CONSTRAINT fk_abisresp_abisreq FOREIGN KEY (abis_req_id)
REFERENCES regprc.abis_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


-- object: fk_abisresp_resp_id | type: CONSTRAINT --
-- ALTER TABLE regprc.abis_response_det DROP CONSTRAINT IF EXISTS fk_abisresp_resp_id CASCADE;
ALTER TABLE regprc.abis_response_det ADD CONSTRAINT fk_abisresp_resp_id FOREIGN KEY (abis_resp_id)
REFERENCES regprc.abis_response (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


-- object: fk_rlostd_reg | type: CONSTRAINT --
-- ALTER TABLE regprc.reg_lost_uin_det DROP CONSTRAINT IF EXISTS fk_rlostd_reg CASCADE;
ALTER TABLE regprc.reg_lost_uin_det ADD CONSTRAINT fk_rlostd_reg FOREIGN KEY (reg_id)
REFERENCES regprc.registration (id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;

