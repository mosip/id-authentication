import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  configs = {};

  public setConfig(configJson: any) {
    this.configs = configJson;
    console.log(this.configs);
  }

  public getConfigByKey(key: string) {
    return this.configs[key];
  }

}
