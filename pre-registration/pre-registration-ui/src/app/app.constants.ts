export const NUMBER_PATTERN = '^[0-9]+[0-9]*$';
// export const MOBILE_PATTERN = '^([6-9]{1})([0-9]{9})$';
export const TEXT_PATTERN = '^[a-zA-Z ]*$';
// export const CNIE_PATTERN = '^([0-9]{10,30})$';
export const COUNTRY_NAME = 'Morroco';
export const COUNTRY_HIERARCHY = 'Country';
export const VERSION = '1.0';
export const RESPONSE = 'response';
export const ERROR = 'error';
export const NESTED_ERROR = 'err';
export const ERROR_CODE = 'errorCode';
export const PRE_REGISTRATION_ID = 'pre_registration_id';
// export const ALLOWED_BOOKING_TIME = 24;

export const YEAR_PATTERN = '(\\d{4})';
export const MONTH_PATTERN = '([0]\\d|1[0-2])';
export const DATE_PATTERN = '([0-2]\\d|3[01])';

export const IDS = {
  newUser: 'mosip.pre-registration.demographic.create',
  transliteration: 'mosip.pre-registration.transliteration.transliterate'
};

// export const LANGUAGE_CODE = {
//   primary: 'eng',
//   secondary: 'ara',
//   primaryKeyboardLang: 'en',
//   secondaryKeyboardLang: 'ar'
// };

export const APPEND_URL = {
  config: 'config',
  send_otp: 'sendotp',
  verify_otp: 'useridotp',
  invalid_token: 'invalidatetoken',
  location_metadata: 'v1.0/locations/locationhierarchy/',
  location_immediate_children: 'v1.0/locations/immediatechildren/',
  applicants: 'demographic/applications',
  get_applicant: 'demographic/applications/details',
  location: 'masterdata/',
  gender: 'masterdata/v1.0/gendertypes',
  transliteration: 'transliteration/transliterate',
  applicantType: 'applicanttype/',
  validDocument: 'v1.0/applicanttype/',
  getApplicantType: 'getApplicantType',
  document: 'document/documents',
  document_copy: 'document/documents/copy',
  nearby_registration_centers: 'getcoordinatespecificregistrationcenters/',
  registration_centers_by_name: 'registrationcenters/',
  booking_appointment: 'booking/appointment',
  booking_availability: 'booking/appointment/availability',
  qr_code: 'notification/generateQRCode',
  notification: 'notification/',
  send_notification: 'notify',
  master_data: 'masterdata/v1.0/',
  auth: 'auth/'
};

export const PARAMS_KEYS = {
  getUsers: 'user_id',
  getUser: PRE_REGISTRATION_ID,
  deleteUser: PRE_REGISTRATION_ID,
  locationHierarchyName: 'hierarchyName',
  getDocument: PRE_REGISTRATION_ID,
  getDocumentCategories: 'languages',
  deleteFile: 'documentId',
  getAvailabilityData: 'registration_center_id'
};

export const ERROR_CODES = {
  noApplicantEnrolled: 'PRG_PAM_APP_005'
};

export const CONFIG_KEYS = {
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
  mosip_postal_code_length: 'mosip.id.validation.identity.postalCode.length',
  mosip_CINE_length: 'mosip.id.validation.identity.CNIENumber.length',
  mosip_email_length: 'mosip.id.validation.identity.email.length',
  mosip_mobile_length: 'mosip.id.validation.identity.phone.length',
  preregistration_address_length: 'preregistration.address.length',
  preregistration_fullname_length: 'preregistration.fullname.length'
  // mosip.kernel.sms.number.length
  // preregistration.max.file.size
  // preregistration.workflow.demographic,
  // preregistration.workflow.documentupload,
  // preregistration.workflow.booking
  // preregistration.auto.logout
};

export const DASHBOARD_RESPONSE_KEYS = {
  bookingRegistrationDTO: {
    dto: 'bookingRegistrationDTO',
    regDate: 'appointment_date',
    time_slot_from: 'time_slot_from',
    time_slot_to: 'time_slot_to'
  },
  applicant: {
    preId: 'preRegistrationId',
    fullname: 'fullname',
    statusCode: 'statusCode',
    postalCode: 'postalCode'
  }
};

export const DEMOGRAPHIC_RESPONSE_KEYS = {
  locations: 'locations',
  preRegistrationId: 'preRegistrationId',
  genderTypes: 'genderType'
  // residentTypes: 'residentType'
};

export const APPLICATION_STATUS_CODES = {
  pending: 'Pending_Appointment',
  booked: 'Booked',
  expired: 'Expired'
};

export const DOCUMENT_UPLOAD_REQUEST_DTO = {
  id: 'mosip.pre-registration.document.upload',
  version: '1.0',
  requesttime: '2018-12-28T05:23:08.019Z',
  request: {
    pre_registartion_id: '',
    doc_cat_code: '',
    doc_typ_code: 'address',
    lang_code: 'ENG'
  }
};

export const DOCUMENT_CATEGORY_DTO = {
  attributes: [
    {
      attribute: 'individualTypeCode',
      value: 'FR'
    },
    {
      attribute: 'dateofbirth',
      value: '2012-03-08T11:46:12.640Z'
    },
    {
      attribute: 'genderCode',
      value: 'MLE'
    },
    {
      attribute: 'biometricAvailable',
      value: false
    }
  ],
  id: 'mosip.applicanttype.fetch',
  requestTime: '2012-03-08T11:46:12.640Z',
  version: 'V1.0'
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
  notificationDto: 'NotificationDTO',
  langCode: 'langCode',
  file: 'file'
};

export const residentTypesMapping = {
  NFR: {
    fra: 'Nationale',
    eng: 'National',
    ara: 'الوطني'
  },
  FR: {
    fra: 'Étranger',
    eng: 'Foreigner',
    ara: 'أجنبي'
  }
};

export const sameAs = {
  eng: 'Document Uploaded using Same As',
  fra: 'Document téléchargé avec Identique à',
  ara: 'تم تحميل المستند باستخدام نفس باسم'
};

export const previewFields = ['region', 'province', 'city', 'localAdministrativeAuthority', 'gender'];

export const DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY = 'file';
export const DOCUMENT_UPLOAD_REQUEST_DTO_KEY = 'Document request';

export const PREVIEW_DATA_APPEND_URL = 'demographic/v0.1/applicationData';

export const MONTHS = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
export const DAYS = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
