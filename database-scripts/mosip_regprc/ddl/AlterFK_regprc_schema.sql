
-- Foreign Key Constraints Same DB/Schema tables.

-- FOREIGN KEY CONSTRAINTS : mosip_regprc database/schema.

alter table regprc.applicant_demographic add constraint fk_appldem_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.applicant_document add constraint fk_appldoc_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.applicant_fingerprint add constraint fk_appfprnt_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.applicant_iris add constraint fk_appliris_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.applicant_photograph add constraint fk_applphot_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.biometric_exception add constraint fk_bioexcp_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.registration_transaction add constraint fk_regtrn_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.registration_transaction add constraint fk_regtrn_trntyp foreign key (trn_type_code, lang_code) references regprc.transaction_type(code, lang_code) on delete no action on update no action ;
alter table regprc.qcuser_registration add constraint fk_qcureg_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.reg_manual_verification add constraint fk_rmnlver_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.individual_demographic_dedup add constraint fk_idemogd_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;