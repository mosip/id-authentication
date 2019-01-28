import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import { MatSelectChange } from '@angular/material';
import { DatePipe } from '@angular/common';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { DemoLabels } from './modal/demographic.labels.modal';
import { IdentityModel } from './modal/identity.modal';
import { AttributeModel } from './modal/attribute.modal';
import { RequestModel } from './modal/request.modal';
import { DemoIdentityModel } from './modal/demo.identity.modal';
import { UserModel } from './modal/user.modal';
import { SharedService } from 'src/app/registration/booking/booking.service';
import { LocationModal } from './modal/location.modal';
import * as appConstants from '../../app.constants';
import Utils from 'src/app/app.util';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {
  textDir = localStorage.getItem('dir');
  secTextDir = localStorage.getItem('secondaryDir');
  // keyboardLang = localStorage.getItem('langCode');
  // keyboardSecondaryLang = localStorage.getItem('secondaryLangCode');
  primaryLang = localStorage.getItem('langCode');
  secondaryLang = localStorage.getItem('secondaryLangCode');
  keyboardLang = appConstants.virtual_keyboard_languages[this.primaryLang];
  keyboardSecondaryLang = appConstants.virtual_keyboard_languages[this.secondaryLang];
  numberPattern = appConstants.NUMBER_PATTERN;
  textPattern = appConstants.TEXT_PATTERN;

  ageOrDobPref = '';
  showDate = false;
  isNewApplicant = false;
  checked = true;
  dataUploadComplete = true;

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
  secondaryLanguage = localStorage.getItem('secondaryLangCode');
  secondaryLanguagelabels: any;
  uppermostLocationHierarchy: any;
  genders: any;
  primaryGender = [];
  secondaryGender = [];
  message = {};

  @ViewChild('dd') dd: ElementRef;
  @ViewChild('mm') mm: ElementRef;
  @ViewChild('yyyy') yyyy: ElementRef;
  @ViewChild('age') age: ElementRef;

  regions: LocationModal[] = [];
  provinces: LocationModal[] = [];
  cities: LocationModal[] = [];
  localAdministrativeAuthorities: LocationModal[] = [];
  transRegions: LocationModal[] = [];
  transProvinces: LocationModal[] = [];
  transCities: LocationModal[] = [];
  transLocalAdministrativeAuthorities: LocationModal[] = [];
  locations: LocationModal[] = [];

  formControlNames = {
    fullName: 'fullNameeee',
    gender: 'gender',
    age: 'age',
    dob: 'dob',
    date: 'date',
    month: 'month',
    year: 'year',
    addressLine1: 'addressLine1',
    addressLine2: 'addressLine2',
    addressLine3: 'addressLine3',
    region: 'region',
    province: 'province',
    city: 'city',
    localAdministrativeAuthority: 'localAdministrativeAuthority',
    email: 'email',
    postalCode: 'postalCode',
    mobilePhone: 'mobilePhone',
    pin: 'pin'
  };

  // location = [this.regions, this.provinces, this.cities, this.localAdministrativeAuthorities];
  // transLocation = [this.transRegions, this.transProvinces, this.transCities, this.transLocalAdministrativeAuthorities];

  //Need to be removed after translation
  // demo = new DemoLabels('', '', 'dd', 'mm', 'yyyy', '', '', '', '', '', '', '', '', '', '', '', '', '');
  // demo1 = new DemoLabels('t_fullName', '', 'dd', 'mm', 'yyyy', '', '', '', '', '', '', '', '', '', '', '', '', '');
  //till here

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService,
    private translate: TranslateService
  ) {
    //need to remove
    // translate.addLangs(['en', 'fr', 'ar']);
    // translate.setDefaultLang(localStorage.getItem('langCode'));
    // const browserLang = translate.getBrowserLang();
    // translate.use(browserLang.match(/en|fr|ar/) ? browserLang : 'en');
    //till here
  }

  ngOnInit() {
    if (localStorage.getItem('newApplicant') === 'true') {
      this.isNewApplicant = true;
    }
    // if (localStorage.getItem('langCode') === 'ar') {
    //   this.primaryLang = 'ara';
    // }
    // if (localStorage.getItem('langCode') === 'ar') {
    //   this.primaryLang = 'ara';
    // }
    this.regService.currentMessage.subscribe(message => (this.message = message));
    if (this.message['modifyUser'] === 'true') {
      this.step = this.regService.getUsers().length - 1;
    } else {
      this.step = this.regService.getUsers().length;
    }
    this.route.parent.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    // this.keyboardLang = appConstants.virtual_keyboard_languages[localStorage.getItem('langCode')];
    this.numberOfApplicants = 1;
    this.initForm();
    this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLanguage).subscribe(response => {
      this.secondaryLanguagelabels = response['demographic'];
    });
  }

  async initForm() {
    let fullName = '';
    let dob = '';
    let age = '';
    let date = '';
    let month = '';
    let year = '';
    let gender = '';
    let addressLine1 = '';
    let addressLine2 = '';
    let addressLine3 = '';
    let region = '';
    let province = '';
    let city = '';
    let localAdministrativeAuthority = '';
    let email = '';
    let postalCode = '';
    let mobileNumber = '';
    let pin = '';

    let t_fullName = '';
    let t_addressLine1 = '';
    let t_addressLine2 = '';
    let t_addressLine3 = '';

    if (this.regService.getUser(this.step) != null) {
      this.user = this.regService.getUser(this.step);

      this.preRegId = this.user.preRegId;
      fullName = this.user.request.demographicDetails.identity.fullName[0].value;
      gender = this.user.request.demographicDetails.identity.gender[0].value;
      date = this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[0];
      month = this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[1];
      year = this.user.request.demographicDetails.identity.dateOfBirth[0].value.split('/')[2];
      dob = this.user.request.demographicDetails.identity.dateOfBirth[0].value;
      age = this.calculateAge(new Date(new Date(dob))).toString();
      addressLine1 = this.user.request.demographicDetails.identity.addressLine1[0].value;
      addressLine2 = this.user.request.demographicDetails.identity.addressLine2[0].value;
      addressLine3 = this.user.request.demographicDetails.identity.addressLine3[0].value;
      region = this.user.request.demographicDetails.identity.region[0].value;
      province = this.user.request.demographicDetails.identity.province[0].value;
      city = this.user.request.demographicDetails.identity.city[0].value;
      localAdministrativeAuthority = this.user.request.demographicDetails.identity.localAdministrativeAuthority[0]
        .value;
      email = this.user.request.demographicDetails.identity.emailId[0].value;
      postalCode = this.user.request.demographicDetails.identity.postalcode[0].value;
      mobileNumber = this.user.request.demographicDetails.identity.mobileNumber[0].value;
      pin = this.user.request.demographicDetails.identity.CNEOrPINNumber[0].value;

      t_fullName = this.user.request.demographicDetails.identity.fullName[1].value;
      t_addressLine1 = this.user.request.demographicDetails.identity.addressLine1[1].value;
      t_addressLine2 = this.user.request.demographicDetails.identity.addressLine2[1].value;
      t_addressLine3 = this.user.request.demographicDetails.identity.addressLine3[1].value;
    }

    this.userForm = new FormGroup({
      fullName: new FormControl(fullName.trim(), [
        Validators.required,
        Validators.maxLength(50),
        this.noWhitespaceValidator
      ]),
      gender: new FormControl(gender, Validators.required),
      age: new FormControl(age, [
        Validators.required,
        Validators.max(150),
        Validators.min(1),
        Validators.pattern(this.numberPattern)
      ]),
      dob: new FormControl(dob),
      date: new FormControl(date, [
        Validators.required,
        Validators.maxLength(2),
        Validators.minLength(2),
        Validators.pattern(this.numberPattern)
      ]),
      month: new FormControl(month, [
        Validators.required,
        Validators.maxLength(2),
        Validators.minLength(2),
        Validators.pattern(this.numberPattern)
      ]),
      year: new FormControl(year, [
        Validators.required,
        Validators.maxLength(4),
        Validators.minLength(4),
        Validators.min(this.maxDate.getFullYear() - 150),
        Validators.pattern(this.numberPattern)
      ]),
      addressLine1: new FormControl(addressLine1, [Validators.required, this.noWhitespaceValidator]),
      addressLine2: new FormControl(addressLine2, Validators.maxLength(50)),
      addressLine3: new FormControl(addressLine3, Validators.maxLength(50)),
      region: new FormControl(region, Validators.required),
      province: new FormControl(province, Validators.required),
      city: new FormControl(city, Validators.required),
      localAdministrativeAuthority: new FormControl(localAdministrativeAuthority, Validators.required),
      email: new FormControl(email, Validators.email),
      postalCode: new FormControl(postalCode, [
        Validators.required,
        Validators.maxLength(5),
        Validators.minLength(5),
        Validators.pattern(this.numberPattern)
      ]),
      mobileNumber: new FormControl(mobileNumber, [
        Validators.maxLength(9),
        Validators.minLength(9),
        Validators.pattern(this.numberPattern)
      ]),
      pin: new FormControl(pin, [Validators.required, Validators.maxLength(30), Validators.pattern(this.numberPattern)])
    });

    this.transUserForm = new FormGroup({
      t_fullName: new FormControl(t_fullName.trim(), [Validators.required, this.noWhitespaceValidator]),
      t_addressLine1: new FormControl(t_addressLine1, [Validators.required, this.noWhitespaceValidator]),
      t_addressLine2: new FormControl(t_addressLine2),
      t_addressLine3: new FormControl(t_addressLine3)
    });

    await this.getLocationMetadataHirearchy();
    await this.getLocationImmediateHierearchy(
      this.primaryLang,
      this.uppermostLocationHierarchy[0].code,
      this.regions,
      region
    );
    await this.getLocationImmediateHierearchy(
      this.secondaryLang,
      this.uppermostLocationHierarchy[0].code,
      this.transRegions,
      region
    );

    await this.getGenderDetails();
    this.filterGenderOnLangCode(this.primaryLang, this.primaryGender);
    this.filterGenderOnLangCode(this.secondaryLang, this.secondaryGender);
    if (this.regService.getUser(this.step) != null) {
      await this.getLocationImmediateHierearchy(this.primaryLang, region, this.provinces, province);
      await this.getLocationImmediateHierearchy(this.secondaryLang, region, this.transProvinces, province);
      await this.getLocationImmediateHierearchy(this.primaryLang, province, this.cities, city);
      await this.getLocationImmediateHierearchy(this.secondaryLang, province, this.transCities, city);
      await this.getLocationImmediateHierearchy(
        this.primaryLang,
        city,
        this.localAdministrativeAuthorities,
        localAdministrativeAuthority
      );
      await this.getLocationImmediateHierearchy(
        this.secondaryLang,
        city,
        this.transLocalAdministrativeAuthorities,
        localAdministrativeAuthority
      );
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
    nextEntity: LocationModal[],
    transNextEntity: LocationModal[],
    parentLocation: LocationModal[]
  ) {
    const locationCode = event.value;
    const locationName = event.source.triggerValue;
    if (nextEntity) this.getLocationImmediateHierearchy(this.primaryLang, locationCode, nextEntity);
    if (transNextEntity) {
      this.getLocationImmediateHierearchy(this.secondaryLang, locationCode, transNextEntity);
    }
    let location = {} as LocationModal;
    location.locationCode = locationCode;
    location.locationName = locationName;
    location.languageCode = this.primaryLang;
    this.locations.push(location);

    if (parentLocation) {
      let loc = {} as LocationModal;
      parentLocation.filter(ele => {
        if (ele.locationCode === event.value) {
          loc = ele;
        }
      });
      this.locations.push(loc);
    }
  }

  getLocationImmediateHierearchy(lang: string, location: string, entity: LocationModal[], parentLocation?: string) {
    entity.length = 0;
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationImmediateHierearchy(lang, location).subscribe(
        response => {
          response[appConstants.DEMOGRAPHIC_RESPONSE_KEYS.locations].forEach(element => {
            let locationModal: LocationModal = {
              locationCode: element.code,
              locationName: element.name,
              languageCode: lang
            };
            entity.push(locationModal);
            if (parentLocation && locationModal.locationCode === parentLocation) {
              this.locations.push(locationModal);
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

  onGenderChange() {
    this.userForm.controls['gender'].markAsTouched();
  }

  onAgeChange() {
    const age = this.age.nativeElement.value;
    if (age) {
      const now = new Date();
      const calulatedYear = now.getFullYear() - age;
      this.userForm.controls.date.patchValue('01');
      this.userForm.controls.month.patchValue('01');
      this.userForm.controls.year.patchValue(calulatedYear);
      this.userForm.controls.dob.patchValue('01/01/' + calulatedYear);
      this.userForm.controls['dob'].setErrors(null);
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
        this.userForm.controls.dob.patchValue(myFormattedDate);
        this.userForm.controls.age.patchValue(this.calculateAge(dateform));
      } else {
        this.userForm.controls['dob'].markAsTouched();
        this.userForm.controls['dob'].setErrors({ incorrect: true });
        this.userForm.controls.age.patchValue('');
      }
    }
  }

  private calculateAge(bDay: Date) {
    const now = new Date();
    const born = new Date(bDay);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));

    if (this.regService.getUser(this.step) != null) {
      return years;
    }
    if (years > 150) {
      this.userForm.controls['dob'].markAsTouched();
      this.userForm.controls['dob'].setErrors({ incorrect: true });
      this.userForm.controls['year'].setErrors(null);
      return '';
    } else {
      this.userForm.controls['dob'].markAsUntouched();
      this.userForm.controls['dob'].setErrors(null);
      this.userForm.controls['year'].setErrors(null);
      return years;
    }
  }

  onTransliteration(fromControl: FormControl, toControl: any) {
    if (fromControl.value) {
      const request: any = {
        from_field_lang: 'English',
        from_field_name: toControl.name,
        from_field_value: fromControl.value,
        to_field_lang: 'Arabic',
        to_field_name: toControl.name,
        to_field_value: ''
      };
      // this.transUserForm.controls[toControl.name].patchValue('dummyValue');
      this.dataStorageService.getTransliteration(request).subscribe(response => {
        this.transUserForm.controls[toControl.name].patchValue(response[appConstants.RESPONSE].to_field_value);
      });
    } else {
      this.transUserForm.controls[toControl.name].patchValue('');
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
        if (this.regService.getUser(this.step) != null) {
          this.regService.updateUser(
            this.step,
            new UserModel(this.preRegId, request, this.regService.getUserFiles(this.step), this.locations)
          );
          this.sharedService.updateNameList(this.step, {
            fullName: this.userForm.controls.fullName.value,
            preRegId: this.preRegId
          });
        } else if (response !== null) {
          this.preRegId = response[appConstants.RESPONSE][0][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.preRegistrationId];
          this.regService.addUser(new UserModel(this.preRegId, request, [], this.locations));
          this.sharedService.addNameList({
            fullName: this.userForm.controls.fullName.value,
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

  private createAttributeArray(element: string) {
    let attr: AttributeModel[] = [];
    const strArray = [this.primaryLang, this.secondaryLang];

    for (let index = 0; index < strArray.length; index++) {
      const lang = strArray[index];
      let value = '';
      if (index === 0) {
        value = this.userForm.controls[element].value;
      } else if (index === 1) {
        value = this.transUserForm.controls['t_' + element].value;
      }
      attr.push(new AttributeModel(lang, value));
    }
    return attr;
  }

  private createIdentityJSONDynamic() {
    const obj = {};

    let keyArr: any[] = Object.values(this.formControlNames);
    console.log(keyArr);

    keyArr.forEach(element => {
      obj[element] = this.createAttributeArray(element);
    });
    // obj[keyArr[0]] = this.createAttributeArray(keyArr[0]);
    console.log('OBJ', obj);
  }

  private createIdentityJSON() {
    const identity = new IdentityModel(
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.fullName.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_fullName.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.dob.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.dob.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.gender.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.gender.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.addressLine1.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_addressLine1.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.addressLine2.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_addressLine2.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.addressLine3.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_addressLine3.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.region.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.region.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.province.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.province.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.city.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.city.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.localAdministrativeAuthority.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.localAdministrativeAuthority.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.postalCode.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.postalCode.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.mobileNumber.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.mobileNumber.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.email.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.email.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.pin.value),
        new AttributeModel(this.secondaryLang, this.userForm.controls.pin.value)
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
