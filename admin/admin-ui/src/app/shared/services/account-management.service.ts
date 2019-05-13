import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class AccountManagementService {

   baseURL = 'https://dev.mosip.io/v1/admin/accountmanagement/';

  constructor(private httpClient: HttpClient) { }
  /*
   * Method to get username from mobile number.
   */
  getUserIdFromMobileNumber(mobileNumber: number) {
    return this.httpClient.get(this.baseURL + '/' + 'unblockaccount?userId=110030');
  }

  /*
   * Method to get username based on user ID.
   */
  forgotUserName(userId: string) {
    return this.httpClient.get(this.baseURL + '?userId=' + userId);

  }

  /*
   * Method to change password.
   */
  changePassword(changePasswordRequest: any) {
    return this.httpClient.post('', changePasswordRequest);
  }

  /*
   * Method to reset password.
   */
  resetPassword(resetPasswordRequest: any) {
    return this.httpClient.post('', resetPasswordRequest);
  }
}
