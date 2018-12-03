
-- Foreign Key Constraints Same DB/Schema tables.


-- FOREIGN KEY CONSTRAINTS : mosip_master database/schema.

alter table master.admin_param add constraint fk_admparm_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.appl_form_type add constraint fk_applftyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.app_detail add constraint fk_appdtl_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.app_authentication_method add constraint fk_appauthm_appdtl foreign key (app_id) references master.app_detail(id) on delete no action on update no action ;
alter table master.app_authentication_method add constraint fk_appauthm_authmeth foreign key (auth_method_code, lang_code) references master.authentication_method(code, lang_code) on delete no action on update no action ;
alter table master.app_authentication_method add constraint fk_appauthm_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.module_detail add constraint fk_moddtl_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.authentication_type add constraint fk_authtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.biometric_attribute add constraint fk_bmattr_bmtyp foreign key (bmtyp_code, lang_code) references master.biometric_type(code , lang_code) on delete no action on update no action ;
alter table master.biometric_attribute add constraint fk_bmattr_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.biometric_type add constraint fk_bmtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.device_master add constraint fk_devicem_dspec foreign key (dspec_id) references master.device_spec(id) on delete no action on update no action ;
alter table master.device_master add constraint fk_devicem_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.device_spec add constraint fk_dspec_dtyp foreign key (dtyp_code, lang_code) references master.device_type(code, lang_code) on delete no action on update no action ;
alter table master.device_spec add constraint fk_dspec_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.device_type add constraint fk_dtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.doc_category add constraint fk_doccat_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.doc_format add constraint fk_docfmt_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.doc_type add constraint fk_doctyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.gender add constraint fk_gndr_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.global_param add constraint fk_glbparm_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.id_type add constraint fk_idtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.introducer_type add constraint fk_intyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.location add constraint fk_loc_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.loc_holiday add constraint fk_lochol_loc foreign key (location_code, lang_code) references master.location(code, lang_code) on delete no action on update no action ;
alter table master.loc_holiday add constraint fk_lochol_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.login_method add constraint fk_logmeth_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.machine_master add constraint fk_machm_mspec foreign key (mspec_id) references master.machine_spec(id) on delete no action on update no action ;
alter table master.machine_master add constraint fk_machm_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.machine_spec add constraint fk_mspec_mtyp foreign key (mtyp_code, lang_code) references master.machine_type(code, lang_code) on delete no action on update no action ;
alter table master.machine_spec add constraint fk_mspec_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.machine_type add constraint fk_mtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.message_list add constraint fk_msglst_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.registration_center add constraint fk_regcntr_cntrtyp foreign key (cntrtyp_code, lang_code) references master.reg_center_type(code, lang_code) on delete no action on update no action ;
alter table master.registration_center add constraint fk_regcntr_loc foreign key (location_code, lang_code) references master.location(code, lang_code) on delete no action on update no action ;
alter table master.registration_center add constraint fk_regcntr_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.reg_center_device add constraint fk_cntrdev_regcntr foreign key (regcntr_id) references master.registration_center(id) on delete no action on update no action ;
alter table master.reg_center_device add constraint fk_cntrdev_devicem foreign key (device_id) references master.device_master(id) on delete no action on update no action ;
alter table master.reg_center_machine add constraint fk_cntrmac_regcntr foreign key (regcntr_id) references master.registration_center(id) on delete no action on update no action ;
alter table master.reg_center_machine add constraint fk_cntrmac_machm foreign key (machine_id) references master.machine_master(id) on delete no action on update no action ;
alter table master.reg_center_type add constraint fk_cntrtyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.reg_center_user_machine add constraint fk_cntrmusr_regcntr foreign key (regcntr_id) references master.registration_center(id) on delete no action on update no action ;
alter table master.reg_center_user_machine add constraint fk_cntrmusr_usrdtl foreign key (usr_id) references master.user_detail(id) on delete no action on update no action ;
alter table master.reg_center_user_machine add constraint fk_cntrmusr_machm foreign key (machine_id) references master.machine_master(id) on delete no action on update no action ;

alter table master.reg_center_machine_device add constraint fk_cntrmdev_regcntr foreign key (regcntr_id) references master.registration_center(id) on delete no action on update no action ;
alter table master.reg_center_machine_device add constraint fk_cntrmdev_machm foreign key (machine_id) references master.machine_master(id) on delete no action on update no action ;
alter table master.reg_center_machine_device add constraint fk_cntrmdev_devicem foreign key (device_id) references master.device_master(id) on delete no action on update no action ;

alter table master.reg_center_user add constraint fk_cntrusr_regcntr foreign key (regcntr_id) references master.registration_center(id) on delete no action on update no action ;
alter table master.reg_center_user add constraint fk_cntrusr_usrdtl foreign key (usr_id) references master.user_detail(id) on delete no action on update no action ;
alter table master.role_list add constraint fk_rolelst_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.screen_authorization add constraint fk_scrauth_scrdtl foreign key (screen_id) references master.screen_detail(id) on delete no action on update no action ;
alter table master.screen_authorization add constraint fk_scrauth_rolelst foreign key (role_code, lang_code) references master.role_list(code, lang_code) on delete no action on update no action ;
alter table master.screen_authorization add constraint fk_scrauth_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.screen_detail add constraint fk_scrdtl_appdtl foreign key (app_id) references master.app_detail(id) on delete no action on update no action ;
alter table master.screen_detail add constraint fk_scrdtl_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.status_type add constraint fk_sttyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.status_list add constraint fk_status_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.status_list add constraint fk_status_sttyp foreign key (sttyp_code, lang_code) references master.status_type(code, lang_code) on delete no action on update no action ;

alter table master.reason_category add constraint fk_rsncat_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.reason_list add constraint fk_rsnlst_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.reason_list add constraint fk_rsnlst_rsncat foreign key (rsncat_code, lang_code) references master.reason_category(code, lang_code) on delete no action on update no action ;

alter table master.template_file_format add constraint fk_tffmt_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.template_type add constraint fk_tmpltyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.template add constraint fk_tmplt_tmpltyp foreign key (template_typ_code, lang_code) references master.template_type(code, lang_code) on delete no action on update no action ;
alter table master.template add constraint fk_tmplt_tffmt foreign key (file_format_code, lang_code) references master.template_file_format(code, lang_code) on delete no action on update no action ;
alter table master.template add constraint fk_tmplt_moddtl foreign key (module_id) references master.module_detail(id) on delete no action on update no action ;
alter table master.template add constraint fk_tmplt_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.title add constraint fk_ttl_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.transaction_type  add constraint fk_trntyp_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.user_detail add constraint fk_usrdtl_logmeth foreign key (last_login_method, lang_code) references master.login_method(code, lang_code) on delete no action on update no action ;
alter table master.user_detail add constraint fk_usrdtl_status foreign key (status_code, lang_code) references master.status_list(code, lang_code) on delete no action on update no action ;
alter table master.user_detail add constraint fk_usrdtl_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;

alter table master.user_pwd add constraint fk_usrpwd_usrdtl foreign key (usr_id) references master.user_detail(id) on delete no action on update no action ;
alter table master.user_pwd add constraint fk_usrpwd_status foreign key (status_code, lang_code) references master.status_list(code, lang_code) on delete no action on update no action ;
alter table master.user_pwd add constraint fk_usrpwd_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.user_role add constraint fk_usrrol_usrdtl foreign key (usr_id) references master.user_detail(id) on delete no action on update no action ;
alter table master.user_role add constraint fk_usrrol_rolelst foreign key (role_code, lang_code) references master.role_list(code, lang_code) on delete no action on update no action ;
alter table master.user_role add constraint fk_usrrol_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.valid_document add constraint fk_valdoc_doctyp foreign key (doctyp_code, lang_code) references master.doc_type(code, lang_code) on delete no action on update no action ;
alter table master.valid_document add constraint fk_valdoc_doccat foreign key (doccat_code, lang_code) references master.doc_category(code, lang_code) on delete no action on update no action ;
alter table master.valid_document add constraint fk_valdoc_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
alter table master.blacklisted_words add constraint fk_blwrd_lang foreign key (lang_code) references master.language(code) on delete no action on update no action ;
