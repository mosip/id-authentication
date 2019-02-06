import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm, FormControlName } from '@angular/forms';
import { MatSelectChange, MatButtonToggleChange } from '@angular/material';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';

import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { SharedService } from '../../booking/booking.service';
import { RegistrationService } from 'src/app/core/services/registration.service';

import { UserModel } from 'src/app/shared/models/demographic-model/user.modal';
import { CodeValueModal } from 'src/app/shared/models/demographic-model/code.value.modal';
import { FormControlModal } from 'src/app/shared/models/demographic-model/form.control.modal';
import { IdentityModel } from 'src/app/shared/models/demographic-model/identity.modal';
import { DemoIdentityModel } from 'src/app/shared/models/demographic-model/demo.identity.modal';
import { RequestModel } from 'src/app/shared/models/demographic-model/request.modal';
import AttributeModel from 'src/app/shared/models/demographic-model';
import * as appConstants from '../../../app.constants';
import Utils from 'src/app/app.util';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit, OnDestroy {
  message$ = new Observable();
  // messageSubscription: Subscription;
  textDir = localStorage.getItem('dir');
  secTextDir = localStorage.getItem('secondaryDir');
  primaryLang = localStorage.getItem('langCode');
  secondaryLang = localStorage.getItem('secondaryLangCode');
  languages = [this.primaryLang, this.secondaryLang];
  keyboardLang = appConstants.virtual_keyboard_languages[this.primaryLang];
  keyboardSecondaryLang = appConstants.virtual_keyboard_languages[this.secondaryLang];
  numberPattern = appConstants.NUMBER_PATTERN;
  textPattern = appConstants.TEXT_PATTERN;

  ageOrDobPref = '';
  showDate = false;
  isNewApplicant = false;
  checked = true;
  dataUploadComplete = true;
  dataModification: boolean;

  step: number = 0;
  id: number;
  numberOfApplicants: number;
  userForm: FormGroup;
  transUserForm: FormGroup;
  maxDate = new Date(Date.now());
  preRegId = '';
  loginId = '';
  user: UserModel;
  demodata: string[];
  secondaryLanguagelabels: any;
  uppermostLocationHierarchy: any;
  primaryGender = [];
  secondaryGender = [];
  genders: any;
  message = {};

  @ViewChild('dd') dd: ElementRef;
  @ViewChild('mm') mm: ElementRef;
  @ViewChild('yyyy') yyyy: ElementRef;
  @ViewChild('age') age: ElementRef;

  regions1: CodeValueModal[] = [];
  regions2: CodeValueModal[] = [];
  regions: CodeValueModal[][] = [this.regions1, this.regions2];
  provinces1: CodeValueModal[] = [];
  provinces2: CodeValueModal[] = [];
  provinces: CodeValueModal[][] = [this.provinces1, this.provinces2];
  cities1: CodeValueModal[] = [];
  cities2: CodeValueModal[] = [];
  cities: CodeValueModal[][] = [this.cities1, this.cities2];
  localAdministrativeAuthorities1: CodeValueModal[] = [];
  localAdministrativeAuthorities2: CodeValueModal[] = [];
  localAdministrativeAuthorities: CodeValueModal[][] = [
    this.localAdministrativeAuthorities1,
    this.localAdministrativeAuthorities2
  ];
  locations = [this.regions, this.provinces, this.cities, this.localAdministrativeAuthorities];
  selectedLocationCode = [];
  codeValue: CodeValueModal[] = [];

  formControlValues: FormControlModal;
  formControlNames: FormControlModal = {
    fullName: 'fullNamee',
    gender: 'gendere',
    dateOfBirth: 'dobe',
    addressLine1: 'addressLine1e',
    addressLine2: 'addressLine2e',
    addressLine3: 'addressLine3e',
    region: 'regione',
    province: 'provincee',
    city: 'citye',
    localAdministrativeAuthority: 'localAdministrativeAuthoritye',
    email: 'emaile',
    postalCode: 'postalCodee',
    phone: 'mobilePhonee',
    CNIENumber: 'pine',

    age: 'agee',
    date: 'datee',
    month: 'monthe',
    year: 'yeare',
    fullNameSecondary: 'secondaryFullName',
    addressLine1Secondary: 'secondaryAddressLine1',
    addressLine2Secondary: 'secondaryAddressLine2',
    addressLine3Secondary: 'secondaryAddressLine3'
  };

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    this.initialization();
  }

  ngOnInit() {
    this.initForm();
    this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLang).subscribe(response => {
      this.secondaryLanguagelabels = response['demographic'];
    });
  }

  private initialization() {
    if (localStorage.getItem('newApplicant') === 'true') {
      this.isNewApplicant = true;
    }
    this.regService.currentMessage.subscribe(message => (this.message = message));
    // this.message$ = this.regService.currentMessage;
    // this.message$.subscribe(message => (this.message = message));
    if (this.message['modifyUser'] === 'true') {
      this.dataModification = true;
      this.step = this.regService.getUsers().length - 1;
    } else {
      this.dataModification = false;
      this.step = this.regService.getUsers().length;
    }
    const arr = this.router.url.split('/');
    console.log(arr);
    this.loginId = arr[2];
  }

  async initForm() {
    if (this.dataModification) {
      this.user = this.regService.getUser(this.step);
      this.preRegId = this.user.preRegId;
    }
    this.setFormControlValues();

    this.userForm = new FormGroup({
      [this.formControlNames.fullName]: new FormControl(this.formControlValues.fullName.trim(), [
        Validators.required,
        Validators.maxLength(50),
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.gender]: new FormControl(this.formControlValues.gender, Validators.required),
      [this.formControlNames.age]: new FormControl(this.formControlValues.age, [
        Validators.required,
        Validators.max(150),
        Validators.min(1),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.dateOfBirth]: new FormControl(this.formControlValues.dateOfBirth),
      [this.formControlNames.date]: new FormControl(this.formControlValues.date, [
        Validators.required,
        Validators.maxLength(2),
        Validators.minLength(2),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.month]: new FormControl(this.formControlValues.month, [
        Validators.required,
        Validators.maxLength(2),
        Validators.minLength(2),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.year]: new FormControl(this.formControlValues.year, [
        Validators.required,
        Validators.maxLength(4),
        Validators.minLength(4),
        Validators.min(this.maxDate.getFullYear() - 150),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.addressLine1]: new FormControl(this.formControlValues.addressLine1, [
        Validators.required,
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.addressLine2]: new FormControl(
        this.formControlValues.addressLine2,
        Validators.maxLength(50)
      ),
      [this.formControlNames.addressLine3]: new FormControl(
        this.formControlValues.addressLine3,
        Validators.maxLength(50)
      ),
      [this.formControlNames.region]: new FormControl(this.formControlValues.region, Validators.required),
      [this.formControlNames.province]: new FormControl(this.formControlValues.province, Validators.required),
      [this.formControlNames.city]: new FormControl(this.formControlValues.city, Validators.required),
      [this.formControlNames.localAdministrativeAuthority]: new FormControl(
        this.formControlValues.localAdministrativeAuthority,
        Validators.required
      ),
      [this.formControlNames.email]: new FormControl(this.formControlValues.email, Validators.email),
      [this.formControlNames.postalCode]: new FormControl(this.formControlValues.postalCode, [
        Validators.required,
        Validators.maxLength(6),
        Validators.minLength(6),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.phone]: new FormControl(this.formControlValues.phone, [
        Validators.maxLength(10),
        Validators.minLength(10),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.CNIENumber]: new FormControl(this.formControlValues.CNIENumber, [
        Validators.required,
        Validators.maxLength(30),
        Validators.pattern(this.numberPattern)
      ])
    });

    this.transUserForm = new FormGroup({
      [this.formControlNames.fullNameSecondary]: new FormControl(this.formControlValues.fullNameSecondary.trim(), [
        Validators.required,
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.addressLine1Secondary]: new FormControl(this.formControlValues.addressLine1Secondary, [
        Validators.required,
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.addressLine2Secondary]: new FormControl(this.formControlValues.addressLine2Secondary),
      [this.formControlNames.addressLine3Secondary]: new FormControl(this.formControlValues.addressLine3Secondary)
    });

    this.setLocations();
    this.setGender();
  }

  private async setLocations() {
    await this.getLocationMetadataHirearchy();

    this.selectedLocationCode = [
      this.uppermostLocationHierarchy[0].code,
      this.formControlValues.region,
      this.formControlValues.province,
      this.formControlValues.city,
      this.formControlValues.localAdministrativeAuthority
    ];
    if (!this.dataModification) {
      this.locations = [this.regions];
    }

    for (let index = 0; index < this.locations.length; index++) {
      const parentLocationCode = this.selectedLocationCode[index];
      const currentLocationCode = this.selectedLocationCode[index + 1];
      const elements = this.locations[index];
      for (let elementsIndex = 0; elementsIndex < elements.length; elementsIndex++) {
        const element = elements[elementsIndex];
        const language = this.languages[elementsIndex];
        await this.getLocationImmediateHierearchy(language, parentLocationCode, element, currentLocationCode);
      }
    }
  }

  private async setGender() {
    await this.getGenderDetails();
    this.filterGenderOnLangCode(this.primaryLang, this.primaryGender);
    this.filterGenderOnLangCode(this.secondaryLang, this.secondaryGender);
  }

  private setFormControlValues() {
    if (!this.dataModification) {
      this.formControlValues = {
        fullName: '',
        gender: '',
        date: '',
        month: '',
        year: '',
        dateOfBirth: '',
        age: '',
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        region: '',
        province: '',
        city: '',
        localAdministrativeAuthority: '',
        email: '',
        postalCode: '',
        phone: '',
        CNIENumber: '',

        fullNameSecondary: '',
        addressLine1Secondary: '',
        addressLine2Secondary: '',
        addressLine3Secondary: ''
      };
    } else {
      const dob = this.user.request.demographicDetails.identity.dateOfBirth;
      console.log(dob);
      console.log(this.user);

      this.formControlValues = {
        fullName: this.user.request.demographicDetails.identity.fullName[0].value,
        gender: this.user.request.demographicDetails.identity.gender[0].value,
        date: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[2],
        month: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[1],
        year: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[0],
        dateOfBirth: dob,
        age: this.calculateAge(new Date(new Date(dob))).toString(),
        addressLine1: this.user.request.demographicDetails.identity.addressLine1[0].value,
        addressLine2: this.user.request.demographicDetails.identity.addressLine2[0].value,
        addressLine3: this.user.request.demographicDetails.identity.addressLine3[0].value,
        region: this.user.request.demographicDetails.identity.region[0].value,
        province: this.user.request.demographicDetails.identity.province[0].value,
        city: this.user.request.demographicDetails.identity.city[0].value,
        localAdministrativeAuthority: this.user.request.demographicDetails.identity.localAdministrativeAuthority[0]
          .value,
        email: this.user.request.demographicDetails.identity.email,
        postalCode: this.user.request.demographicDetails.identity.postalCode,
        phone: this.user.request.demographicDetails.identity.phone,
        CNIENumber: this.user.request.demographicDetails.identity.CNIENumber.toString(),

        fullNameSecondary: this.user.request.demographicDetails.identity.fullName[1].value,
        addressLine1Secondary: this.user.request.demographicDetails.identity.addressLine1[1].value,
        addressLine2Secondary: this.user.request.demographicDetails.identity.addressLine2[1].value,
        addressLine3Secondary: this.user.request.demographicDetails.identity.addressLine3[1].value
      };
    }
  }

  getGenderDetails() {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getGenderDetails().subscribe(response => {
        this.genders = response[appConstants.DEMOGRAPHIC_RESPONSE_KEYS.genderTypes];
        resolve(true);
      });
    });
  }

  private filterGenderOnLangCode(langCode: string, genderEntity = []) {
    this.genders.filter((element: any) => {
      if (element.langCode === langCode) genderEntity.push(element);
    });
    if (this.formControlValues.gender) {
      genderEntity.filter(element => {
        if (element.code === this.formControlValues.gender) {
          const codeValue: CodeValueModal = {
            valueCode: element.code,
            valueName: element.genderName,
            languageCode: element.langCode
          };
          this.addCodeValue(codeValue);
        }
      });
    }
  }

  getLocationMetadataHirearchy() {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationMetadataHirearchy(appConstants.COUNTRY_HIERARCHY).subscribe(
        response => {
          const countryHirearchy = response[appConstants.DEMOGRAPHIC_RESPONSE_KEYS.locations];
          const uppermostLocationHierarchy = countryHirearchy.filter(
            (element: any) => element.name === appConstants.COUNTRY_NAME
          );
          this.uppermostLocationHierarchy = uppermostLocationHierarchy;
          resolve(this.uppermostLocationHierarchy);
        },
        error => console.log('Error in fetching location Hierarchy')
      );
    });
  }

  async onLocationSelect(
    event: MatSelectChange,
    nextHierarchies: CodeValueModal[][],
    currentLocationHierarchies: CodeValueModal[][]
  ) {
    // const locationCode = event.value;
    // const locationName = event.source.triggerValue;
    if (nextHierarchies) {
      for (let index = 0; index < nextHierarchies.length; index++) {
        const element = nextHierarchies[index];
        const languageCode = this.languages[index];
        this.getLocationImmediateHierearchy(languageCode, event.value, element);
      }
    }

    if (currentLocationHierarchies) {
      for (let index = 0; index < currentLocationHierarchies.length; index++) {
        const currentLocationHierarchy = currentLocationHierarchies[index];
        currentLocationHierarchy.filter(currentLocationHierarchy => {
          if (currentLocationHierarchy.valueCode === event.value) {
            this.addCodeValue(currentLocationHierarchy);
          }
        });
      }
    }
  }

  private addCodeValue(element: CodeValueModal) {
    this.codeValue.push({
      valueCode: element.valueCode,
      valueName: element.valueName,
      languageCode: element.languageCode
    });
  }

  getLocationImmediateHierearchy(
    languageCode: string,
    parentLocationCode: string,
    childLocations: CodeValueModal[],
    currentLocationCode?: string
  ) {
    childLocations.length = 0;
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationImmediateHierearchy(languageCode, parentLocationCode).subscribe(
        response => {
          response[appConstants.DEMOGRAPHIC_RESPONSE_KEYS.locations].forEach(element => {
            let codeValueModal: CodeValueModal = {
              valueCode: element.code,
              valueName: element.name,
              languageCode: languageCode
            };
            childLocations.push(codeValueModal);
            if (currentLocationCode && codeValueModal.valueCode === currentLocationCode) {
              this.codeValue.push(codeValueModal);
            }
          });
          return resolve(true);
        },
        error => console.log('Unable to fetch Below Hierearchy')
      );
    });
  }

  onBack() {
    this.router.navigate(['dashboard', this.loginId]);
  }

  onGenderChange(genderEntity: any, event?: MatButtonToggleChange) {
    if (event) {
      genderEntity.forEach(element => {
        element.filter(element => {
          if (event.value === element.code) {
            const codeValue: CodeValueModal = {
              languageCode: element.langCode,
              valueCode: element.code,
              valueName: element.genderName
            };
            this.addCodeValue(codeValue);
          }
        });
      });
    }
    this.userForm.controls[this.formControlNames.gender].markAsTouched();
  }

  onAgeChange() {
    const age = this.age.nativeElement.value;
    if (age) {
      const now = new Date();
      const calulatedYear = now.getFullYear() - age;
      this.userForm.controls[this.formControlNames.date].patchValue('01');
      this.userForm.controls[this.formControlNames.month].patchValue('01');
      this.userForm.controls[this.formControlNames.year].patchValue(calulatedYear);
      this.userForm.controls[this.formControlNames.dateOfBirth].patchValue('01/01/' + calulatedYear);
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors(null);
    }
  }

  onDOBChange() {
    const date = this.dd.nativeElement.value;
    const month = this.mm.nativeElement.value;
    const year = this.yyyy.nativeElement.value;
    if (date !== '' && month !== '' && year !== '') {
      const newDate = month + '/' + date + '/' + year;
      const dateform = new Date(newDate);
      const _month = dateform.getMonth() + 1;
      if (dateform.toDateString() !== 'Invalid Date' && (+month === _month || month === '0' + _month)) {
        const pipe = new DatePipe('en-US');
        const myFormattedDate = pipe.transform(dateform, 'yyyy/MM/dd');
        this.userForm.controls[this.formControlNames.dateOfBirth].patchValue(myFormattedDate);
        this.userForm.controls[this.formControlNames.age].patchValue(this.calculateAge(dateform));
      } else {
        this.userForm.controls[this.formControlNames.dateOfBirth].markAsTouched();
        this.userForm.controls[this.formControlNames.dateOfBirth].setErrors({ incorrect: true });
        this.userForm.controls[this.formControlNames.age].patchValue('');
      }
    }
  }

  private calculateAge(bDay: Date) {
    const now = new Date();
    const born = new Date(bDay);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));

    if (this.dataModification) {
      return years;
    }
    if (years > 150) {
      this.userForm.controls[this.formControlNames.dateOfBirth].markAsTouched();
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors({ incorrect: true });
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      return '';
    } else {
      this.userForm.controls[this.formControlNames.dateOfBirth].markAsUntouched();
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors(null);
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      return years;
    }
  }

  onTransliteration(fromControl: FormControl, toControl: any) {
    // from   -   To
    // eng - ara - T
    // ara - eng - T
    // eng - fre - F
    // fre - eng - F
    // ara - fre - T
    // fre - ara - T
    if (fromControl.value) {
      const request: any = {
        from_field_lang: this.primaryLang,
        from_field_name: toControl,
        from_field_value: fromControl.value,
        to_field_lang: this.secondaryLang,
        to_field_name: toControl,
        to_field_value: ''
      };

      // this.transUserForm.controls[toControl].patchValue('dummyValue');
      this.dataStorageService.getTransliteration(request).subscribe(response => {
        this.transUserForm.controls[toControl].patchValue(response[appConstants.RESPONSE].to_field_value);
      });
    } else {
      this.transUserForm.controls[toControl].patchValue('');
    }
  }

  private noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { whitespace: true };
  }

  onSubmit() {
    const request = this.createRequestJSON();
    this.dataUploadComplete = false;
    this.dataStorageService.addUser(request).subscribe(
      response => {
        console.log(response);

        if (this.dataModification) {
          this.regService.updateUser(
            this.step,
            new UserModel(this.preRegId, request, this.regService.getUserFiles(this.step), this.codeValue)
          );
          this.sharedService.updateNameList(this.step, {
            fullName: this.userForm.controls[this.formControlNames.fullName].value,
            fullNameSecondaryLang: this.formControlValues.fullNameSecondary,
            preRegId: this.preRegId
          });
        } else if (response !== null) {
          console.log(response);

          this.preRegId = response[appConstants.RESPONSE][0][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.preRegistrationId];
          this.regService.addUser(new UserModel(this.preRegId, request, [], this.codeValue));
          this.sharedService.addNameList({
            fullName: this.userForm.controls[this.formControlNames.fullName].value,
            fullNameSecondaryLang: this.formControlValues.fullNameSecondary,
            preRegId: this.preRegId
          });
        } else {
          console.log('Response is null');
          this.router.navigate(['error']);
        }
      },
      error => {
        console.log(error);
        this.router.navigate(['error']);
      },
      () => {
        this.checked = true;
        this.dataUploadComplete = true;
        const url = Utils.getURL(this.router.url, 'file-upload');
        console.log(this.regService.getUsers());

        this.router.navigateByUrl(url);
      }
    );
  }

  private createAttributeArray(element: string, identity: IdentityModel) {
    console.log(element, typeof identity[element]);
    let attr: any;
    if (typeof identity[element] === 'object') {
      let forms = [];
      let formControlNames = [];
      const transliterateField = ['fullName', 'addressLine1', 'addressLine2', 'addressLine3'];
      if (transliterateField.includes(element)) {
        forms = ['userForm', 'transUserForm'];
        formControlNames = [element, element + 'Secondary'];
      } else {
        forms = ['userForm', 'userForm'];
        formControlNames = [element, element];
      }
      attr = [];
      for (let index = 0; index < this.languages.length; index++) {
        const languageCode = this.languages[index];
        const form = forms[index];
        const controlName = formControlNames[index];
        attr.push(new AttributeModel(languageCode, this[form].controls[this.formControlNames[controlName]].value));
      }
    } else if (typeof identity[element] === 'string') {
      attr = this.userForm.controls[this.formControlNames[element]].value;
    } else if (typeof identity[element] === 'number') {
      identity[element] = attr = +this.userForm.controls[this.formControlNames[element]].value;
    }
    identity[element] = attr;
  }

  private createIdentityJSONDynamic() {
    const identity = new IdentityModel(1, [], '', [], [], [], [], [], [], [], [], '', '', '', 0);
    let keyArr: any[] = Object.keys(this.formControlNames);
    for (let index = 0; index < keyArr.length - 8; index++) {
      const element = keyArr[index];
      this.createAttributeArray(element, identity);
    }
    return identity;
  }

  // private createIdentityJSON() {
  //   const identity = new IdentityModel(
  //     1.0,
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.fullName].value),
  //       new AttributeModel(
  //         this.secondaryLang,
  //         this.transUserForm.controls[this.formControlNames.fullNameSecondary].value
  //       )
  //     ],
  //     this.userForm.controls[this.formControlNames.dateOfBirth].value,
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.gender].value),
  //       new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.gender].value)
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine1].value),
  //       new AttributeModel(
  //         this.secondaryLang,
  //         this.transUserForm.controls[this.formControlNames.addressLine1Secondary].value
  //       )
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine2].value),
  //       new AttributeModel(
  //         this.secondaryLang,
  //         this.transUserForm.controls[this.formControlNames.addressLine2Secondary].value
  //       )
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine3].value),
  //       new AttributeModel(
  //         this.secondaryLang,
  //         this.transUserForm.controls[this.formControlNames.addressLine3Secondary].value
  //       )
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.region].value),
  //       new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.region].value)
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.province].value),
  //       new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.province].value)
  //     ],
  //     [
  //       new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.city].value),
  //       new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.city].value)
  //     ],
  //     [
  //       new AttributeModel(
  //         this.primaryLang,
  //         this.userForm.controls[this.formControlNames.localAdministrativeAuthority].value
  //       ),
  //       new AttributeModel(
  //         this.secondaryLang,
  //         this.userForm.controls[this.formControlNames.localAdministrativeAuthority].value
  //       )
  //     ],
  //     this.userForm.controls[this.formControlNames.postalCode].value,
  //     this.userForm.controls[this.formControlNames.phone].value,
  //     this.userForm.controls[this.formControlNames.email].value,
  //     +this.userForm.controls[this.formControlNames.CNIENumber].value
  //   );

  //   return identity;
  // }

  private createRequestJSON() {
    const identity = this.createIdentityJSONDynamic();

    let preRegistrationId = '';
    let createdBy = this.loginId;
    let createdDateTime = Utils.getCurrentDate();
    let updatedBy = '';
    let updatedDateTime = '';
    let langCode = this.primaryLang;
    if (this.user) {
      preRegistrationId = this.user.preRegId;
      createdBy = this.user.request.createdBy;
      createdDateTime = this.user.request.createdDateTime;
      updatedBy = this.loginId;
      updatedDateTime = Utils.getCurrentDate();
      langCode = this.user.request.langCode;
    }
    const req: RequestModel = {
      preRegistrationId: preRegistrationId,
      createdBy: createdBy,
      createdDateTime: createdDateTime,
      updatedBy: updatedBy,
      updatedDateTime: updatedDateTime,
      langCode: langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }

  ngOnDestroy() {
    // this.message$
  }
}
