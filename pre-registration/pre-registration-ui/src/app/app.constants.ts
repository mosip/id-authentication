export const LANG_CODE = 'en';
export const NUMBER_PATTERN = '^[0-9]+[0-9]*$';
export const TEXT_PATTERN = '^[a-zA-Z ]*$';
export const COUNTRY_NAME = 'India';
export const NEW_USER_ID = 'mosip.pre-registration.demographic.create';
export const TRANSLITERATION_ID = 'mosip.pre-registration.transliteration.transliterate';
export const VERSION = '1.0';
export const LOCATION_APPEND_URL = 'v1.0/locations/locationhierarchy/country';
export const LOCATION_IMMEDIATE_CHILDREN_APPEND_URL = 'v1.0/locations/immediatechildren/';
export const PARAMS_KEYS = {
  getUsers: 'userId',
  getUser: 'preRegId',
  deleteUser: 'preId',
  locationHierarchyName: 'hierarchyName'
};

// to be removed in future.
export const nameList = [
  {
    fullName: 'Agnitra Banerjee',
    preRegId: '1'
  },
  {
    fullName: 'Shashank Agrawal',
    preRegId: '2'
  },
  {
    fullName: 'Gurdayal Singh Dhillon',
    preRegId: '3'
  },
  {
    fullName: 'Agnitra Banerjee',
    preRegId: '4'
  },
  {
    fullName: 'Shashank Agrawal',
    preRegId: '5'
  },
  {
    fullName: 'Gurdayal Singh Dhillon',
    preRegId: '6'
  }
]
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
