
-- Foreign Key Constraints Same DB/Schema tables.

-- FOREIGN KEY CONSTRAINTS : mosip_reg database/schema.

alter table reg.registration_transaction add constraint fk_regtrn_reg foreign key (reg_id) references reg.registration(id) on delete no action on update no action ;
alter table reg.registration_transaction add constraint fk_regtrn_trntyp foreign key (trn_type_code, lang_code) references reg.transaction_type(code, lang_code) on delete no action on update no action ;
alter table reg.sync_control add constraint fk_synctrl_cntrmac foreign key (machine_id, regcntr_id) references reg.reg_center_machine(machine_id, regcntr_id) on delete no action on update no action ;
alter table reg.sync_control add constraint fk_synctrl_syncjob foreign key (syncjob_id ) references reg.sync_job_def(id) on delete no action on update no action ;
alter table reg.sync_control add constraint fk_synctrl_synctrn foreign key (synctrn_id) references reg.sync_transaction(id) on delete no action on update no action ;
alter table reg.sync_transaction add constraint fk_synctrn_cntrmac foreign key (machine_id, regcntr_id) references reg.reg_center_machine(machine_id, regcntr_id) on delete no action on update no action ;
alter table reg.sync_transaction add constraint fk_synctrn_syncjob foreign key (syncjob_id ) references reg.sync_job_def(id) on delete no action on update no action ;
alter table reg.user_pwd add constraint fk_usrpwd_usrdtl foreign key (usr_id) references reg.user_detail(id) on delete no action on update no action ;
alter table reg.user_role add constraint fk_usrrol_usrdtl foreign key (usr_id) references reg.user_detail(id) on delete no action on update no action ;
alter table reg.reg_center_device add constraint fk_cntrdev_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.reg_center_device add constraint fk_cntrdev_devicem foreign key (device_id) references reg.device_master(id) on delete no action on update no action ;
alter table reg.reg_center_machine add constraint fk_cntrmac_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.reg_center_machine add constraint fk_cntrmac_machm foreign key (machine_id) references reg.machine_master(id) on delete no action on update no action ;
alter table reg.reg_center_user_machine add constraint fk_cntrmusr_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.reg_center_user_machine add constraint fk_cntrmusr_usrdtl foreign key (usr_id) references reg.user_detail(id) on delete no action on update no action ;
alter table reg.reg_center_user_machine add constraint fk_cntrmusr_machm foreign key (machine_id) references reg.machine_master(id) on delete no action on update no action ;
alter table reg.reg_center_user add constraint fk_cntrusr_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.reg_center_user add constraint fk_cntrusr_usrdtl foreign key (usr_id) references reg.user_detail(id) on delete no action on update no action ;

alter table reg.reg_center_machine_device add constraint fk_cntrmdev_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.reg_center_machine_device add constraint fk_cntrmdev_machm foreign key (machine_id) references reg.machine_master(id) on delete no action on update no action ;
alter table reg.reg_center_machine_device add constraint fk_cntrmdev_devicem foreign key (device_id) references reg.device_master(id) on delete no action on update no action ;

alter table reg.registration add constraint fk_reg_reg_usrdtl foreign key (reg_usr_id) references reg.user_detail(id) on delete no action on update no action ;
alter table reg.registration add constraint fk_reg_regcntr foreign key (regcntr_id) references reg.registration_center(id) on delete no action on update no action ;
alter table reg.registration add constraint fk_reg_appr_usrdtl foreign key (approver_usr_id) references reg.user_detail(id) on delete no action on update no action ;

alter table reg.device_master add constraint fk_devicem_dspec foreign key (dspec_id) references reg.device_spec(id) on delete no action on update no action ;
alter table reg.device_spec add constraint fk_dspec_dtyp foreign key (dtyp_code, lang_code) references reg.device_type(code, lang_code) on delete no action on update no action ;
alter table reg.machine_master add constraint fk_machm_mspec foreign key (mspec_id) references reg.machine_spec(id) on delete no action on update no action ;
alter table reg.machine_spec add constraint fk_mspec_mtyp foreign key (mtyp_code, lang_code) references reg.machine_type(code, lang_code) on delete no action on update no action ;

alter table reg.user_biometric add constraint fk_usrbio_usrdtl foreign key (usr_id) references reg.user_detail(id) on delete no action on update no action ;