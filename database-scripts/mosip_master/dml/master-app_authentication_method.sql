INSERT INTO master.app_authentication_method(app_id,auth_method_code,process_id,role_code,method_seq,lang_code,is_active,cr_by,cr_dtimes ) VALUES ('10003','PWD','login_auth','OFFICER','1','eng',true,'superadmin',now())
, ('10003','OTP','login_auth','OFFICER','2','eng',true,'superadmin',now())
, ('10003','PWD','login_auth','SUPERVISOR','1','eng',true,'superadmin',now())
, ('10003','PWD','packet_auth','OFFICER','1','eng',true,'superadmin',now())
, ('10003','IRIS','eod_auth','OFFICER','1','eng',true,'superadmin',now())
, ('10003','BIO','exception_auth','SUPERVISOR','1','eng',true,'superadmin',now())
, ('10003','FACE','exception_auth','OFFICER','2','eng',true,'superadmin',now())
, ('10003','PWD','onboard_auth','OFFICER','1','eng',true,'superadmin',now())
, ('10003','OTP','onboard_auth','SUPERVISOR','2','eng',true,'superadmin',now());
