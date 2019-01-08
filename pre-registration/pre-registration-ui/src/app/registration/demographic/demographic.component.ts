import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm, FormBuilder } from '@angular/forms';
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
import { SharedService } from 'src/app/shared/shared.service';
import * as appConstants from '../../app.constants';
import Utils from 'src/app/app.util';

export interface DropDown {
  locationCode: string;
  locationName: string;
}

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {
  numberPattern = appConstants.NUMBER_PATTERN;
  textPattern = appConstants.TEXT_PATTERN;
  primaryLang = appConstants.PRIMARY_LANG_CODE;
  secondaryLang = appConstants.SECONDARY_LANG_CODE;
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

  uppermostLocationHierarchy: any;

  @ViewChild('dd') dd: ElementRef;
  @ViewChild('mm') mm: ElementRef;
  @ViewChild('yyyy') yyyy: ElementRef;
  @ViewChild('age') age: ElementRef;
  @ViewChild('f') transForm: NgForm;

  selectedRegion = {} as DropDown;
  transSelectedRegion = {} as DropDown;
  selectedProvince = {} as DropDown;
  transSelectedProvince = {} as DropDown;
  selectedCity = {} as DropDown;
  transSelectedCity = {} as DropDown;
  selectedLAA = {} as DropDown;
  transSelectedLAA = {} as DropDown;

  regions: DropDown[] = [];
  provinces: DropDown[] = [];
  cities: DropDown[] = [];
  localAdministrativeAuthorities: DropDown[] = [];
  transRegions: DropDown[] = [
    { locationCode: 'BLR', locationName: '(trans) BLR' },
    { locationCode: 'TN', locationName: '(trans) TN' },
    { locationCode: 'region3', locationName: '(trans) Fez, Meknes and the Middle Atlas' }
  ];
  transProvinces: DropDown[] = [
    { locationCode: 'BLR', locationName: '(trans) BLR' },
    { locationCode: 'TN', locationName: '(trans) TN' },
    { locationCode: 'region3', locationName: '(trans) Fez, Meknes and the Middle Atlas' }
  ];
  transCities: DropDown[] = [
    { locationCode: 'BLR', locationName: '(trans) BLR' },
    { locationCode: 'TN', locationName: '(trans) TN' },
    { locationCode: 'region3', locationName: '(trans) Fez, Meknes and the Middle Atlas' }
  ];
  transLocalAdministrativeAuthorities: DropDown[] = [
    { locationCode: 'BLR', locationName: '(trans) BLR' },
    { locationCode: 'TN', locationName: '(trans) TN' },
    { locationCode: 'region3', locationName: '(trans) Fez, Meknes and the Middle Atlas' }
  ];

  //Need to be removed after translation
  demo = new DemoLabels('', '', 'dd', 'mm', 'yyyy', '', '', '', '', '', '', '', '', '', '', '', '', '');

  demo1 = new DemoLabels(
    't_Full Name',
    't_dob',
    't_dd',
    't_mm',
    't_yyyy',
    't_gender',
    't_Address Line 1',
    't_Address Line 2',
    't_Address Line 3',
    't_Region',
    't_Province',
    't_City',
    't_Postal Code',
    't_Local Administrative Authority',
    't_Email Id',
    't_Mobile Number',
    't_CNE/PIN Number',
    't_Age'
  );

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService
  ) {}

  ngOnInit() {
    if (sessionStorage.getItem('newApplicant') === 'true') {
      this.isNewApplicant = true;
    }
    this.route.parent.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.numberOfApplicants = 1;
    this.initForm();
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
    let mobilePhone = '';
    let pin = '';

    let t_fullName = '';
    let t_dob = '';
    let t_age = '';
    let t_date = '';
    let t_month = '';
    let t_year = '';
    let t_gender = '';
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
      mobilePhone = this.user.request.demographicDetails.identity.mobileNumber[0].value;
      pin = this.user.request.demographicDetails.identity.CNEOrPINNumber[0].value;

      t_fullName = this.user.request.demographicDetails.identity.fullName[1].value;
      t_gender = this.user.request.demographicDetails.identity.gender[1].value;
      t_date = this.user.request.demographicDetails.identity.dateOfBirth[1].value.split('/')[0];
      t_month = this.user.request.demographicDetails.identity.dateOfBirth[1].value.split('/')[1];
      t_year = this.user.request.demographicDetails.identity.dateOfBirth[1].value.split('/')[2];
      t_dob = this.user.request.demographicDetails.identity.dateOfBirth[1].value;
      t_age = this.calculateAge(new Date(new Date(dob))).toString();
      t_addressLine1 = this.user.request.demographicDetails.identity.addressLine1[1].value;
      t_addressLine2 = this.user.request.demographicDetails.identity.addressLine2[1].value;
      t_addressLine3 = this.user.request.demographicDetails.identity.addressLine3[1].value;
    }

    this.userForm = new FormGroup({
      fullName: new FormControl(fullName.trim(), [Validators.required, this.noWhitespaceValidator]),
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
      addressLine2: new FormControl(addressLine2),
      addressLine3: new FormControl(addressLine3),
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
      mobilePhone: new FormControl(mobilePhone, [
        Validators.maxLength(9),
        Validators.minLength(9),
        Validators.pattern(this.numberPattern)
      ]),
      pin: new FormControl(pin, [Validators.maxLength(30), Validators.pattern(this.numberPattern)])
    });

    this.transUserForm = new FormGroup({
      t_fullName: new FormControl(t_fullName.trim(), [Validators.required, this.noWhitespaceValidator]),
      t_gender: new FormControl({ value: t_gender, disabled: true }),
      t_age: new FormControl(t_age),
      t_dob: new FormControl(t_dob),
      t_date: new FormControl(t_date),
      t_month: new FormControl(t_month),
      t_year: new FormControl(t_year),
      t_addressLine1: new FormControl(t_addressLine1, [Validators.required, this.noWhitespaceValidator]),
      t_addressLine2: new FormControl(t_addressLine2),
      t_addressLine3: new FormControl(t_addressLine3)
    });

    this.userForm.valueChanges.subscribe(selectedValue => {
      if (this.userForm.controls['date'].valueChanges) {
        this.transUserForm.controls['t_date'].patchValue(selectedValue.date);
      } else if (this.userForm.controls['month'].valueChanges) {
        this.transUserForm.controls['t_month'].patchValue(selectedValue.month);
      } else if (this.userForm.controls['year'].valueChanges) {
        this.transUserForm.controls['t_year'].patchValue(selectedValue.year);
      } else if (this.userForm.controls['gender'].valueChanges) {
        this.transUserForm.controls['t_gender'].patchValue(selectedValue.gender);
      } else if (this.userForm.controls['age'].valueChanges) {
        this.transUserForm.controls['t_age'].patchValue(selectedValue.age);
      } else if (this.userForm.controls['dob'].valueChanges) {
        this.transUserForm.controls['t_dob'].patchValue(selectedValue.dob);
      }
    });

    await this.getLocationMetadataHirearchy();
    await this.getLocationImmediateHierearchy(this.primaryLang, this.uppermostLocationHierarchy[0].code, this.regions);

    if (this.regService.getUser(this.step) != null) {
      await this.getLocationImmediateHierearchy(
        this.primaryLang,
        this.uppermostLocationHierarchy[0].code,
        this.provinces
      );
      await this.getLocationImmediateHierearchy(this.primaryLang, this.uppermostLocationHierarchy[0].code, this.cities);
      await this.getLocationImmediateHierearchy(
        this.primaryLang,
        this.uppermostLocationHierarchy[0].code,
        this.localAdministrativeAuthorities
      );
      this.viewValueToValue(region, this.regions, 'region');
      this.viewValueToValue(province, this.provinces, 'province');
      this.viewValueToValue(city, this.cities, 'city');
      this.viewValueToValue(
        localAdministrativeAuthority,
        this.localAdministrativeAuthorities,
        'localAdministrativeAuthority'
      );
    }
  }

  viewValueToValue(viewValue: string, entity: DropDown[], controlValue: string) {
    entity.filter(el => {
      if (el.locationName === viewValue) {
        this.userForm.controls[controlValue].patchValue(el.locationCode);
      }
    });
  }

  getLocationMetadataHirearchy() {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationMetadataHirearchy('country').subscribe(
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

  onLocationSelect(
    event: MatSelectChange,
    currentEntity: DropDown[],
    nextEntity: DropDown[],
    selectedEntity: DropDown,
    transCurrentEntity: DropDown[],
    transSelectedEntiy: DropDown
  ) {
    const locationCode = 'IND';
    if (nextEntity) this.getLocationImmediateHierearchy(this.primaryLang, locationCode, nextEntity);
    this.valueToViewValue(event, currentEntity, selectedEntity, transCurrentEntity, transSelectedEntiy);
  }

  getLocationImmediateHierearchy(lang: string, location: string, entity: DropDown[]) {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationImmediateHierearchy(lang, location).subscribe(
        response => {
          response[appConstants.DEMOGRAPHIC_RESPONSE_KEYS.locations].forEach(element => {
            let dropDown: DropDown = {
              locationCode: element.code,
              locationName: element.name
            };
            entity.push(dropDown);
          });
          return resolve(true);
        },
        error => console.log('Unable to fetch Regions')
      );
    });
  }

  valueToViewValue(
    event: MatSelectChange,
    currentEntity: DropDown[],
    selectedEntity: DropDown,
    transCurrentEntity: DropDown[],
    transSelectedEntiy: DropDown
  ) {
    currentEntity.filter(element => {
      if (element.locationCode === event.value) {
        selectedEntity.locationName = element.locationName;
      }
    });

    transCurrentEntity.filter(element => {
      if (element.locationCode === event.value) {
        transSelectedEntiy.locationName = element.locationName;
      }
    });
  }

  onBack() {
    this.router.navigate(['dashboard', this.loginId]);
  }

  onGenderChange() {
    this.userForm.controls['gender'].markAsTouched();
    this.transUserForm.controls['t_gender'].patchValue(this.userForm.controls['gender'].value);
  }

  onAgeChange() {
    const age = this.age.nativeElement.value;
    if (age) {
      const now = new Date();
      const calulatedYear = now.getFullYear() - age;
      this.userForm.controls.date.patchValue('01');
      this.userForm.controls.month.patchValue('01');
      this.userForm.controls.year.patchValue(calulatedYear);
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

  onTransliteration(fromControl: FormControl, toControl) {
    if (fromControl.value) {
      const request: any = {
        from_field_lang: 'English',
        from_field_name: 'Name1',
        from_field_value: fromControl.value,
        to_field_lang: 'Arabic',
        to_field_name: 'Name2',
        to_field_value: ''
      };
      this.transUserForm.controls[toControl.name].patchValue('dummyValue');

      // this.dataStorageService.getTransliteration(request).subscribe(response => {
      //   console.log(response);
      // this.transForm.controls[toControl.name].patchValue(response[appConstants.RESPONSE].to_field_value);
      // });
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
            new UserModel(this.preRegId, request, this.regService.getUserFiles(this.step))
          );
          this.sharedService.updateNameList(this.step, {
            fullName: this.userForm.controls.fullName.value,
            preRegId: this.preRegId
          });
        } else {
          this.preRegId = response[appConstants.RESPONSE][0][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.preRegistrationId];
          this.regService.addUser(new UserModel(this.preRegId, request, []));
          this.sharedService.addNameList({
            fullName: this.userForm.controls.fullName.value,
            preRegId: this.preRegId
          });
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

  private createIdentityJSON() {
    const identity = new IdentityModel(
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.fullName.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_fullName.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.dob.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_dob.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.gender.value),
        new AttributeModel(this.secondaryLang, this.transUserForm.controls.t_gender.value)
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
        new AttributeModel(this.primaryLang, this.selectedRegion.locationName),
        new AttributeModel(this.secondaryLang, this.transSelectedRegion.locationName)
      ],
      [
        new AttributeModel(this.primaryLang, this.selectedProvince.locationName),
        new AttributeModel(this.secondaryLang, this.transSelectedProvince.locationName)
      ],
      [
        new AttributeModel(this.primaryLang, this.selectedCity.locationName),
        new AttributeModel(this.secondaryLang, this.transSelectedCity.locationName)
      ],
      [
        new AttributeModel(this.primaryLang, this.selectedLAA.locationName),
        new AttributeModel(this.secondaryLang, this.transSelectedLAA.locationName)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.postalCode.value),
        new AttributeModel(this.secondaryLang, this.transForm.controls.t_postalCode.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.mobilePhone.value),
        new AttributeModel(this.secondaryLang, this.transForm.controls.t_mobilePhone.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.email.value),
        new AttributeModel(this.secondaryLang, this.transForm.controls.t_email.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.userForm.controls.pin.value),
        new AttributeModel(this.secondaryLang, this.transForm.controls.t_pin.value)
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
