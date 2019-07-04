import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  navigationType: string;
  configs = {};

  public setConfig(configJson: any) {
    this.configs = configJson.response;
  }

  public getConfigByKey(key: string) {
    return this.configs[key];
  }

  public getConfig() {
    return { ...this.configs };
  }
}
