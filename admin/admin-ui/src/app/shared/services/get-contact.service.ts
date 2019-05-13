import { Injectable } from '@angular/core';

@Injectable()
export class GetContactService {
  contactNumber: number;

  constructor() { }

  setContactNumber(contactNumber: number): void {
    this.contactNumber = contactNumber;

  }

  getContactNumber(): number {
    return this.contactNumber;
  }

}
