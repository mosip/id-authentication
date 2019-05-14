import { Injectable } from '@angular/core';

@Injectable()
export class GetContactService {
  private contactNumber: number;
  private userId: string;
  constructor() { }

  setContactNumber(contactNumber: number): void {
    this.contactNumber = contactNumber;

  }

  getContactNumber(): number {
    return this.contactNumber;
  }

  getUserId(): string {
    return this.userId;
  }
  setUserId(userId: string) {
    this.userId = userId;
  }

}
