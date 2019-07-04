
-- Foreign Key Constraints Same DB/Schema tables.


-- FOREIGN KEY CONSTRAINTS : mosip_kernal database/schema.

alter table kernel.sync_control add constraint fk_synctrl_syncjob foreign key (syncjob_id ) references kernel.sync_job_def(id) on delete no action on update no action ;
alter table kernel.sync_control add constraint fk_synctrl_synctrn foreign key (synctrn_id) references kernel.sync_transaction(id) on delete no action on update no action ;
alter table kernel.sync_transaction add constraint fk_synctrn_syncjob foreign key (syncjob_id ) references kernel.sync_job_def(id) on delete no action on update no action ;
