export const NUMBER_PATTERN = '^[0-9]+[0-9]*$';
export const TEXT_PATTERN = '^[a-zA-Z ]*$';
export const COUNTRY_NAME = 'India';
export const VERSION = '1.0';
export const RESPONSE = 'response';
export const ERROR = 'error';
export const NESTED_ERROR = 'err';
export const ERROR_CODE = 'errorCode';

export const IDS = {
  newUser: 'mosip.pre-registration.demographic.create',
  transliteration: 'mosip.pre-registration.transliteration.transliterate'
};

export const LANGUAGE_CODE = {
  primary: 'ENG',
  secondary: 'arb'
};

export const APPEND_URL = {
  LOCATION_METADATA: 'v1.0/locations/locationhierarchy/country',
  LOCATION_IMMEDIATE_CHILDREN: 'v1.0/locations/immediatechildren/',
  GET_APPLICANT: 'demographic/v0.1/pre-registration/applicationData',
  APPLICANTS: 'demographic/v0.1/pre-registration/applications',
  LOCATION: 'masterdata/'
};

export const PARAMS_KEYS = {
  getUsers: 'userId',
  getUser: 'preRegId',
  deleteUser: 'preId',
  locationHierarchyName: 'hierarchyName'
};

export const ERROR_CODES = {
  noApplicantEnrolled: 'PRG_PAM_APP_005'
};

export const DASHBOARD_RESPONSE_KEYS = {
  bookingRegistrationDTO: {
    dto: 'bookingRegistrationDTO',
    regDate: 'reg_date',
    time_slot_from: 'time_slot_from',
    time_slot_to: 'time_slot_to'
  },
  applicant: {
    preId: 'preId',
    fullname: 'fullname',
    statusCode: 'statusCode'
  }
};

export const DEMOGRAPHIC_RESPONSE_KEYS = {
  locations: 'locations',
  preRegistrationId: 'preRegistrationId'
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

export const DOCUMENT_UPLOAD_REQUEST_DOCUMENT_KEY = 'file';
export const DOCUMENT_UPLOAD_REQUEST_DTO_KEY = 'Document request DTO';
