import { Injectable } from '@angular/core';
import {  BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  isActive

  configs = {};
  private messageAutoLogout = new BehaviorSubject({});
  currentMessageAutoLogout = this.messageAutoLogout.asObservable();

  public setConfig(configJson: any) {
    this.configs = configJson.response;
    console.log(this.configs);
  }

  public getConfigByKey(key: string) {
    return this.configs[key];
  }

  public getConfig() {
    return { ...this.configs };
  }
  changeMessage(message : object){
    this.messageAutoLogout.next(message);
  }

}
