import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class DataExchangeService {

  currentLoginType: string = "";
  selectedValue: string = "";
  userInfo: object;
  original:string;
  constructor() { }

  getLogin(): object {
    this.userInfo = { value: this.selectedValue, type: this.currentLoginType,original:this.original };
    return this.userInfo;
  }
  setLogin(value,type) {
    this.currentLoginType = type;
    this.original=value;
    this.selectedValue = "XXX" + value.slice(3, value.length);
  }

}
