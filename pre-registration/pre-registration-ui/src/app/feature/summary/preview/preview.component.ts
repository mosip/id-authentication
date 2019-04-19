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
  dateOfBirthPrimary: string = '';
  dateOfBirthSecondary: string = '';
  user: UserModel;
  files = [];
  documentTypes = [];
  documentMapObject = [];

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
    console.log('IN PREVIEW');
    this.primaryLanguage = localStorage.getItem('langCode');
    this.secondaryLanguage = localStorage.getItem('secondaryLangCode');
    this.user = { ...this.registrationService.getUser(this.registrationService.getUsers().length - 1) };
    this.documentTypes = this.registrationService.getDocumentCategories();
    console.log('document types', this.documentTypes);
    console.log(this.user);
    this.previewData = this.user.request.demographicDetails.identity;
    this.calculateAge();
    this.previewData.primaryAddress = this.combineAddress(0);
    this.previewData.secondaryAddress = this.combineAddress(1);
    this.formatDob(this.previewData.dateOfBirth);
    this.setFieldValues();
    this.setResidentStatus();
    console.log(this.previewData);
    this.getSecondaryLanguageLabels();
    this.files = this.user.files[0];
    this.documentsMapping();
  }

  formatDob(dob: string) {
    dob = dob.replace(/\//g, '-');
    this.dateOfBirthPrimary = Utils.getBookingDateTime(dob, '', localStorage.getItem('langCode'));
    this.dateOfBirthSecondary = Utils.getBookingDateTime(dob, '', localStorage.getItem('secondaryLangCode'));
    console.log(this.dateOfBirthPrimary, this.dateOfBirthSecondary);
  }

  setFieldValues() {
    let fields = appConstants.previewFields;
    fields.forEach(field => {
      this.previewData[field].forEach(element => {
        element.name = this.locCodeToName(element.value, element.language);
      });
    });
  }

  setResidentStatus() {
    this.previewData['residenceStatus'].forEach(element => {
      element.name = appConstants.residentTypesMapping[element.value][element.language];
    });
  }

  documentsMapping() {
    this.documentMapObject = [];
    if (this.documentTypes.length !== 0) {
      this.documentTypes.forEach(type => {
        const file = this.files.filter(file => file.docCatCode === type.code);
        if (type.code === 'POA' && file.length === 0 && this.registrationService.getSameAs() !== '') {
          const obj = {
            docName: appConstants.sameAs[localStorage.getItem('langCode')]
          };
          file.push(obj);
        }
        const obj = {
          code: type.code,
          name: type.description,
          fileName: file.length > 0 ? file[0].docName : undefined
        };
        this.documentMapObject.push(obj);
      });
    }
    console.log(this.documentMapObject);
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
    let flag = true;
    this.documentMapObject.forEach(object => {
      if (object.fileName === undefined) {
        if (object.code === 'POA' && this.registrationService.getSameAs() !== '') {
          flag = true;
        } else {
          flag = false;
        }
      }
    });
    return flag;
  }

  navigateDashboard() {
    localStorage.setItem('newApplicant', 'false');
    this.registrationService.setSameAs('');
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
