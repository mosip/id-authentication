-- Foreign Key Constraints Same DB/Schema tables.

-- FOREIGN KEY CONSTRAINTS : mosip_iam database/schema.

alter table iam.user_pwd add constraint fk_usrpwd_usrdtl foreign key (usr_id) references iam.user_detail(id) on delete no action on update no action ;
alter table iam.user_role add constraint fk_usrrol_usrdtl foreign key (usr_id) references iam.user_detail(id) on delete no action on update no action ;
alter table iam.user_role add constraint fk_usrrol_rolelst foreign key (role_code, lang_code) references iam.role_list(code, lang_code) on delete no action on update no action ;
