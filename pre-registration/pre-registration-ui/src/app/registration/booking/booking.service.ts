import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { NameList } from '../demographic/modal/name-list.modal';

@Injectable({
  providedIn: 'root'
})
export class SharedService {
  private nameList: NameList[] = [];
  private coordinatesSource = new BehaviorSubject(Array);
  currentCoordinates = this.coordinatesSource.asObservable();

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

  addNameList(nameList: NameList) {
    this.nameList.push(nameList);
  }

  updateNameList(index: number, nameList: NameList) {
    this.nameList[index] = nameList;
  }

  resetNameList() {
    this.nameList = [];
  }

  getIndexByPreId(preId: string):number {
    let index = -1;
    this.nameList.forEach(name => {
      if (name.preRegId ===  preId) {
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
}
