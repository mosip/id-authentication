import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { NameList } from '../registration/demographic/modal/name-list.modal';

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
    return this.nameList.slice();
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
}
