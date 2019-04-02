import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppConfigService {
  private appConfig: any;

  constructor(private http: HttpClient) {}

  async loadAppConfig() {
    const data = await this.http.get('./assets/config.json').toPromise();
    console.log(data);
    this.appConfig = data;
  }

  getConfig() {
    return this.appConfig;
  }
}
