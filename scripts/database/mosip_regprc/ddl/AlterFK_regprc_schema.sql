
-- Foreign Key Constraints Same DB/Schema tables.
-- FOREIGN KEY CONSTRAINTS : mosip_regprc database/schema.
alter table regprc.registration_transaction add constraint fk_regtrn_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.registration_transaction add constraint fk_regtrn_trntyp foreign key (trn_type_code, lang_code) references regprc.transaction_type(code, lang_code) on delete no action on update no action ;
alter table regprc.individual_demographic_dedup add constraint fk_idemogd_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;

alter table regprc.reg_manual_verification add constraint fk_rmnlver_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.reg_manual_verification add constraint fk_rmnlver_trntyp foreign key (trntyp_code, lang_code) references regprc.transaction_type(code, lang_code) on delete no action on update no action ;

alter table regprc.reg_abisref add constraint fk_regref_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
alter table regprc.reg_uin add constraint fk_reguin_reg foreign key (reg_id) references regprc.registration(id) on delete no action on update no action ;
