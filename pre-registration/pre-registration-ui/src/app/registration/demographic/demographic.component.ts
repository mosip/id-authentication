import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm, FormControlName } from '@angular/forms';
import { MatSelectChange, MatButtonToggleChange } from '@angular/material';
import { DatePipe } from '@angular/common';
import { TranslateService } from '@ngx-translate/core';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { IdentityModel } from './modal/identity.modal';
import { AttributeModel } from './modal/attribute.modal';
import { RequestModel } from './modal/request.modal';
import { DemoIdentityModel } from './modal/demo.identity.modal';
import { UserModel } from './modal/user.modal';
import { SharedService } from 'src/app/registration/booking/booking.service';
import { CodeValueModal } from './modal/code.value.modal';
import * as appConstants from '../../app.constants';
import Utils from 'src/app/app.util';
import { FormControlModal } from './modal/form.control.modal';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {
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
    dob: 'dobe',
    addressLine1: 'addressLine1e',
    addressLine2: 'addressLine2e',
    addressLine3: 'addressLine3e',
    region: 'regione',
    province: 'provincee',
    city: 'citye',
    localAdministrativeAuthority: 'localAdministrativeAuthoritye',
    email: 'emaile',
    postalCode: 'postalCodee',
    mobilePhone: 'mobilePhonee',
    pin: 'pine',

    age: 'agee',
    date: 'datee',
    month: 'monthe',
    year: 'yeare',
    secondaryFullName: 'secondaryFullName',
    secondaryAddressLine1: 'secondaryAddressLine1',
    secondaryAddressLine2: 'secondaryAddressLine2',
    secondaryAddressLine3: 'secondaryAddressLine3'
  };

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    console.log('demo comp called');
    //need to remove
    // translate.addLangs(['eng', 'fra', 'ara']);
    // translate.setDefaultLang(localStorage.getItem('langCode'));
    // const browserLang = translate.getBrowserLang();
    // translate.use(browserLang.match(/eng|fra|ara/) ? browserLang : 'eng');
    //till here
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
    if (this.message['modifyUser'] === 'true') {
      this.dataModification = true;
      this.step = this.regService.getUsers().length - 1;
    } else {
      this.dataModification = false;
      this.step = this.regService.getUsers().length;
    }
    this.route.parent.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
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
      [this.formControlNames.dob]: new FormControl(this.formControlValues.dob),
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
        Validators.maxLength(5),
        Validators.minLength(5),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.mobilePhone]: new FormControl(this.formControlValues.mobilePhone, [
        Validators.maxLength(9),
        Validators.minLength(9),
        Validators.pattern(this.numberPattern)
      ]),
      [this.formControlNames.pin]: new FormControl(this.formControlValues.pin, [
        Validators.required,
        Validators.maxLength(30),
        Validators.pattern(this.numberPattern)
      ])
    });

    this.transUserForm = new FormGroup({
      [this.formControlNames.secondaryFullName]: new FormControl(this.formControlValues.secondaryFullName.trim(), [
        Validators.required,
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.secondaryAddressLine1]: new FormControl(this.formControlValues.secondaryAddressLine1, [
        Validators.required,
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.secondaryAddressLine2]: new FormControl(this.formControlValues.secondaryAddressLine2),
      [this.formControlNames.secondaryAddressLine3]: new FormControl(this.formControlValues.secondaryAddressLine3)
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
        dob: '',
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
        mobilePhone: '',
        pin: '',

        secondaryFullName: '',
        secondaryAddressLine1: '',
        secondaryAddressLine2: '',
        secondaryAddressLine3: ''
      };
    } else {
      const dob = this.user.request.demographicDetails.identity.dateOfBirth[0].value;
      this.formControlValues = {
        fullName: this.user.request.demographicDetails.identity.fullName[0].value,
        gender: this.user.request.demographicDetails.identity.gender[0].value,
        date: this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[0],
        month: this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[1],
        year: this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[2],
        dob: dob,
        age: this.calculateAge(new Date(new Date(dob))).toString(),
        addressLine1: this.user.request.demographicDetails.identity.addressLine1[0].value,
        addressLine2: this.user.request.demographicDetails.identity.addressLine2[0].value,
        addressLine3: this.user.request.demographicDetails.identity.addressLine3[0].value,
        region: this.user.request.demographicDetails.identity.region[0].value,
        province: this.user.request.demographicDetails.identity.province[0].value,
        city: this.user.request.demographicDetails.identity.city[0].value,
        localAdministrativeAuthority: this.user.request.demographicDetails.identity.localAdministrativeAuthority[0]
          .value,
        email: this.user.request.demographicDetails.identity.emailId[0].value,
        postalCode: this.user.request.demographicDetails.identity.postalcode[0].value,
        mobilePhone: this.user.request.demographicDetails.identity.mobileNumber[0].value,
        pin: this.user.request.demographicDetails.identity.CNEOrPINNumber[0].value,

        secondaryFullName: this.user.request.demographicDetails.identity.fullName[1].value,
        secondaryAddressLine1: this.user.request.demographicDetails.identity.addressLine1[1].value,
        secondaryAddressLine2: this.user.request.demographicDetails.identity.addressLine2[1].value,
        secondaryAddressLine3: this.user.request.demographicDetails.identity.addressLine3[1].value
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
            const codeValue: CodeValueModal = {
              languageCode: element.langCode,
              valueCode: element.code,
              valueName: element.genderName
              };
              this.addCodeValue(codeValue);               
            // this.addCodeValue(element);
            // // this.codeValue.push({
            // //   languageCode: element.langCode,
            // //   valueName: element.genderName,
            // //   valueCode: element.code
            // // });
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
      this.userForm.controls[this.formControlNames.dob].patchValue('01/01/' + calulatedYear);
      this.userForm.controls[this.formControlNames.dob].setErrors(null);
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
        const myFormattedDate = pipe.transform(dateform, 'dd/MM/yyyy');
        this.userForm.controls[this.formControlNames.dob].patchValue(myFormattedDate);
        this.userForm.controls[this.formControlNames.age].patchValue(this.calculateAge(dateform));
      } else {
        this.userForm.controls[this.formControlNames.dob].markAsTouched();
        this.userForm.controls[this.formControlNames.dob].setErrors({ incorrect: true });
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
      this.userForm.controls[this.formControlNames.dob].markAsTouched();
      this.userForm.controls[this.formControlNames.dob].setErrors({ incorrect: true });
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      return '';
    } else {
      this.userForm.controls[this.formControlNames.dob].markAsUntouched();
      this.userForm.controls[this.formControlNames.dob].setErrors(null);
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      return years;
    }
  }

  onTransliteration(fromControl: FormControl, toControl: any) {
    if (fromControl.value) {
      const request: any = {
        from_field_lang: 'English',
        from_field_name: toControl,
        from_field_value: fromControl.value,
        to_field_lang: 'Arabic',
        to_field_name: toControl,
        to_field_value: ''
      };

      // this.transUserForm.controls[toControl.name].patchValue('dummyValue');
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
    // this.createIdentityJSONDynamic();
    const request = this.createRequestJSON();
    this.dataUploadComplete = false;
    this.dataStorageService.addUser(request).subscribe(
      response => {
        if (this.dataModification) {
          this.regService.updateUser(
            this.step,
            new UserModel(this.preRegId, request, this.regService.getUserFiles(this.step), this.codeValue)
          );
          this.sharedService.updateNameList(this.step, {
            fullName: this.userForm.controls[this.formControlNames.fullName].value,
            preRegId: this.preRegId
          });
        } else if (response !== null) {
          this.preRegId = response[appConstants.RESPONSE][0][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.preRegistrationId];
          this.regService.addUser(new UserModel(this.preRegId, request, [], this.codeValue));
          this.sharedService.addNameList({
            fullName: this.userForm.controls[this.formControlNames.fullName].value,
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
        this.router.navigate(['../file-upload'], { relativeTo: this.route });
      }
    );
  }

  private createAttributeArray(element: string, identity: IdentityModel) {
    let attr: AttributeModel[] = [];
    console.log(element);

    if (identity[element] instanceof Object) {
      console.log('yes');
    }
    for (let index = 0; index < this.languages.length; index++) {
      const languageCode = this.languages[index];
      attr.push(new AttributeModel(languageCode, this.userForm.controls[this.formControlNames[element]].value));
    }
    console.log(attr);
    identity[element] = attr;

    return attr;
  }

  private createIdentityJSONDynamic() {
    const identity = new IdentityModel([], [], [], [], [], [], [], [], [], [], [], [], [], []);
    let keyArr: any[] = Object.keys(this.formControlNames);
    console.log(keyArr);

    // keyArr[0]
    // this.createAttributeArray(keyArr[0], identity);
    for (let index = 0; index < keyArr.length - 8; index++) {
      const element = keyArr[index];
      console.log(element);

      this.createAttributeArray(element, identity);
    }

    console.log(identity);

    // obj[keyArr[0]] = this.createAttributeArray(keyArr[0]);
    // console.log('OBJ', obj);
  }

  private createIdentityJSON() {
    const identity = new IdentityModel(
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.fullName].value),
        new AttributeModel(
          this.secondaryLang,
          this.transUserForm.controls[this.formControlNames.secondaryFullName].value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.dob].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.dob].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.gender].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.gender].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine1].value),
        new AttributeModel(
          this.secondaryLang,
          this.transUserForm.controls[this.formControlNames.secondaryAddressLine1].value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine2].value),
        new AttributeModel(
          this.secondaryLang,
          this.transUserForm.controls[this.formControlNames.secondaryAddressLine2].value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.addressLine3].value),
        new AttributeModel(
          this.secondaryLang,
          this.transUserForm.controls[this.formControlNames.secondaryAddressLine3].value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.region].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.region].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.province].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.province].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.city].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.city].value)
      ],
      [
        new AttributeModel(
          this.primaryLang,
          this.userForm.controls[this.formControlNames.localAdministrativeAuthority].value
        ),
        new AttributeModel(
          this.secondaryLang,
          this.userForm.controls[this.formControlNames.localAdministrativeAuthority].value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.postalCode].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.postalCode].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.mobilePhone].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.mobilePhone].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.email].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.email].value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls[this.formControlNames.pin].value),
        new AttributeModel(this.secondaryLang, this.userForm.controls[this.formControlNames.pin].value)
      ]
    );

    return identity;
  }

  private createRequestJSON() {
    const identity = this.createIdentityJSON();

    let preRegistrationId = '';
    let createdBy = this.loginId;
    let createdDateTime = Utils.getCurrentDate();
    let updatedBy = '';
    let updatedDateTime = '';
    let statusCode = appConstants.APPLICATION_STATUS_CODES.pending;
    let langCode = this.primaryLang;
    if (this.user) {
      preRegistrationId = this.user.preRegId;
      createdBy = this.user.request.createdBy;
      createdDateTime = this.user.request.createdDateTime;
      updatedBy = this.loginId;
      updatedDateTime = Utils.getCurrentDate();
      statusCode = this.user.request.statusCode;
      langCode = this.user.request.langCode;
    }
    const req: RequestModel = {
      preRegistrationId: preRegistrationId,
      createdBy: createdBy,
      createdDateTime: createdDateTime,
      updatedBy: updatedBy,
      updatedDateTime: updatedDateTime,
      statusCode: statusCode,
      langCode: langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }
}
