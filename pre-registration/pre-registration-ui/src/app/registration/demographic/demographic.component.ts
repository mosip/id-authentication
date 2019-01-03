import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { FormGroup, FormControl, Validators, NgForm } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { RegistrationService } from '../registration.service';
import { DataStorageService } from 'src/app/shared/data-storage.service';
import { DemoLabels } from './demographic.labels';
import { IdentityModel } from './identity.model';
import { AttributeModel } from './attribute.model';
import { RequestModel } from './request.model';
import { DemoIdentityModel } from './demo.identity.model';
import { UserModel } from './user.model';
import { SharedService } from 'src/app/shared/shared.service';
import * as appConstants from '../../app.constants';

@Component({
  selector: 'app-demographic',
  templateUrl: './demographic.component.html',
  styleUrls: ['./demographic.component.css']
})
export class DemographicComponent implements OnInit {
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
  secondaryLang = 'fr';
  ageOrDobPref = '';
  showCalender: boolean;
  showDate = false;
  numberOfApplicants: number;
  userForm: FormGroup;
  numbers: number[];
  isDisabled = [];
  checked = true;
  maxDate = new Date(Date.now());
  preRegId = '';
  loginId = '';
  dataUploadComplete = true;
  uppermostLocationHierarchy;
  isNewApplicant = false;
  @ViewChild('dd') dd;
  @ViewChild('mm') mm;
  @ViewChild('yyyy') yyyy;
  @ViewChild('age') age;
  @ViewChild('f') transForm;
  // @ViewChild('f1') form;
  @ViewChild('gen') gender;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
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
    this.isDisabled[0] = true;

    // this.uppermostLocationHierarchy = this.dataStorageService
    //   .getLocationMetadataHirearchy('country')
    //   .subscribe(response => {
    //     const countryHirearchy = response['response'];
    //     const uppermostLocationHierarchy = countryHirearchy.filter(element => element.name === 'INDIA');
    //     this.uppermostLocationHierarchy = uppermostLocationHierarchy;
    //   });
  }

  initForm() {
    let fullName = '';
    let gender = '';
    let addressLine1 = '';
    let addressLine2 = '';
    let addressLine3 = '';
    let region = '';
    let province = '';
    let city = '';
    let localAdministrativeAuthority = '';
    let email = '';
    let dob = '';
    let age = '';
    let postalCode = '';
    let mobilePhone = '';
    let pin = '';
    let date = '';
    let month = '';
    let year = '';

    if (this.regService.getUser(this.step) != null) {
      const user = this.regService.getUser(this.step);
      this.preRegId = user.preRegId;
      fullName = user.identity.FullName[0].value;
      gender = user.identity.gender[0].value;
      addressLine1 = user.identity.addressLine1[0].value;
      addressLine2 = user.identity.addressLine2[0].value;
      addressLine3 = user.identity.addressLine3[0].value;
      region = user.identity.region[0].value;
      province = user.identity.province[0].value;
      city = user.identity.city[0].value;
      localAdministrativeAuthority = user.identity.localAdministrativeAuthority[0].value;
      email = user.identity.emailId[0].value;
      date = user.identity.dateOfBirth[0].value.split('/')[0];
      month = user.identity.dateOfBirth[0].value.split('/')[1];
      year = user.identity.dateOfBirth[0].value.split('/')[2];
      dob = user.identity.dateOfBirth[0].value;
      age = this.calculateAge(new Date(new Date(dob))).toString();
      postalCode = user.identity.postalcode[0].value;
      mobilePhone = user.identity.mobileNumber[0].value;
      pin = user.identity.CNEOrPINNumber[0].value;
    }

    this.userForm = new FormGroup({
      fullName: new FormControl(fullName.trim(), [Validators.required, this.noWhitespaceValidator]),
      gender: new FormControl(gender, Validators.required),
      addressLine1: new FormControl(addressLine1, [Validators.required, this.noWhitespaceValidator]),
      addressLine2: new FormControl(addressLine2),
      addressLine3: new FormControl(addressLine3),
      region: new FormControl(region, Validators.required),
      province: new FormControl(province, Validators.required),
      city: new FormControl(city, Validators.required),
      localAdministrativeAuthority: new FormControl(localAdministrativeAuthority, Validators.required),
      email: new FormControl(email, Validators.email),
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
  }

  onBack() {
    this.router.navigate(['dashboard', this.loginId]);
  }

  onSubmit() {
    // console.log(this.uppermostLocationHierarchy[0].code);
    // this.dataStorageService.getLocationList('BLR', 'ENG');
    let preId = '';
    const identity = this.createIdentityJSON();
    this.dataUploadComplete = false;
    this.dataStorageService.addUser(this.createRequestJSON(this.preRegId)).subscribe(
      response => {
        if (this.regService.getUser(this.step) != null) {
          this.regService.updateUser(
            this.step,
            new UserModel(this.preRegId, identity, this.regService.getUserFiles(this.step))
          );
          this.sharedService.updateNameList(this.step, {
            fullName: this.userForm.controls.fullName.value,
            preRegId: this.preRegId
          });
        } else {
          preId = response['response'][0].prId;
          this.regService.addUser(new UserModel(preId, identity, []));
          this.sharedService.addNameList({
            fullName: this.userForm.controls.fullName.value,
            preRegId: preId
          });
        }
      },
      error => {
        console.log(error);
        this.router.navigate(['error']);
      },
      () => {
        this.isDisabled[this.step] = true;
        this.step++;
        this.checked = true;
        this.dataUploadComplete = true;
        if (this.step === this.numberOfApplicants) {
          this.router.navigate(['../file-upload'], { relativeTo: this.route });
        }
      }
    );
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

  private calculateAge(bDay) {
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

  onTransliteration(fromControl, toControl) {
    console.log(toControl.name);

    console.log(fromControl.value);

    if (fromControl) {
      console.log('inside trans');
      const request: any = {
        from_field_lang: 'English',
        from_field_name: 'Name1',
        from_field_value: fromControl,
        to_field_lang: 'Arabic',
        to_field_name: 'Name2',
        to_field_value: ''
      };
      this.dataStorageService.getTransliteration(request).subscribe(response => {
        console.log(response);
        this.transForm.controls[toControl.name].patchValue(response['response'].to_field_value);
      });
    }
  }

  noWhitespaceValidatorHTML(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    if (!isValid) {
      control.setErrors({ incorrect: true });
    }
  }

  private noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { whitespace: true };
  }

  private createIdentityJSON() {
    const identity = new IdentityModel(
      [
        new AttributeModel(this.primaryLang, this.demo.fullName, this.userForm.controls.fullName.value),
        new AttributeModel(this.secondaryLang, this.demo1.fullName, this.transForm.controls.t_fullName.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.dateOfBirth, this.userForm.controls.dob.value),
        new AttributeModel(this.secondaryLang, this.demo1.dateOfBirth, this.userForm.controls.dob.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.gender, this.userForm.controls.gender.value),
        new AttributeModel(this.secondaryLang, this.demo1.gender, this.transForm.controls.t_gender.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine1, this.userForm.controls.addressLine1.value),
        new AttributeModel(this.secondaryLang, this.demo1.addressLine1, this.transForm.controls.t_addressLine1.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine2, this.userForm.controls.addressLine2.value),
        new AttributeModel(this.secondaryLang, this.demo1.addressLine2, this.transForm.controls.t_addressLine2.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.addressLine3, this.userForm.controls.addressLine3.value),
        new AttributeModel(this.secondaryLang, this.demo1.addressLine3, this.transForm.controls.t_addressLine3.value)
      ],
      [
        new AttributeModel(this.primaryLang, this.demo.region, this.userForm.controls.region.value),
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

  private createRequestJSON(id: string) {
    const identity = this.createIdentityJSON();
    const req: RequestModel = {
      preRegistrationId: id,
      createdBy: this.loginId,
      createdDateTime: '',
      updatedBy: '',
      updatedDateTime: '',
      statusCode: appConstants.STATUS_CODE,
      langCode: appConstants.LANG_CODE,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }
}
