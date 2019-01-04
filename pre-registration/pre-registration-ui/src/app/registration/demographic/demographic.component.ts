import { Component, OnInit, ViewChild, AfterViewChecked, ElementRef } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm, AbstractControl } from '@angular/forms';
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
import Utils from 'src/app/app.util';
import * as appConstants from '../../app.constants';

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
  user: UserModel;
  id: number;
  step = 0;
  demo = new DemoLabels(
    'Full Name',
    'dob',
    'dd',
    'mm',
    'yyyy',
    'gender',
    'Address Line 1',
    'Address Line 2',
    'Address Line 3',
    'Region',
    'Province',
    'City',
    'Postal Code',
    'Local Administrative Authority',
    'Email Id',
    'Mobile Number',
    'CNE/PIN Number',
    'Age'
  );

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
  numberPattern = appConstants.NUMBER_PATTERN;
  textPattern = appConstants.TEXT_PATTERN;
  primaryLang = 'en';
  primaryLangCode = 'ENG';
  secondaryLang = 'fr';
  ageOrDobPref = '';
  showCalender: boolean;
  showDate = false;
  numberOfApplicants: number;
  userForm: FormGroup;
  transUserForm: FormGroup;
  numbers: number[];
  checked = true;
  maxDate = new Date(Date.now());
  preRegId = '';
  loginId = '';
  dataUploadComplete = true;

  uppermostLocationHierarchy;
  regions: DropDown[] = [
    { locationCode: 'region1', locationName: 'Tangier, Tetouan and the northwest' },
    { locationCode: 'region2', locationName: 'The Mediterranean coast and the Rif' },
    { locationCode: 'region3', locationName: 'Fez, Meknes and the Middle Atlas' }
  ];
  transRegions: DropDown[] = [
    { locationCode: 'region1', locationName: '(trans) Tangier, Tetouan and the northwest' },
    { locationCode: 'region2', locationName: '(trans) The Mediterranean coast and the Rif' },
    { locationCode: 'region3', locationName: '(trans) Fez, Meknes and the Middle Atlas' }
  ];
  provinces: DropDown[] = [
    //   { locationCode: 'province1', locationName: 'Fahs-Anjra' },
    //   { locationCode: 'province2', locationName: 'Tétouan' },
    //   { locationCode: 'province3', locationName: 'Al Hoceïma' }
  ];
  cities: DropDown[] = [
    // { locationCode: 'city1', locationName: 'Anjra' },
    // { locationCode: 'city2', locationName: 'Jouamaa' },
    // { locationCode: 'city3', locationName: 'Ksar El Majaz' }
  ];
  localAdministrativeAuthorities: DropDown[] = [
    // { locationCode: 'localAdministrativeAuthorities1', locationName: 'LAA1' },
    // { locationCode: 'localAdministrativeAuthorities2', locationName: 'LAA2' },
    // { locationCode: 'localAdministrativeAuthorities3', locationName: 'LAA3' }
  ];

  isNewApplicant = false;
  @ViewChild('dd') dd: ElementRef;
  @ViewChild('mm') mm: ElementRef;
  @ViewChild('yyyy') yyyy: ElementRef;
  @ViewChild('age') age: ElementRef;
  @ViewChild('f') transForm: NgForm;

  selectedRegion: DropDown;
  selectedProvince: DropDown;
  selectedCity: DropDown;
  selectedLAA: DropDown;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService
  ) {}

  async ngOnInit() {
    if (sessionStorage.getItem('newApplicant') === 'true') {
      this.isNewApplicant = true;
    }
    this.route.parent.params.subscribe((params: Params) => {
      this.loginId = params['id'];
    });
    this.numberOfApplicants = 1;
    this.initForm();

    await this.getLocationMetadataHirearchy();
    // await this.onLocationSelect(this.uppermostLocationHierarchy[0].code);
    // console.log('tets', this.uppermostLocationHierarchy[0].code);
    // await this.getLocationImmediateHierearchy('', this.uppermostLocationHierarchy[0].code).then(res => {
    //   console.log(res);
    // });
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
    let t_region = '';

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

      await this.callLocation();
      await this.viewValueToValue(region);
      await this.viewValueToValue(province);
      await this.viewValueToValue(city);
      await this.viewValueToValue(localAdministrativeAuthority);
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
      t_addressLine3: new FormControl(t_addressLine3),
      t_region: new FormControl(t_region)
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
  }

  callLocation() {
    this.onLocationSelect('IND', this.provinces);
    this.onLocationSelect('IND', this.cities);
    this.onLocationSelect('IND', this.localAdministrativeAuthorities);

    console.log('provinces', this.provinces);
    console.log('cities', this.cities);
    console.log('LAAS', this.localAdministrativeAuthorities);
  }

  getLocationMetadataHirearchy() {
    return new Promise((resolve, reject) => {
      this.dataStorageService.getLocationMetadataHirearchy('country').subscribe(
        response => {
          const countryHirearchy = response['locations'];
          const uppermostLocationHierarchy = countryHirearchy.filter(
            element => element.name === appConstants.COUNTRY_NAME
          );
          this.uppermostLocationHierarchy = uppermostLocationHierarchy;
          resolve(this.uppermostLocationHierarchy);
        },
        error => console.log('Error in fetching location Hierarchy')
      );
    });
  }

  setLocation(event: MatSelectChange, entity: AbstractControl) {
    // entity.patchValue(event.source.triggerValue);
    console.log(event);
    const test = this.regions.filter(element => {
      if (element.locationCode === entity.value) {
        console.log('inside');
        this.selectedRegion = element;
        return element;
      }
    });
    //same for transRegion///////////////////////////

    // console.log(this.selectedRegion.locationName);
    // this.viewValueToValue();
    // console.log(this.selectedRegion);
  }

  viewValueToValue(region) {
    console.log(region);

    const value = region;
    this.regions.filter(el => {
      if (el.locationCode === value) {
        this.selectedRegion = el;
      }
    });
  }

  onLocationSelect(locationCode: string, entity: DropDown[]) {
    locationCode = 'IND';
    this.getLocationImmediateHierearchy(this.primaryLangCode, locationCode, entity);
  }

  getLocationImmediateHierearchy(lang: string, location: string, entity: DropDown[]) {
    this.dataStorageService.getLocationImmediateHierearchy(lang, location).subscribe(
      response => {
        response['locations'].forEach(element => {
          let dropDown: DropDown = {
            locationCode: element.code,
            locationName: element.name
          };
          entity.push(dropDown);
        });
      },
      error => console.log('Unable to fetch Regions'),
      () => {
        this.setLocation(null, this.userForm.controls['region']);
      }
    );
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
      console.log(bDay);
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
      //   this.transForm.controls[toControl.name].patchValue(response['response'].to_field_value);
      // });
    }
  }

  private noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { whitespace: true };
  }

  onSubmit() {
    // console.log(this.uppermostLocationHierarchy[0].code);
    // this.dataStorageService.getLocationList('BLR', this.primaryLangCode);
    console.log(this.userForm.controls);

    const request = this.createRequestJSON();
    this.dataUploadComplete = false;
    this.dataStorageService.addUser(request).subscribe(
      response => {
        console.log('response', response);

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
          this.preRegId = response['response'][0].preRegistrationId;
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
        new AttributeModel(this.primaryLang, this.demo.fullName, this.userForm.controls.fullName.value),
        new AttributeModel(this.secondaryLang, this.demo1.fullName, this.transUserForm.controls.t_fullName.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.dateOfBirth, this.userForm.controls.dob.value),
        new AttributeModel(this.secondaryLang, this.demo1.dateOfBirth, this.transUserForm.controls.t_dob.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.gender, this.userForm.controls.gender.value),
        new AttributeModel(this.secondaryLang, this.demo1.gender, this.transUserForm.controls.t_gender.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine1, this.userForm.controls.addressLine1.value),
        new AttributeModel(
          this.secondaryLang,
          this.demo1.addressLine1,
          this.transUserForm.controls.t_addressLine1.value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine2, this.userForm.controls.addressLine2.value),
        new AttributeModel(
          this.secondaryLang,
          this.demo1.addressLine2,
          this.transUserForm.controls.t_addressLine2.value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine3, this.userForm.controls.addressLine3.value),
        new AttributeModel(
          this.secondaryLang,
          this.demo1.addressLine3,
          this.transUserForm.controls.t_addressLine3.value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.selectedRegion.locationName, this.userForm.controls.region.value),
        new AttributeModel(this.secondaryLang, this.demo1.region, this.transForm.controls.t_region.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.province, this.userForm.controls.province.value),
        new AttributeModel(this.secondaryLang, this.demo1.province, this.transForm.controls.t_province.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.city, this.userForm.controls.city.value),
        new AttributeModel(this.secondaryLang, this.demo1.city, this.transForm.controls.t_city.value)
      ],
      [
        new AttributeModel(
          this.primaryLang,
          this.demo.localAdministrativeAuthority,
          this.userForm.controls.localAdministrativeAuthority.value
        ),
        new AttributeModel(
          this.secondaryLang,
          this.demo1.localAdministrativeAuthority,
          this.transForm.controls.t_localAdministrativeAuthority.value
        )
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.postalCode, this.userForm.controls.postalCode.value),
        new AttributeModel(this.secondaryLang, this.demo1.postalCode, this.transForm.controls.t_postalCode.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.mobileNumber, this.userForm.controls.mobilePhone.value),
        new AttributeModel(this.secondaryLang, this.demo1.mobileNumber, this.transForm.controls.t_mobilePhone.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.emailId, this.userForm.controls.email.value),
        new AttributeModel(this.secondaryLang, this.demo1.emailId, this.transForm.controls.t_email.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.CNEOrPINNumber, this.userForm.controls.pin.value),
        new AttributeModel(this.secondaryLang, this.demo1.CNEOrPINNumber, this.transForm.controls.t_pin.value)
      ]
    );

    return identity;
  }

  private createRequestJSON() {
    const identity = this.createIdentityJSON();
    console.log('identity', identity);

    let preRegistrationId = '';
    let createdBy = this.loginId;
    let createdDateTime = Utils.getCurrentDate();
    let updatedBy = '';
    let updatedDateTime = '';
    let statusCode = appConstants.APPLICATION_STATUS_CODES.pending;
    let langCode = appConstants.LANG_CODE;
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
