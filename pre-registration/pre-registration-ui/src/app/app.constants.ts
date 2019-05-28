export const VERSION = '1.0';
export const RESPONSE = 'response';
export const METADATA = 'documentsMetaData';
export const ERROR = 'error';
export const NESTED_ERROR = 'errors';
export const ERROR_CODE = 'errorCode';
export const PRE_REGISTRATION_ID = 'pre_registration_id';
export const APPENDER = '/';

export const IDS = {
  newUser: 'mosip.pre-registration.demographic.create',
  updateUser: 'mosip.pre-registration.demographic.update',
  transliteration: 'mosip.pre-registration.transliteration.transliterate',
  notification: 'mosip.pre-registration.notification.notify',
  cancelAppointment: 'mosip.pre-registration.appointment.cancel',
  booking: 'mosip.pre-registration.booking.book',
  qrCode: 'mosip.pre-registration.qrcode.generate',
  sendOtp: 'mosip.pre-registration.login.sendotp',
  validateOtp: 'mosip.pre-registration.login.useridotp',
  documentUpload: 'mosip.pre-registration.document.upload',
  applicantTypeId: 'mosip.applicanttype.fetch'
};

export const APPEND_URL = {
  config: 'config',
  send_otp: 'sendOtp',
  login: 'validateOtp',
  logout: 'invalidateToken',
  location_metadata: 'locations/locationhierarchy/',
  location_immediate_children: 'locations/immediatechildren/',
  applicants: 'applications',
  location: 'v1/masterdata/',
  gender: 'v1/masterdata/gendertypes',
  resident: 'v1/masterdata/individualtypes',
  transliteration: 'transliteration/transliterate',
  applicantType: 'v1/applicanttype/',
  validDocument: 'applicanttype/',
  getApplicantType: 'getApplicantType',
  post_document: 'documents/',
  document: 'documents/preregistration/',
  document_copy: 'document/documents/copy',
  nearby_registration_centers: 'getcoordinatespecificregistrationcenters/',
  registration_centers_by_name: 'registrationcenters/',
  booking_appointment: 'appointment',
  booking_availability: 'appointment/availability/',
  delete_application: 'applications/',
  qr_code: 'qrCode/generate',
  notification: 'notification/',
  send_notification: 'notify',
  master_data: 'v1/masterdata/',
  auth: 'login/',
  cancelAppointment: 'appointment/'
};

export const PARAMS_KEYS = {
  getUsers: 'user_id',
  getUser: PRE_REGISTRATION_ID,
  deleteUser: PRE_REGISTRATION_ID,
  locationHierarchyName: 'hierarchyName',
  getDocument: PRE_REGISTRATION_ID,
  getDocumentCategories: 'languages',
  preRegistrationId: 'preRegistrationId',
  deleteFile: 'documentId',
  getAvailabilityData: 'registration_center_id',
  catCode: 'catCode',
  sourcePrId: 'sourcePrId',
  POA: 'POA'
};

export const ERROR_CODES = {
  noApplicantEnrolled: 'PRG_PAM_APP_005',
  userBlocked: 'PRG_PAM_LGN_013'
};

export const CONFIG_KEYS = {
  mosip_country_code: 'mosip.country.code',
  preregistration_nearby_centers: 'preregistration.nearby.centers',
  preregistration_timespan_rebook: 'preregistration.timespan.rebook',
  mosip_login_mode: 'mosip.login.mode',
  mosip_regex_email: 'mosip.id.validation.identity.email',
  mosip_regex_phone: 'mosip.id.validation.identity.phone',
  mosip_primary_language: 'mosip.primary-language',
  mosip_secondary_language: 'mosip.secondary-language',
  mosip_left_to_right_orientation: 'mosip.left_to_right_orientation',
  mosip_kernel_otp_expiry_time: 'mosip.kernel.otp.expiry-time',
  mosip_kernel_otp_default_length: 'mosip.kernel.otp.default-length',
  preregistration_recommended_centers_locCode: 'preregistration.recommended.centers.locCode',
  preregistration_availability_noOfDays: 'preregistration.availability.noOfDays',
  mosip_regex_CNIE: 'mosip.id.validation.identity.CNIENumber',
  mosip_regex_postalCode: 'mosip.id.validation.identity.postalCode',
  mosip_regex_DOB: 'mosip.id.validation.identity.dateOfBirth',
  mosip_default_dob_day: 'mosip.default.dob.day',
  mosip_default_dob_month: 'mosip.default.dob.month',
  preregistration_address_length: 'mosip.id.validation.identity.addressLine1.[*].value',
  preregistration_fullname_length: 'mosip.id.validation.identity.fullName.[*].value',
  mosip_id_validation_identity_age: 'mosip.id.validation.identity.age',
  mosip_preregistration_auto_logout_idle: 'mosip.preregistration.auto.logout.idle',
  mosip_preregistration_auto_logout_timeout: 'mosip.preregistration.auto.logout.timeout',
  mosip_preregistration_auto_logout_ping: 'mosip.preregistration.auto.logout.ping',
  preregistration_document_alllowe_files: 'preregistration.documentupload.allowed.file.type',
  preregistration_document_alllowe_file_size: 'preregistration.documentupload.allowed.file.size',
  preregistration_document_alllowe_file_name_lenght: 'preregistration.documentupload.allowed.file.nameLength'
};

export const DASHBOARD_RESPONSE_KEYS = {
  bookingRegistrationDTO: {
    dto: 'bookingRegistrationDTO',
    // new dashboard api
    // dto: 'bookingMetadata',
    regDate: 'appointment_date',
    time_slot_from: 'time_slot_from',
    time_slot_to: 'time_slot_to'
  },
  applicant: {
    preId: 'preRegistrationId',
    fullname: 'fullname',
    statusCode: 'statusCode',
    postalCode: 'postalCode',
    basicDetails: 'basicDetails'
  }
};

export const DEMOGRAPHIC_RESPONSE_KEYS = {
  locations: 'locations',
  preRegistrationId: 'preRegistrationId',
  genderTypes: 'genderType',
  residentTypes: 'individualTypes'
};

export const APPLICATION_STATUS_CODES = {
  pending: 'Pending_Appointment',
  booked: 'Booked',
  expired: 'Expired'
};

export const APPLICANT_TYPE_ATTRIBUTES = {
  individualTypeCode: 'individualTypeCode',
  dateofbirth: 'dateofbirth',
  genderCode: 'genderCode',
  biometricAvailable: 'biometricAvailable'
};

export const virtual_keyboard_languages = {
  eng: 'en',
  fra: 'fr',
  ara: 'ar'
};

export const languageMapping = {
  eng: {
    langName: 'English'
  },
  ara: {
    langName: 'Arabic'
  },
  fra: {
    langName: 'French'
  }
};

export const notificationDtoKeys = {
  notificationDto: 'NotificationRequestDTO',
  langCode: 'langCode',
  file: 'attachment'
};

export const previewFields = [
  'region',
  'province',
  'city',
  'localAdministrativeAuthority',
  'gender',
  'residenceStatus'
];

export const DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY = 'file';
export const DOCUMENT_UPLOAD_REQUEST_DTO_KEY = 'Document request';

export const PREVIEW_DATA_APPEND_URL = 'demographic/v0.1/applicationData';

export const MONTHS = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
