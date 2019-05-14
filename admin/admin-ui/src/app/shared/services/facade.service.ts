import { Injectable, Injector } from '@angular/core';
import { AccountManagementService } from './account-management.service';
import { GetContactService } from './get-contact.service';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class FacadeService {

  constructor(private injector: Injector) { }

  private getAccountManagementService: AccountManagementService;
  private getContactService: GetContactService;

  public get contactService(): GetContactService {
    if (!this.getContactService) {
      this.getContactService = this.injector.get(GetContactService);
    }
    return this.getContactService;
  }

  public get accountManagementService(): AccountManagementService {
    if (!this.getAccountManagementService) {
      this.getAccountManagementService = this.injector.get(AccountManagementService);
    }
    return this.getAccountManagementService;
  }

  getContact() {
    return this.contactService.getContactNumber();
  }

  setContact(contactNumber: number) {
    this.contactService.setContactNumber(contactNumber);
  }

  getUserID() {
    return this.contactService.getUserId();
  }

  setUserID(userID: string) {
    this.contactService.setUserId(userID);
  }

  getUserNameFromPhoneNumber(phoneNumber: number): Observable<any> {
    return this.accountManagementService.getUserNameFromPhoneNumber(phoneNumber);
  }

  sendOTP(sendOTPRequest: any): Observable <any> {
    return this.accountManagementService.sendOTP(sendOTPRequest);
  }

  validateOTP(validateOTPRequest: any): Observable <any> {
    return this.accountManagementService.validateOTP(validateOTPRequest);
  }

  resetPassword(resetPasswordRequest: any): Observable <any> {
    return this.accountManagementService.resetPassword(resetPasswordRequest);
  }
}
