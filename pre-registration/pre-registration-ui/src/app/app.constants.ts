export const NUMBER_PATTERN = '^[0-9]+[0-9]*$';
export const TEXT_PATTERN = '^[a-zA-Z ]*$';
export const COUNTRY_NAME = 'Morroco';
export const COUNTRY_HIERARCHY = 'Country';
export const VERSION = '1.0';
export const RESPONSE = 'response';
export const ERROR = 'error';
export const NESTED_ERROR = 'err';
export const ERROR_CODE = 'errorCode';
export const PRE_REGISTRATION_ID = 'pre_registration_id';

export const IDS = {
  newUser: 'mosip.pre-registration.demographic.create',
  transliteration: 'mosip.pre-registration.transliteration.transliterate'
};

export const LANGUAGE_CODE = {
  primary: 'eng',
  secondary: 'ara',
  primaryKeyboardLang: 'en',
  secondaryKeyboardLang: 'ar'
};

export const APPEND_URL = {
  location_metadata: 'v1.0/locations/locationhierarchy/',
  location_immediate_children: 'v1.0/locations/immediatechildren/',
  get_applicant: 'demographic/v0.1/pre-registration/applicationData',
  applicants: 'demographic/v0.1/pre-registration/applications',
  location: 'masterdata/',
  gender: 'masterdata/v1.0/gendertypes',
  transliteration: 'transliterate/v0.1/pre-registration/translitrate'
};

export const PARAMS_KEYS = {
  getUsers: 'user_id',
  getUser: PRE_REGISTRATION_ID,
  deleteUser: PRE_REGISTRATION_ID,
  locationHierarchyName: 'hierarchyName'
};

export const ERROR_CODES = {
  noApplicantEnrolled: 'PRG_PAM_APP_005'
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
    statusCode: 'statusCode'
  }
};

export const DEMOGRAPHIC_RESPONSE_KEYS = {
  locations: 'locations',
  preRegistrationId: 'preRegistrationId',
  genderTypes: 'genderType'
};

export const APPLICATION_STATUS_CODES = {
  pending: 'Pending_Appointment',
  booked: 'Booked',
  expired: 'Expired'
};

export const DOCUMENT_UPLOAD_REQUEST_DTO = {
  id: 'mosip.pre-registration.document.upload',
  ver: '1.0',
  reqTime: '2018-12-28T05:23:08.019Z',
  request: {
    pre_registartion_id: '86710482195706',
    doc_cat_code: 'POA',
    doc_typ_code: 'address',
    lang_code: 'ENG',
    doc_file_format: 'pdf',
    status_code: 'Pending-Appoinment',
    upload_by: '9900806086',
    upload_date_time: '2018-12-28T05:23:08.019Z'
  }
};

export const virtual_keyboard_languages = {
  eng: 'en',
  fra: 'fr',
  ara: 'ar'
};

export const DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY = 'file';
export const DOCUMENT_UPLOAD_REQUEST_DTO_KEY = 'Document request';

export const PREVIEW_DATA_APPEND_URL = 'demographic/v0.1/pre-registration/applicationData';
// "BASE_URL": "https://dev.mosip.io/"
