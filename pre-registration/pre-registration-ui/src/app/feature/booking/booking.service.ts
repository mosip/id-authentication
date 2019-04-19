import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private nameList: NameList[] = [];
  private allApplicants: NameList[] = [];
  private coordinatesSource = new BehaviorSubject(Array);
  currentCoordinates = this.coordinatesSource.asObservable();
  private sendNotification = false;
  private registrationCenterCoordinatesList = new BehaviorSubject(Array);
  coordinatesList = this.registrationCenterCoordinatesList.asObservable();

  constructor() {}

  changeCoordinates(coordinates) {
    this.coordinatesSource.next(coordinates);
  }

  listOfCenters(coordinates) {
    this.registrationCenterCoordinatesList.next(coordinates);
  }

  flushNameList() {
    this.nameList.length = 0;
  }

  getNameList() {
    return [...this.nameList];
  }

  getAllApplicants() {
    return this.allApplicants.slice();
  }

  addApplicants(applicants) {
    this.allApplicants = applicants.response;
  }

  addNameList(nameList: NameList) {
    this.nameList.push(nameList);
  }

  updateNameList(index: number, nameList: NameList) {
    this.nameList[index] = nameList;
  }

  resetNameList() {
    this.nameList = [];
  }

  getIndexByPreId(preId: string): number {
    let index = -1;
    this.nameList.forEach(name => {
      if (name.preRegId === preId) {
        index = this.nameList.indexOf(name);
      }
    });
    return index;
  }

  updateRegistrationCenterData(preId: string, registrationCenter: any) {
    const index = this.getIndexByPreId(preId);
    this.nameList[index].registrationCenter = registrationCenter;
    return this.nameList.slice();
  }

  updateBookingDetails(preId: string, bookingDetails: any) {
    const index = this.getIndexByPreId(preId);
    this.nameList[index].bookingData = bookingDetails;
    return this.nameList.slice();
  }

  getSendNotification() {
    return this.sendNotification;
  }

  resetSendNotification() {
    this.sendNotification = false;
  }

  setSendNotification(flag: boolean) {
    this.sendNotification = flag;
  }
}
