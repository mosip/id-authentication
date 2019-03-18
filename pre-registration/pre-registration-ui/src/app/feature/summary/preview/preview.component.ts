import { Component, OnInit } from '@angular/core';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { Router, ActivatedRoute } from '@angular/router';
import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { TranslateService } from '@ngx-translate/core';
import Utils from 'src/app/app.util';
import * as appConstants from '../../../app.constants';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css']
})
export class PreviewComponent implements OnInit {
  previewData: any;
  secondaryLanguagelabels: any;
  primaryLanguage;
  secondaryLanguage;
  user: UserModel;
  files = [];

  constructor(
    private dataStorageService: DataStorageService,
    private route: ActivatedRoute,
    private router: Router,
    private registrationService: RegistrationService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    localStorage.setItem('modifyDocument', 'false');
  }

  ngOnInit() {
    this.primaryLanguage = localStorage.getItem('langCode');
    this.secondaryLanguage = localStorage.getItem('secondaryLangCode');
    this.user = { ...this.registrationService.getUser(this.registrationService.getUsers().length - 1) };
    console.log(this.user);
    this.previewData = this.user.request.demographicDetails.identity;
    this.calculateAge();
    this.previewData.primaryAddress = this.combineAddress(0);
    this.previewData.secondaryAddress = this.combineAddress(1);
    this.setFieldValues();
    console.log(this.previewData);
    this.getSecondaryLanguageLabels();
    this.files = this.user.files[0];
  }

  setFieldValues() {
    let fields = appConstants.previewFields;
    fields.forEach(field => {
      this.previewData[field].forEach(element => {
          element.name = this.locCodeToName(
          element.value,
          element.language
        )
      })
    })
  }

  combineAddress(index: number) {
    const address =
      this.previewData.addressLine1[index].value +
      (this.previewData.addressLine2[index].value ? ', ' + this.previewData.addressLine2[index].value : '') +
      (this.previewData.addressLine3[index].value ? ', ' + this.previewData.addressLine3[index].value : '');
    return address;
  }

  getSecondaryLanguageLabels() {
    this.dataStorageService
      .getSecondaryLanguageLabels(localStorage.getItem('secondaryLangCode'))
      .subscribe(response => {
        this.secondaryLanguagelabels = response['preview'];
        console.log(this.secondaryLanguagelabels);
      });
  }

  calculateAge() {
    const now = new Date();
    const born = new Date(this.previewData.dateOfBirth);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));
    this.previewData.age = years;
  }

  modifyDemographic() {
    const url = Utils.getURL(this.router.url, 'demographic', 2);
    this.registrationService.changeMessage({ modifyUserFromPreview: 'true' });
    this.router.navigateByUrl(url);
  }

  modifyDocument() {
    localStorage.setItem('modifyDocument', 'true');
    this.navigateBack();
  }

  private locCodeToName(locationCode: string, language: string): string {
    const locations = this.user.location;
    const locationName = locations.filter(
      location => location.languageCode === language && location.valueCode === locationCode
    );
    return locationName[0].valueName;
  }

  enableContinue(): boolean {
    let flag = false;
    if (this.files.length === 4) {
      flag = true;
    } else if (this.files.length === 3 && this.registrationService.getSameAs() !== '') {
      flag = true;
    } else {
      flag = false;
    }
    return flag;
  }

  navigateDashboard() {
    localStorage.setItem('newApplicant', 'false');
    this.registrationService.changeMessage({ modifyUserFromPreview: 'false' });
    const url = Utils.getURL(this.router.url, 'demographic', 2);
    this.router.navigateByUrl(url);
  }

  navigateBack() {
    const url = Utils.getURL(this.router.url, 'file-upload', 2);
    this.router.navigateByUrl(url);
  }

  navigateNext() {
    const url = Utils.getURL(this.router.url, 'booking/pick-center', 2);
    this.router.navigateByUrl(url);
  }
}
