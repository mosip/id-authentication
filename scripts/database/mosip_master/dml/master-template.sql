INSERT INTO master.template(id,name,descr,file_format_code,model,file_txt,module_id,module_name,template_typ_code,lang_code,is_active,cr_by,cr_dtimes ) VALUES ('1101','Template for authorization content','Template for authorization content','txt','','"Dear $name
Your Authentication of UIN $uin using $authType on $date at $time Hrs $status at a device deployed by MOSIP Services"','10007','ID Authentication','auth-email-content','eng',TRUE,'superadmin',now())
, ('1102','Template for authorization subject','Template for authorization subject','txt','','UIN $uin Authentication $status','10007','ID Authentication','auth-email-subject','eng',TRUE,'superadmin',now())
, ('1103','Template for authorization SMS','Template for authorization SMS','txt','','Your Authentication of UIN $uin using $authType on $date at $time Hrs $status at a device deployed by MOSIP Services.','10007','ID Authentication','auth-sms','eng',TRUE,'superadmin',now())
, ('1104','Template for Email Content','Template for Email Content','txt','','"Dear $name
OTP for UIN  $uin is $otp and is valid for $validTime minutes. (Generated on $date at $time Hrs)"','10007','ID Authentication','otp-email-content','eng',TRUE,'superadmin',now())
, ('1105','Template for Email Subject','Template for Email Subject','txt','','UIN $uin: OTP Request','10007','ID Authentication','otp-email-subject','eng',TRUE,'superadmin',now())
, ('1106','Template for OTP in SMS ','Template for OTP in SMS ','txt','','OTP for UIN  $uin is $otp and is valid for $validTime minutes. (Generated on $date at $time Hrs)','10007','ID Authentication','otp-sms','eng',TRUE,'superadmin',now())
, ('1107','قالب لمحتوى التخويل','قالب لمحتوى التخويل','txt','','OTP لـ UIN $uin هو $otp وهو صالح لمدة $validTime دقيقة. (التي تم إنشاؤها على $date في $time ساعات)','10008','مصادقة الهوية','auth-email-content','ara',TRUE,'superadmin',now())
, ('1108','قالب لموضوع التخويل','قالب لموضوع التخويل','txt','','UIN $uin: Authentication $status','10008','مصادقة الهوية','auth-email-subject','ara',TRUE,'superadmin',now())
, ('1109','قالب لرسالة التفويض','قالب لرسالة التفويض','txt','','"لديك مصادقة UIN $uin باستخدام authType$ على date$ في time Hs $status$ في جهاز تم نشره بواسطة ""خدمات MOSIP""."','10008','مصادقة الهوية','auth-sms','ara',TRUE,'superadmin',now())
, ('1110','قالب لمحتوى البريد الإلكتروني','قالب لمحتوى البريد الإلكتروني','txt','','"$name
OTP لـ UIN $uin هو $otp وهو صالح لمدة $validTime دقيقة. (التي تم إنشاؤها على $date في $time ساعات)"','10008','مصادقة الهوية','otp-email-content','ara',TRUE,'superadmin',now())
, ('1111','قالب لموضوع البريد الإلكتروني','قالب لموضوع البريد الإلكتروني','txt','','UIN $uin: OTP Request','10008','مصادقة الهوية','otp-email-subject','ara',TRUE,'superadmin',now())
, ('1112','قالب كلمة المرور لمرة واحدة في الرسالة','قالب كلمة المرور لمرة واحدة في الرسالة','txt','','OTP لـ UIN $uin هو $otp وهو صالح لمدة $validTime دقيقة. (التي تم إنشاؤها على $date في $time ساعات)','10008','مصادقة الهوية','otp-sms','ara',TRUE,'superadmin',now())
, ('1113','Modèle de contenu dautorisation','Modèle de contenu dautorisation','txt','','"Cher $name,
Votre authentification UIN $uin utilisant $authType le $date à $time Hrs $status sur un périphérique déployé par ""MOSIP Services"""','10016','Authentification ID','auth-email-content','fra',TRUE,'superadmin',now())
, ('1114','Modèle pour sujet dautorisation','Modèle pour sujet dautorisation','txt','','UIN $uin: $status dauthentification','10016','Authentification ID','auth-email-subject','fra',TRUE,'superadmin',now())
, ('1115','Modèle de SMS dautorisation','Modèle de SMS dautorisation','txt','','"Votre authentification UIN $uin utilisant $authType le $date à $time Hrs $status sur un périphérique déployé par ""MOSIP Services""."','10016','Authentification ID','auth-sms','fra',TRUE,'superadmin',now())
, ('1116','Modèle de contenu de courrier électronique','Modèle de contenu de courrier électronique','txt','','"Cher $name,
OTP pour UIN $uin est $otp et est valide pour $validTime minutes. (Généré le $date à $time Hrs)"','10016','Authentification ID','otp-email-content','fra',TRUE,'superadmin',now())
, ('1117','Modèle pour sujet demail','Modèle pour sujet demail','txt','','UIN $uin: Requête OTP','10016','Authentification ID','otp-email-subject','fra',TRUE,'superadmin',now())
, ('1118','Modèle pour OTP dans SMS','Modèle pour OTP dans SMS','txt','','OTP pour UIN $uin est $otp et est valide pour $validTime minutes. (Généré le $date à $time Hrs)','10016','Authentification ID','otp-sms','fra',TRUE,'superadmin',now())
, ('1119','Template for duplicate UIN Email','Template for duplicate UIN Email','txt','','"Hi $name,
Your Request for Registration $RID has failed because an UIN has been found against your details. Please visit your nearest Registration Office.
And Visit https://mosip.io/grievances

Thanks and Regards,
MOSIP Team"','10005','Registration Processor','RPR_DUP_UIN_EMAIL','eng',TRUE,'superadmin',now())
, ('1120','Template for duplicate UIN SMS','Template for duplicate UIN SMS','txt','','"Hi $name,
Your Request for Registration $RID has failed because an UIN has been found against your details. Please visit your nearest Registration Office.
And Visit https://mosip.io/grievances"','10005','Registration Processor','RPR_DUP_UIN_SMS','eng',TRUE,'superadmin',now())
, ('1121','Template for Technical Issue Email','Template for Technical Issue Email','txt','','"Hi $name,
Your Request for Registration $RID has failed because of an Technical issue please visit your nearest Registration Office.
And Visit https://mosip.io/grievances

Thanks and Regards,
MOSIP Team"','10005','Registration Processor','RPR_TEC_ISSUE_EMAIL','eng',TRUE,'superadmin',now())
, ('1122','Template for Technical Issue SMS','Template for Technical Issue SMS','txt','','"Hi $name,
Your Request for Registration $RID has failed because of an Technical issue please visit your nearest Registration Office.
And Visit https://mosip.io/grievances"','10005','Registration Processor','RPR_TEC_ISSUE_SMS','eng',TRUE,'superadmin',now())
, ('1123','Template for UIN generation Email','Template for UIN generation Email','txt','','"Hi $name,
Your UIN for the Registration $RID has been successfully generated and will reach soon at your Postal Address.

Thanks and Regards,
MOSIP Team"','10005','Registration Processor','RPR_UIN_GEN_EMAIL','eng',TRUE,'superadmin',now())
, ('1124','Template for UIN generation SMS','Template for UIN generation SMS','txt','','"Hi $name,
Your UIN for the Registration $RID has been successfully generated and will reach soon at your Postal Address."','10005','Registration Processor','RPR_UIN_GEN_SMS','eng',TRUE,'superadmin',now())
, ('1125','Template for update details Email','Template for update details Email','txt','','"Hi $name,
Your UIN details have been updated corresponding to the Registration Number $RID and a Physical Copy of your UIN will reach you soon at your Postal Address.

Thanks and Regards,
MOSIP Team"','10005','Registration Processor','RPR_UIN_UPD_EMAIL','eng',TRUE,'superadmin',now())
, ('1126','Template for update Details SMS','Template for update Details SMS','txt','','"Hi $name,
Your UIN details have been updated corresponding to the Registration Number $RID and a Physical Copy of your UIN will reach you soon at your Postal Address."','10005','Registration Processor','RPR_UIN_UPD_SMS','eng',TRUE,'superadmin',now())
, ('1127','قالب لبريد إلكتروني مكرر الهوية','قالب لبريد إلكتروني مكرر الهوية','txt','','"$name ،
الخاص بك لأنه تم العثور على UIN مقابل بياناتك. يرجى زيارة أقرب مكتب تسجيل $RID فشل طلب التسجيل
وزيارة https://mosip.io/grievances

شكرا مع تحياتي،
فريق MOSIP"','10006','معالج التسجيل','RPR_DUP_UIN_EMAIL','ara',TRUE,'superadmin',now())
, ('1128','قالب لرسالة الهوية المكررة','قالب لرسالة الهوية المكررة','txt','','"$name،
فشل طلب التسجيل $RID الخاص بك لأنه تم العثور على UIN مقابل بياناتك. يرجى زيارة أقرب مكتب تسجيل.
وزيارة https://mosip.io/grievances"','10006','معالج التسجيل','RPR_DUP_UIN_SMS','ara',TRUE,'superadmin',now())
, ('1129','نموذج للبريد الإلكتروني لمشكلة فنية','نموذج للبريد الإلكتروني لمشكلة فنية','txt','','"$name ،
شل طلب التسجيل $RID بسبب مشكلة فنية ، يرجى زيارة أقرب مكتب تسجيل.
وزيارة https://mosip.io/grievances

شكرا مع تحياتي،
فريق MOSIP"','10006','معالج التسجيل','RPR_TEC_ISSUE_EMAIL','ara',TRUE,'superadmin',now())
, ('1130','قالب لرسالة المشكلة الفنية','قالب لرسالة المشكلة الفنية','txt','','"$name،
فشل طلب التسجيل $RID بسبب مشكلة فنية ، يرجى زيارة أقرب مكتب تسجيل.
وزيارة https://mosip.io/grievances"','10006','معالج التسجيل','RPR_TEC_ISSUE_SMS','ara',TRUE,'superadmin',now())
, ('1131','قالب لتوليد الهوية البريد الإلكتروني','قالب لتوليد الهوية البريد الإلكتروني','txt','','"$name،
تم إنشاء UIN الخاص بك للتسجيل $RID بنجاح وستصل قريبًا إلى العنوان البريدي الخاص بك.

شكرا مع تحياتي،
فريق MOSIP"','10006','معالج التسجيل','RPR_UIN_GEN_EMAIL','ara',TRUE,'superadmin',now())
, ('1132','قالب لرسالة توليد الهوية','قالب لرسالة توليد الهوية','txt','','"$name ،
تم إنشاء UIN الخاص بك للتسجيل $RID بنجاح وستصل قريبًا إلى العنوان البريدي الخاص بك."','10006','معالج التسجيل','RPR_UIN_GEN_SMS','ara',TRUE,'superadmin',now())
, ('1133','قالب للحصول على تفاصيل التحديث','قالب للحصول على تفاصيل التحديث','txt','','"$name ،
تم تحديث تفاصيل UIN الخاصة بك المقابلة لرقم التسجيل $RID وستصل إليك نسخة فعلية من UIN الخاصة بك على العنوان البريدي الخاص بك.

شكرا مع تحياتي،
فريق MOSIP"','10006','معالج التسجيل','RPR_UIN_UPD_EMAIL','ara',TRUE,'superadmin',now())
, ('1134','قالب لتحديث تفاصيل الرسالة','قالب لتحديث تفاصيل الرسالة','txt','','"$name ،
تم تحديث تفاصيل UIN الخاصة بك المقابلة لرقم التسجيل $RID وستصل إليك نسخة فعلية من UIN الخاصة بك على العنوان البريدي الخاص بك."','10006','معالج التسجيل','RPR_UIN_UPD_SMS','ara',TRUE,'superadmin',now())
, ('1135','Modèle de courrier didentité en double','Modèle de courrier didentité en double','txt','','"Bonjour $name,
Votre demande d’enregistrement $RID a échoué car un UIN a été trouvé contre vos coordonnées. Veuillez visiter votre bureau d’inscription le plus proche.
Et visitez https://mosip.io/grievances

Merci et salutations,
Équipe MOSIP"','10015','Processeur dinscription','RPR_DUP_UIN_EMAIL','fra',TRUE,'superadmin',now())
, ('1136','Modèle de message didentité en double','Modèle de message didentité en double','txt','','"Bonjour $name,
Votre demande d’enregistrement $RID a échoué car un UIN a été trouvé contre vos coordonnées. Veuillez visiter votre bureau d’inscription le plus proche.
Et visitez https://mosip.io/grievances"','10015','Processeur dinscription','RPR_DUP_UIN_SMS','fra',TRUE,'superadmin',now())
, ('1137','Modèle pour courrier électronique de problème technique','Modèle pour courrier électronique de problème technique','txt','','"Bonjour $name,
Votre demande d’enregistrement $RID a échoué à cause d’un problème technique, veuillez vous rendre au bureau d’inscription le plus proche.
Et visitez https://mosip.io/grievances

Merci et salutations,
Équipe MOSIP"','10015','Processeur dinscription','RPR_TEC_ISSUE_EMAIL','fra',TRUE,'superadmin',now())
, ('1138','Modèle de message de problème technique','Modèle de message de problème technique','txt','','"Bonjour $name,
Votre demande d’enregistrement $RID a échoué à cause d’un problème technique, veuillez vous rendre au bureau d’inscription le plus proche.
Et visitez https://mosip.io/grievances"','10015','Processeur dinscription','RPR_TEC_ISSUE_SMS','fra',TRUE,'superadmin',now())
, ('1139','Modèle de courrier électronique de génération didentité','Modèle de courrier électronique de génération didentité','txt','','"Bonjour $name,
Votre UIN pour l’enregistrement $RID a été généré avec succès et vous parviendra sous peu à votre adresse postale.

Merci et salutations,
Équipe MOSIP"','10015','Processeur dinscription','RPR_UIN_GEN_EMAIL','fra',TRUE,'superadmin',now())
, ('1140','Modèle de message de génération didentité','Modèle de message de génération didentité','txt','','"Bonjour $name,
Votre UIN pour l’enregistrement $RID a été généré avec succès et vous parviendra sous peu à votre adresse postale."','10015','Processeur dinscription','RPR_UIN_GEN_SMS','fra',TRUE,'superadmin',now())
, ('1141','Modèle pour les détails de la mise à jour Email','Modèle pour les détails de la mise à jour Email','txt','','"Bonjour $name,
Les détails de votre UIN correspondant au numéro d’enregistrement $RID ont été mis à jour et une copie physique de votre UIN vous parviendra sous peu à votre adresse postale.

Merci et salutations,
Équipe MOSIP"','10015','Processeur dinscription','RPR_UIN_UPD_EMAIL','fra',TRUE,'superadmin',now())
, ('1142','Modèle pour la mise à jour Détails Message','Modèle pour la mise à jour Détails Message','txt','','"Bonjour $name,
Les détails de votre UIN correspondant au numéro d’enregistrement $RID ont été mis à jour et une copie physique de votre UIN vous parviendra sous peu à votre adresse postale."','10015','Processeur dinscription','RPR_UIN_UPD_SMS','fra',TRUE,'superadmin',now())
, ('1143','Template for new registration Email Content','Template for new registration Email Content','txt','','"Dear $name, 
Thank you for registering with the digital identity platform. Your registration id is $RegistrationID. If there are any corrections to be made in your details, please contact the Registration centre within the next 4 days."','10003','Registration Client','NewReg-email-content-template','eng',TRUE,'superadmin',now())
, ('1144','Template for new registration Email Subject','Template for new registration Email Subject','txt','','Registration confirmation','10003','Registration Client','NewReg-email-subject-template','eng',TRUE,'superadmin',now())
, ('1145','Template for new registration SMS','Template for new registration SMS','txt','','"Dear $name,
Thank you for registering with the digital identity platform. Your registration id is $RegistrationID. If there are any corrections to be made in your details, please contact the Registration centre within the next 4 days. "','10003','Registration Client','NewReg-sms-template','eng',TRUE,'superadmin',now())
, ('1146','Template for OTP generation Email Content','Template for OTP generation Email Content','txt','','"Dear $name, 
OTP for username $username is $otp and is valid for $validTime minutes (Generated on $date at $time hrs)."','10003','Registration Client','OTP-email-content-template','eng',TRUE,'superadmin',now())
, ('1147','Template for OTP generation Email Subject','Template for OTP generation Email Subject','txt','','One time password from digital identify platfor','10003','Registration Client','OTP-email-subject-template','eng',TRUE,'superadmin',now())
, ('1148','Template for OTP SMS','Template for OTP SMS','txt','','"Dear $name, 
OTP for username $username is $otp and is valid for $validTime minutes (Generated on $date at $time hrs)."','10003','Registration Client','OTP-sms-template','eng',TRUE,'superadmin',now())
, ('1149','Template for update registration Email Content','Template for update registration Email Content','txt','','"Dear $name, 
Thank you for updating your details with the digital identity platform. Your registration id is $RegistrationID. If there are any corrections to be made in your details, please contact the Registration centre within the next 4 days."','10003','Registration Client','Update-email-content-template','eng',TRUE,'superadmin',now())
, ('1150','Template for update registration Email Subject','Template for update registration Email Subject','txt','','Registration update confirmation','10003','Registration Client','Update-email-subject-template','eng',TRUE,'superadmin',now())
, ('1151','Template for update registration SMS','Template for update registration SMS','txt','','"Dear $name, 
Thank you for updating your details with the digital identity platform. Your registration id is $RegistrationID. If there are any corrections to be made in your details, please contact the Registration centre within the next 4 days."','10003','Registration Client','Update-sms-template','eng',TRUE,'superadmin',now())
, ('1152','قالب للتسجيل الجديد محتوى البريد الإلكتروني','قالب للتسجيل الجديد محتوى البريد الإلكتروني','txt','','"$name ،
نشكرك على التسجيل في منصة الهوية الرقمية. رقم التسجيل الخاص بك هو $RegistrationID. إذا كان هناك أي تصحيحات يتم إدخالها في تفاصيلك ، يرجى الاتصال بمركز التسجيل في غضون 4 أيام مقبلة."','10004','عميل التسجيل','NewReg-email-content-template','ara',TRUE,'superadmin',now())
, ('1153','قالب للتسجيل الجديد البريد الإلكتروني الموضوع','قالب للتسجيل الجديد البريد الإلكتروني الموضوع','txt','','تأكيد التسجيل','10004','عميل التسجيل','NewReg-email-subject-template','ara',TRUE,'superadmin',now())
, ('1154','قالب لرسالة التسجيل الجديدة','قالب لرسالة التسجيل الجديدة','txt','','"$name ،
نشكرك على التسجيل في منصة الهوية الرقمية. رقم التسجيل الخاص بك هو $RegistrationID. إذا كان هناك أي تصحيحات يتم إدخالها في تفاصيلك ، يرجى الاتصال بمركز التسجيل في غضون 4 أيام مقبلة."','10004','عميل التسجيل','NewReg-sms-template','ara',TRUE,'superadmin',now())
, ('1155','قالب لتوليد OTP محتوى البريد الإلكتروني','قالب لتوليد OTP محتوى البريد الإلكتروني','txt','','"$name ،
OTP لاسم المستخدم $username هو $otp وصالحة لدقائق $validTime (منشأ على $date في $time hrs)."','10004','عميل التسجيل','OTP-email-content-template','ara',TRUE,'superadmin',now())
, ('1156','قالب لتوليد OTP البريد الإلكتروني الموضوع','قالب لتوليد OTP البريد الإلكتروني الموضوع','txt','','كلمة مرور مرة واحدة من منصة تحديد الرقمية','10004','عميل التسجيل','OTP-email-subject-template','ara',TRUE,'superadmin',now())
, ('1157','قالب لرسالة OTP','قالب لرسالة OTP','txt','','"$name ،
OTP لاسم المستخدم $username هو $otp وصالحة لدقائق $validTime (منشأ على $date في $time hrs)."','10004','عميل التسجيل','OTP-sms-template','ara',TRUE,'superadmin',now())
, ('1158','قالب لتحديث تسجيل محتوى البريد الإلكتروني','قالب لتحديث تسجيل محتوى البريد الإلكتروني','txt','','"$name ،
شكرا لتحديث التفاصيل الخاصة بك مع منصة الهوية الرقمية. رقم التسجيل الخاص بك هو $RegistrationID. إذا كان هناك أي تصحيحات يتم إدخالها في تفاصيلك ، يرجى الاتصال بمركز التسجيل في غضون 4 أيام مقبلة"','10004','عميل التسجيل','Update-email-content-template','ara',TRUE,'superadmin',now())
, ('1159','قالب لتسجيل التحديث البريد الإلكتروني الموضوع','قالب لتسجيل التحديث البريد الإلكتروني الموضوع','txt','','تأكيد تحديث التسجيل','10004','عميل التسجيل','Update-email-subject-template','ara',TRUE,'superadmin',now())
, ('1160','قالب لرسالة تسجيل التحديث','قالب لرسالة تسجيل التحديث','txt','','"$name ،
شكرا لتحديث التفاصيل الخاصة بك مع منصة الهوية الرقمية. رقم التسجيل الخاص بك هو $ RegistrationID. إذا كان هناك أي تصحيحات يتم إدخالها في تفاصيلك ، يرجى الاتصال بمركز التسجيل في غضون 4 أيام مقبلة."','10004','عميل التسجيل','Update-sms-template','ara',TRUE,'superadmin',now())
, ('1161','Modèle pour nouvelle inscription Email Content','Modèle pour nouvelle inscription Email Content','txt','','"Cher $name,
Merci de vous être inscrit sur la plateforme d’identité numérique. Votre identifiant d’enregistrement est $RegistrationID. Si des corrections doivent être apportées à vos données, veuillez contacter le centre d’inscription dans les 4 prochains jours."','10014','Client dinscription','NewReg-email-content-template','fra',TRUE,'superadmin',now())
, ('1162','Modèle pour nouvelle inscription Objet de le-mail','Modèle pour nouvelle inscription Objet de le-mail','txt','','Confirmation d’enregistrement','10014','Client dinscription','NewReg-email-subject-template','fra',TRUE,'superadmin',now())
, ('1163','Modèle de nouvelle inscription SMS','Modèle de nouvelle inscription SMS','txt','','"Cher $name,
Merci de vous être inscrit sur la plateforme d’identité numérique. Votre identifiant d’enregistrement est $RegistrationID. Si des corrections doivent être apportées à vos données, veuillez contacter le centre d’inscription dans les 4 prochains jours."','10014','Client dinscription','NewReg-sms-template','fra',TRUE,'superadmin',now())
, ('1164','Modèle de contenu de courrier électronique de génération dOTP','Modèle de contenu de courrier électronique de génération dOTP','txt','','"Cher $name,
OTP pour le nom d’utilisateur $username est $otp et est valide pour $validTime minutes (Généré le $date à $heure hrs)."','10014','Client dinscription','OTP-email-content-template','fra',TRUE,'superadmin',now())
, ('1165','Modèle pour le sujet de-mail de génération dOTP','Modèle pour le sujet de-mail de génération dOTP','txt','','Mot de passe unique de la plateforme d’identification numérique','10014','Client dinscription','OTP-email-subject-template','fra',TRUE,'superadmin',now())
, ('1166','Modèle pour SMS OTP','Modèle pour SMS OTP','txt','','"Cher $name,
OTP pour le nom d’utilisateur $username est $otp et est valide pour $validTime minutes (Généré le $date à $heure hrs)."','10014','Client dinscription','OTP-sms-template','fra',TRUE,'superadmin',now())
, ('1167','Modèle pour lenregistrement de la mise à jour','Modèle pour lenregistrement de la mise à jour','txt','','"Cher $name,
Merci de mettre à jour vos coordonnées avec la plateforme d’identité numérique. Votre identifiant d’enregistrement est $RegistrationID. Si des corrections doivent être apportées à vos données, veuillez contacter le centre d’inscription dans les 4 prochains jours."','10014','Client dinscription','Update-email-content-template','fra',TRUE,'superadmin',now())
, ('1168','Modèle denregistrement de mise à jour Objet de le-mail','Modèle denregistrement de mise à jour Objet de le-mail','txt','','Confirmation de la mise à jour de l’inscription','10014','Client dinscription','Update-email-subject-template','fra',TRUE,'superadmin',now())
, ('1169','Modèle pour SMS denregistrement de mise à jour','Modèle pour SMS denregistrement de mise à jour','txt','','"Merci de mettre à jour vos coordonnées avec la plateforme d’identité numérique. Votre identifiant d’enregistrement est $RegistrationID. Si des corrections doivent être apportées à vos données, veuillez contacter le centre d’inscription dans les 4 prochains jours."','10014','Client dinscription','Update-sms-template','fra',TRUE,'superadmin',now())
, ('1170','Template for Email Acknowledgement','Template for Email Acknowledgement','txt','','"Dear $name,
Your Pre-Registration for UIN is Completed Successfully
on $Date at $Time. Your ID is #$PRID.
Appointment is scheduled for $Appointmentdate at $Appointmenttime.
you will also receive the details on your registered Mobile Number"','10001','Pre-Registration','Email-Acknowledgement','eng',TRUE,'superadmin',now())
, ('1171','Template for Onscreen Acknowledgment','Template for Onscreen Acknowledgment','txt','','"Name                 :$Individual Name
Pre Registration ID    :$PRID
Registration Centre    :$Registration Centre
Centre Contact Number  :$Contact Number
Appointment Date & Time:$Appointment Date and Time

Important Guidelines
1.$Guideline 1
2.$Guideline 2
3.$Guideline 3
4.$Guideline 4
5.$Guideline 5
6.$Guideline 6
7.$Guideline 7
8.$Guideline 8
9.$Guideline 9
10.$Guideline 10
"','10001','Pre-Registration','Onscreen-Acknowledgement','eng',TRUE,'superadmin',now())
, ('1172','Template for OTP Email Content','Template for OTP Email Content','txt','','"Dear $name,
TP for Pre-Registration  $PRID is $otp and is valid for $validTime minutes. (Generated on $date at $time Hrs)"','10001','Pre-Registration','otp-email-content-template','eng',TRUE,'superadmin',now())
, ('1173','Template for OTP Email Subject','Template for OTP Email Subject','txt','','Pre-Registration $PRID: OTP Request','10001','Pre-Registration','otp-email-subject-template','eng',TRUE,'superadmin',now())
, ('1174','Template for OTP SMS','Template for OTP SMS','txt','','OTP for Pre-Registration  $PRID is $otp and is valid for $validTime minutes. (Generated on $date at $time Hrs)','10001','Pre-Registration','otp-sms-template','eng',TRUE,'superadmin',now())
, ('1175','Template for SMS Acknowledgement','Template for SMS Acknowledgement','txt','','"Your Pre-Registration for UIN is Completed Successfully
on $Date at $Time. Your ID is #$PRID.
Appointment is scheduled for $Appointmentdate at $Appointmenttime.
you will also receive the details on your registered email address"','10001','Pre-Registration','SMS-Acknowledgement','eng',TRUE,'superadmin',now())
, ('1176','قالب لتأكيد البريد الإلكتروني','قالب لتأكيد البريد الإلكتروني','txt','','"$name ، 
تم الانتهاء من التسجيل المسبق ل uin بنجاح علي $Date في $Time. رقم التعريف الخاص بك هو # $PRID. ومن المقرر تعيين $Appointmentdate في $Appointmenttime. "','10002','ما قبل التسجيل','Email-Acknowledgement','ara',TRUE,'superadmin',now())
, ('1177','قالب للشاشة شكر وتقدير','قالب للشاشة شكر وتقدير','txt','','"اسم                           : $Individual Name
معرف التسجيل المسبق: $PRID
مركز التسجيل: $Registration Centre
رقم الاتصال بالمركز: $Contact Number
تاريخ ووقت الموعد: $Appointment Date and Time

إرشادات هامه
1. $Guideline 1
2. $Guideline 2
3. $Guideline 3
4. $Guideline 4
5. $Guideline 5
6. $Guideline 6
7. $Guideline 7
8. $Guideline 8
9. $Guideline 9
01. $Guideline 10
"','10002','ما قبل التسجيل','Onscreen-Acknowledgement','ara',TRUE,'superadmin',now())
, ('1178','قالب لمحتوى البريد الإلكتروني OTP','قالب لمحتوى البريد الإلكتروني OTP','txt','','"$name ، 
OTP لـ Pre-Registration $PRID هو $otp وهو صالح لمدة $validTime دقيقة. (التي تم إنشاؤها على $date في $time ساعات)"','10002','ما قبل التسجيل','otp-email-content-template','ara',TRUE,'superadmin',now())
, ('1179','قالب لموضوع البريد الإلكتروني OTP','قالب لموضوع البريد الإلكتروني OTP','txt','','Pre-Registration $PRID: OTP Request','10002','ما قبل التسجيل','otp-email-subject-template','ara',TRUE,'superadmin',now())
, ('1180','قالب ل OTP SMS','قالب ل OTP SMS','txt','','OTP لـ Pre-Registration $PRID هو $otp وهو صالح لمدة $validTime دقيقة. (التي تم إنشاؤها على $date في $time ساعات)','10002','ما قبل التسجيل','otp-sms-template','ara',TRUE,'superadmin',now())
, ('1181','قالب للإشعار SMS','قالب للإشعار SMS','txt','','"
تم الانتهاء من التسجيل المسبق ل uin بنجاح
علي $Date في $Time. رقم التعريف الخاص بك هو # $PRID.
ومن المقرر تعيين $Appointmentdate في $Appointmenttime.
سوف تتلقي أيضا التفاصيل علي عنوان البريد الكتروني المسجل الخاص بك"','10002','ما قبل التسجيل','SMS-Acknowledgement','ara',TRUE,'superadmin',now())
, ('1182','Template for email confirmation','Template for email confirmation','txt','','"Cher $name, 
votre pré-inscription à l’UIN est terminée avec succès sur $Date à $Time. Votre ID est # $PRID.
Le rendez-vous est prévu pour $Appointmentdate à $Appointmenttime.
vous recevrez également les détails sur votre numéro de mobile enregistré"','10013','Pré-inscription','Email-Acknowledgement','fra',TRUE,'superadmin',now())
, ('1183','On-screen recognition template','On-screen recognition template','txt','','"Nom                           : $Individual Name
ID de pré-inscription: $PRID
Centre d’inscription: $Registration Centre
Numéro de contact du Centre: $Contact Number
Date et heure de la nomination: $Appointment Date and Time

Directives importantes
1.$Guideline 1
2.$Guideline 2
3.$Guideline 3
4.$Guideline 4
5.$Guideline 5
6.$Guideline 6
7.$Guideline 7
8.$Guideline 8
9.$Guideline 9
10.$Guideline 10"','10013','Pré-inscription','Onscreen-Acknowledgement','fra',TRUE,'superadmin',now())
, ('1184','OTP Email Content Template','OTP Email Content Template','txt','','"Cher $name, 
OTP pour Pre-Registration $PRID est $otp et est valide pour $validTime minutes. (Généré le $date à $time Hrs)"','10013','Pré-inscription','otp-email-content-template','fra',TRUE,'superadmin',now())
, ('1185','Template for OTP email subject','Template for OTP email subject','txt','','Pre-Registration $PRID: Requête OTP','10013','Pré-inscription','otp-email-subject-template','fra',TRUE,'superadmin',now())
, ('1186','Template for OTP SMS','Template for OTP SMS','txt','','OTP pour Pre-Registration $PRID est $otp et est valide pour $validTime minutes. (Généré le $date à $time Hrs)','10013','Pré-inscription','otp-sms-template','fra',TRUE,'superadmin',now())
, ('1187','Template for SMS Acknowledgment','Template for SMS Acknowledgment','txt','','"Votre pré-inscription pour UIN est terminée avec succès sur $Date à $Time. 
Votre ID est # $PRID.
Le rendez-vous est prévu pour $Appointmentdate à $Appointmenttime.
vous recevrez également les détails sur votre adresse email enregistrée"','10013','Pré-inscription','SMS-Acknowledgement','fra',TRUE,'superadmin',now());
