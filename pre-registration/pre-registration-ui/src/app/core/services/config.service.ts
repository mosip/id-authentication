import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: "root"
})
export class ConfigService {
  isActive;

  configs = {};

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
}
