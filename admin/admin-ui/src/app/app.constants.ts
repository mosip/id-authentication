export const menuItems = [
  'Master Data',
  'Assets',
  'Users',
  'UIN',
  'Configuration'
];

export const appId = 'admin';
export const applicationVersion = 'v1';
export const userIdType = 'USERID';
export const loginOtpContext = 'auth-otp';




export const base_url = 'dev.mosip.io';
export const code_url_mapping = {
  centers: `https://${base_url}/v1/masterdata/registrationcenters`
};

export const loginURL = {
  userRole: `https://${base_url}/v1/admin/security/authfactors/`,
  userIdpasswd: `https://${base_url}/v1/admin/useridPwd`,
  sendOtp: `https://${base_url}/v1/authmanager/authenticate/sendotp`,
  verifyOtp: `https://${base_url}/v1/authmanager/authenticate/useridOTP`
};

