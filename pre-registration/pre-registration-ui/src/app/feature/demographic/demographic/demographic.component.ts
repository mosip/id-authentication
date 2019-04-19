import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSelectChange, MatButtonToggleChange, MatDialog } from '@angular/material';
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
import * as appConstants from '../../../app.constants';
import Utils from 'src/app/app.util';
import { DialougComponent } from 'src/app/shared/dialoug/dialoug.component';
import { ConfigService } from 'src/app/core/services/config.service';
import { AttributeModel } from 'src/app/shared/models/demographic-model/attribute.modal';
import { ResponseModel } from 'src/app/shared/models/demographic-model/response.model';

/**
 * @description This component takes care of the demographic page.
 * @author Shashank Agrawal
 *
 * @export
 * @class DemographicComponent
 * @implements {OnInit}
 * @implements {OnDestroy}
 */
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

  // YEAR_PATTERN = appConstants.YEAR_PATTERN;
  // MONTH_PATTERN = appConstants.MONTH_PATTERN;
  // DATE_PATTERN = appConstants.DATE_PATTERN;

  agePattern: string;
  MOBILE_PATTERN: string;
  MOBILE_LENGTH: string;
  CNIE_PATTERN: string;
  CNIE_LENGTH: string;
  EMAIL_PATTERN: string;
  EMAIL_LENGTH: string;
  DOB_PATTERN: string;
  POSTALCODE_PATTERN: string;
  POSTALCODE_LENGTH: string;
  ADDRESS_PATTERN: string;
  defaultDay: string;
  defaultMonth: string;
  FULLNAME_PATTERN: string;

  ageOrDobPref = '';
  showDate = false;
  isNewApplicant = false;
  checked = true;
  dataUploadComplete = true;
  // isReadOnly = false;
  dataModification: boolean;
  showPreviewButton = false;

  step: number = 0;
  id: number;
  oldAge: number;
  numberOfApplicants: number;
  userForm: FormGroup;
  transUserForm: FormGroup;
  maxDate = new Date(Date.now());
  preRegId = '';
  loginId = '';
  user: UserModel;
  demodata: string[];
  secondaryLanguagelabels: any;
  primaryLanguagelabels: any;
  uppermostLocationHierarchy: any;
  primaryGender = [];
  secondaryGender = [];
  primaryResidenceStatus = [];
  secondaryResidenceStatus = [];
  genders: any;
  residenceStatus: any;
  message = {};
  config = {};

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
    residenceStatus: 'residenceStatus',
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

  /**
   * @description Creates an instance of DemographicComponent.
   * @param {Router} router
   * @param {RegistrationService} regService
   * @param {DataStorageService} dataStorageService
   * @param {SharedService} sharedService
   * @param {ConfigService} configService
   * @param {TranslateService} translate
   * @param {MatDialog} dialog
   * @memberof DemographicComponent
   */
  constructor(
    private router: Router,
    private regService: RegistrationService,
    private dataStorageService: DataStorageService,
    private sharedService: SharedService,
    private configService: ConfigService,
    private translate: TranslateService,
    private dialog: MatDialog
  ) {
    this.translate.use(localStorage.getItem('langCode'));
    this.regService.getMessage().subscribe(message => (this.message = message));
    this.initialization();
  }

  /**
   * @description This is the anular life cycle hook called upon loading the component.
   *
   * @memberof DemographicComponent
   */
  async ngOnInit() {
    console.log('IN DEMOGRAPHIC');
    this.config = this.configService.getConfig();
    this.setConfig();
    await this.getPrimaryLabels();
    this.initForm();
    this.dataStorageService.getSecondaryLanguageLabels(this.secondaryLang).subscribe(response => {
      this.secondaryLanguagelabels = response['demographic'];
    });
    if (!this.dataModification) this.consentDeclaration();
  }

  /**
   * @description This set the global configuration used in the demographic component.
   *
   * @memberof DemographicComponent
   */
  setConfig() {
    this.MOBILE_PATTERN = this.config[appConstants.CONFIG_KEYS.mosip_regex_phone];
    this.CNIE_PATTERN = this.config[appConstants.CONFIG_KEYS.mosip_regex_CNIE];
    this.EMAIL_PATTERN = this.config[appConstants.CONFIG_KEYS.mosip_regex_email];
    this.POSTALCODE_PATTERN = this.config[appConstants.CONFIG_KEYS.mosip_regex_postalCode];
    this.DOB_PATTERN = this.config[appConstants.CONFIG_KEYS.mosip_regex_DOB];
    this.defaultDay = this.config[appConstants.CONFIG_KEYS.mosip_default_dob_day];
    this.defaultMonth = this.config[appConstants.CONFIG_KEYS.mosip_default_dob_month];
    this.ADDRESS_PATTERN = this.config[appConstants.CONFIG_KEYS.preregistration_address_length];
    this.FULLNAME_PATTERN = this.config[appConstants.CONFIG_KEYS.preregistration_fullname_length];
    this.agePattern = this.config[appConstants.CONFIG_KEYS.mosip_id_validation_identity_age];
  }

  /**
   * @description This will return the json object of label of demographic in the primary language.
   *
   * @private
   * @returns the `Promise`
   * @memberof DemographicComponent
   */
  private getPrimaryLabels() {
    return new Promise(resolve => {
      this.dataStorageService.getSecondaryLanguageLabels(this.primaryLang).subscribe(response => {
        this.primaryLanguagelabels = response['demographic'];
        resolve(true);
      });
    });
  }

  /**
   * @description This method do the basic initialization,
   * if user is opt for updation or creating the new applicaton
   *
   * @private
   * @memberof DemographicComponent
   */
  private initialization() {
    if (localStorage.getItem('newApplicant') === 'true') {
      this.isNewApplicant = true;
    }
    if (this.message['modifyUser'] === 'true' || this.message['modifyUserFromPreview'] === 'true') {
      this.dataModification = true;
      this.step = this.regService.getUsers().length - 1;
      if (this.message['modifyUserFromPreview'] === 'true') this.showPreviewButton = true;
    } else {
      this.dataModification = false;
      this.step = this.regService.getUsers().length;
    }
    this.loginId = this.regService.getLoginId();
  }

  /**
   * @description This is the consent form, which applicant has to agree upon to proceed forward.
   *
   * @private
   * @memberof DemographicComponent
   */
  private consentDeclaration() {
    if (this.primaryLanguagelabels) {
      const data = {
        case: 'CONSENTPOPUP',
        title: this.primaryLanguagelabels.consent.title,
        subtitle: this.primaryLanguagelabels.consent.subtitle,
        message: this.primaryLanguagelabels.consent.message,
        checkCondition: this.primaryLanguagelabels.consent.checkCondition,
        acceptButton: this.primaryLanguagelabels.consent.acceptButton,
        alertMessageFirst: this.primaryLanguagelabels.consent.alertMessageFirst,
        alertMessageSecond: this.primaryLanguagelabels.consent.alertMessageSecond,
        alertMessageThird: this.primaryLanguagelabels.consent.alertMessageThird
      };
      this.dialog.open(DialougComponent, {
        width: '550px',
        data: data,
        disableClose: true
      });
    }
  }

  /**
   * @description This will initialize the demographic form and
   * if update set the inital values of the attributes.
   *
   *
   * @memberof DemographicComponent
   */
  async initForm() {
    if (this.dataModification) {
      this.user = this.regService.getUser(this.step);
      this.preRegId = this.user.preRegId;
    }
    this.setFormControlValues();

    this.userForm = new FormGroup({
      [this.formControlNames.fullName]: new FormControl(this.formControlValues.fullName.trim(), [
        Validators.required,
        Validators.pattern(this.FULLNAME_PATTERN),
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.gender]: new FormControl(this.formControlValues.gender, Validators.required),
      [this.formControlNames.residenceStatus]: new FormControl(
        this.formControlValues.residenceStatus,
        Validators.required
      ),
      [this.formControlNames.age]: new FormControl(this.formControlValues.age, [
        Validators.required,
        Validators.pattern(this.agePattern)
      ]),
      [this.formControlNames.dateOfBirth]: new FormControl(this.formControlValues.dateOfBirth, [
        Validators.pattern(this.DOB_PATTERN)
      ]),
      [this.formControlNames.date]: new FormControl(this.formControlValues.date, [Validators.required]),
      [this.formControlNames.month]: new FormControl(this.formControlValues.month, [Validators.required]),
      [this.formControlNames.year]: new FormControl(this.formControlValues.year, [Validators.required]),
      [this.formControlNames.addressLine1]: new FormControl(this.formControlValues.addressLine1, [
        Validators.required,
        Validators.pattern(this.ADDRESS_PATTERN),
        this.noWhitespaceValidator
      ]),
      [this.formControlNames.addressLine2]: new FormControl(
        this.formControlValues.addressLine2,
        Validators.pattern(this.ADDRESS_PATTERN)
      ),
      [this.formControlNames.addressLine3]: new FormControl(
        this.formControlValues.addressLine3,
        Validators.pattern(this.ADDRESS_PATTERN)
      ),
      [this.formControlNames.region]: new FormControl(this.formControlValues.region, Validators.required),
      [this.formControlNames.province]: new FormControl(this.formControlValues.province, Validators.required),
      [this.formControlNames.city]: new FormControl(this.formControlValues.city, Validators.required),
      [this.formControlNames.localAdministrativeAuthority]: new FormControl(
        this.formControlValues.localAdministrativeAuthority,
        Validators.required
      ),
      [this.formControlNames.email]: new FormControl(this.formControlValues.email, [
        Validators.pattern(this.EMAIL_PATTERN)
        // Validators.maxLength(Number(this.EMAIL_LENGTH))
      ]),
      [this.formControlNames.postalCode]: new FormControl(this.formControlValues.postalCode, [
        Validators.required,
        Validators.pattern(this.POSTALCODE_PATTERN)
      ]),
      [this.formControlNames.phone]: new FormControl(this.formControlValues.phone, [
        Validators.pattern(this.MOBILE_PATTERN)
      ]),
      [this.formControlNames.CNIENumber]: new FormControl(this.formControlValues.CNIENumber, [
        Validators.required,
        // Validators.maxLength(Number(this.CNIE_LENGTH)),
        Validators.pattern(this.CNIE_PATTERN)
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

  /**
   * @description This sets the top location hierachy,
   * if update set the regions also.
   *
   * @private
   * @memberof DemographicComponent
   */
  private async setLocations() {
    await this.getLocationMetadataHirearchy();
    console.log('this.uppermostLocationHierarchy', this.uppermostLocationHierarchy);

    this.selectedLocationCode = [
      this.uppermostLocationHierarchy,
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

  /**
   * @description This is to get the list of gender available in the master data.
   *
   * @private
   * @memberof DemographicComponent
   */
  private async setGender() {
    await this.getGenderDetails();
    this.filterOnLangCode(this.primaryLang, this.primaryGender, this.genders);
    this.filterOnLangCode(this.secondaryLang, this.secondaryGender, this.genders);
  }

  /**
   * @description This set the initial values for the form attributes.
   *
   * @private
   * @memberof DemographicComponent
   */
  private setFormControlValues() {
    if (!this.dataModification) {
      this.formControlValues = {
        fullName: '',
        gender: '',
        residenceStatus: '',
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
      let index = 0;
      let secondaryIndex = 1;
      if (this.user.request.demographicDetails.identity.fullName[0].language !== this.primaryLang) {
        index = 1;
        secondaryIndex = 0;
      }
      const dob = this.user.request.demographicDetails.identity.dateOfBirth;
      this.formControlValues = {
        fullName: this.user.request.demographicDetails.identity.fullName[index].value,
        gender: this.user.request.demographicDetails.identity.gender[index].value,
        residenceStatus: this.user.request.demographicDetails.identity.residenceStatus[index].value,
        date: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[2],
        month: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[1],
        year: this.user.request.demographicDetails.identity.dateOfBirth.split('/')[0],
        dateOfBirth: dob,
        age: this.calculateAge(new Date(new Date(dob))).toString(),
        addressLine1: this.user.request.demographicDetails.identity.addressLine1[index].value,
        addressLine2: this.user.request.demographicDetails.identity.addressLine2[index].value,
        addressLine3: this.user.request.demographicDetails.identity.addressLine3[index].value,
        region: this.user.request.demographicDetails.identity.region[index].value,
        province: this.user.request.demographicDetails.identity.province[index].value,
        city: this.user.request.demographicDetails.identity.city[index].value,
        localAdministrativeAuthority: this.user.request.demographicDetails.identity.localAdministrativeAuthority[0]
          .value,
        email: this.user.request.demographicDetails.identity.email,
        postalCode: this.user.request.demographicDetails.identity.postalCode,
        phone: this.user.request.demographicDetails.identity.phone,
        CNIENumber: this.user.request.demographicDetails.identity.CNIENumber.toString(),

        fullNameSecondary: this.user.request.demographicDetails.identity.fullName[secondaryIndex].value,
        addressLine1Secondary: this.user.request.demographicDetails.identity.addressLine1[secondaryIndex].value,
        addressLine2Secondary: this.user.request.demographicDetails.identity.addressLine2[secondaryIndex].value,
        addressLine3Secondary: this.user.request.demographicDetails.identity.addressLine3[secondaryIndex].value
      };
    }
  }

  /**
   * @description This will get the gender details from the master data.
   *
   * @private
   * @returns
   * @memberof DemographicComponent
   */
  private getGenderDetails() {
    return new Promise(resolve => {
      this.dataStorageService.getGenderDetails().subscribe(
        response => {
          console.log(response);
          if (response[appConstants.NESTED_ERROR]) {
            this.onError();
          } else {
            this.genders = response[appConstants.RESPONSE][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.genderTypes];
            resolve(true);
          }
        },
        () => {
          console.log('Unable to fetch gender');
          this.onError();
        }
      );
    });
  }

  /**
   * @description This will filter the gender on the basis of langugae code.
   *
   * @private
   * @param {string} langCode
   * @param {*} [genderEntity=[]]
   * @param {*} entityArray
   * @memberof DemographicComponent
   */
  private filterOnLangCode(langCode: string, genderEntity = [], entityArray: any) {
    if (entityArray) {
      entityArray.filter((element: any) => {
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
  }

  /**
   * @description This is to get the top most location Hierarchy, i.e. `Country Code`
   *
   * @returns
   * @memberof DemographicComponent
   */
  getLocationMetadataHirearchy() {
    return new Promise(resolve => {
      const uppermostLocationHierarchy = this.dataStorageService.getLocationMetadataHirearchy();
      this.uppermostLocationHierarchy = uppermostLocationHierarchy;
      resolve(this.uppermostLocationHierarchy);
    });
  }

  /**
   * @description This method get the next set of locations hierarchy for the selected location
   *
   * @param {MatSelectChange} event
   * @param {CodeValueModal[][]} nextHierarchies
   * @param {CodeValueModal[][]} currentLocationHierarchies
   * @param {string} [formControlName]
   * @memberof DemographicComponent
   */
  async onLocationSelect(
    event: MatSelectChange,
    nextHierarchies: CodeValueModal[][],
    currentLocationHierarchies: CodeValueModal[][],
    formControlName?: string
  ) {
    if (nextHierarchies) {
      for (let index = 0; index < nextHierarchies.length; index++) {
        const element = nextHierarchies[index];
        const languageCode = this.languages[index];
        if (formControlName) this.userForm.controls[formControlName].setValue('');
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

  /**
   * @description This method push to the CodeValueModal array
   *
   * @param {CodeValueModal} element
   * @memberof DemographicComponent
   */
  addCodeValue(element: CodeValueModal) {
    this.codeValue.push({
      valueCode: element.valueCode,
      valueName: element.valueName,
      languageCode: element.languageCode
    });
  }

  /**
   * @description This method returns the next immediate location hierarchy for the selected location.
   *
   * @param {string} languageCode
   * @param {string} parentLocationCode
   * @param {CodeValueModal[]} childLocations
   * @param {string} [currentLocationCode]
   * @returns
   * @memberof DemographicComponent
   */
  getLocationImmediateHierearchy(
    languageCode: string,
    parentLocationCode: string,
    childLocations: CodeValueModal[],
    currentLocationCode?: string
  ) {
    childLocations.length = 0;
    return new Promise(resolve => {
      this.dataStorageService.getLocationImmediateHierearchy(languageCode, parentLocationCode).subscribe(
        response => {
          console.log('IMMEDIATE', response);
          if (response[appConstants.NESTED_ERROR]) {
            this.onError();
          } else {
            response[appConstants.RESPONSE][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.locations].forEach(element => {
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
          }
        },
        () => {
          this.onError();
          console.log('Unable to fetch Below Hierearchy');
        }
      );
    });
  }

  /**
   * @description On click of back button the user will be navigate to dashboard.
   *
   * @memberof DemographicComponent
   */
  onBack() {
    let url = '';
    url = Utils.getURL(this.router.url, 'dashboard', 2);
    this.router.navigate([url]);
  }

  /**
   * @description On change of natioanlity, this is called.
   *
   * @param {*} entity
   * @param {MatButtonToggleChange} [event]
   * @memberof DemographicComponent
   */
  onEntityChange(entity: any, event?: MatButtonToggleChange) {
    if (event) {
      entity.forEach(element => {
        element.filter((element: any) => {
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
  }

  /**
   * @description This is called when age is changed and the date of birth will get calculated.
   *
   * @memberof DemographicComponent
   */
  onAgeChange() {
    const age = this.age.nativeElement.value;
    const ageRegex = new RegExp(this.agePattern);
    if (age && age != this.oldAge)
      if (ageRegex.test(age)) {
        const now = new Date();
        const calulatedYear = now.getFullYear() - age;
        this.userForm.controls[this.formControlNames.date].patchValue(this.defaultDay);
        this.userForm.controls[this.formControlNames.month].patchValue(this.defaultMonth);
        this.userForm.controls[this.formControlNames.year].patchValue(calulatedYear);
        this.userForm.controls[this.formControlNames.dateOfBirth].patchValue(
          calulatedYear + '/' + this.defaultMonth + '/' + this.defaultDay
        );
        this.userForm.controls[this.formControlNames.dateOfBirth].setErrors(null);
      } else {
        this.userForm.controls[this.formControlNames.date].patchValue('');
        this.userForm.controls[this.formControlNames.month].patchValue('');
        this.userForm.controls[this.formControlNames.year].patchValue('');
      }
  }

  /**
   * @description This is to change the focus in date of birth field.
   *
   * @memberof DemographicComponent
   */
  nextElementFocus() {
    console.log('AAYA');

    const dayMaxLength = 2;
    const monthMaxLength = 2;
    const date = this.dd.nativeElement.value;
    const month = this.mm.nativeElement.value;
    console.log(this.mm);
    console.log(date.length);
    console.log(dayMaxLength);

    if (!this.dataModification) {
      if (date.length == dayMaxLength) {
        console.log('aaya date');
        this.mm.nativeElement.focus();
      }
      if (month.length == monthMaxLength) {
        console.log('aaya month');
        this.yyyy.nativeElement.focus();
      }
    }
  }

  /**
   * @description This is called whenever there is a change in Date of birth field and accordingly age
   * will get calculate.
   *
   * @memberof DemographicComponent
   */
  onDOBChange() {
    const date = this.dd.nativeElement.value;
    const month = this.mm.nativeElement.value;
    const year = this.yyyy.nativeElement.value;

    const newDate = year + '/' + month + '/' + date;
    const dobRegex = new RegExp(this.DOB_PATTERN);
    if (dobRegex.test(newDate)) {
      // if (date.length == 2 && month.length == 2 && year.length == 4) {
      const dateform = new Date(newDate);
      // const _month = dateform.getMonth() + 1;
      // if (dateform.toDateString() !== 'Invalid Date' && (+month === _month || month === '0' + _month)) {
      // const pipe = new DatePipe('en-US');
      // const myFormattedDate = pipe.transform(dateform, 'yyyy/MM/dd');
      this.userForm.controls[this.formControlNames.dateOfBirth].patchValue(newDate);
      this.userForm.controls[this.formControlNames.age].patchValue(this.calculateAge(dateform));
      // }
      // else {
      //   console.log('INSIDE DATE CHANGE ERROR');

      //   this.userForm.controls[this.formControlNames.dateOfBirth].markAsTouched();
      //   this.userForm.controls[this.formControlNames.dateOfBirth].setErrors({
      //     incorrect: true
      //   });
      // this.userForm.controls[this.formControlNames.age].patchValue('');
    } else {
      console.log('INSIDE DATE CHANGE ERROR');
      this.userForm.controls[this.formControlNames.dateOfBirth].markAsTouched();
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors({
        incorrect: true
      });
      this.userForm.controls[this.formControlNames.age].patchValue('');
    }
  }

  /**
   * @description This method calculates the age for the given date.
   *
   * @param {Date} bDay
   * @returns
   * @memberof DemographicComponent
   */
  calculateAge(bDay: Date) {
    const now = new Date();
    const born = new Date(bDay);
    const years = Math.floor((now.getTime() - born.getTime()) / (365.25 * 24 * 60 * 60 * 1000));

    if (this.dataModification) {
      this.oldAge = years;
      return years;
    }
    if (years > 150) {
      this.userForm.controls[this.formControlNames.dateOfBirth].markAsTouched();
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors({
        incorrect: true
      });
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      return '';
    } else {
      this.userForm.controls[this.formControlNames.dateOfBirth].markAsUntouched();
      this.userForm.controls[this.formControlNames.dateOfBirth].setErrors(null);
      this.userForm.controls[this.formControlNames.year].setErrors(null);
      this.oldAge = years;
      return years;
    }
  }

  /**
   * @description This is used for the tranliteration.
   *
   * @param {FormControl} fromControl
   * @param {*} toControl
   * @memberof DemographicComponent
   */
  onTransliteration(fromControl: FormControl, toControl: any) {
    if (fromControl.value) {
      const request: any = {
        from_field_lang: this.primaryLang,
        from_field_value: fromControl.value,
        to_field_lang: this.secondaryLang,
        to_field_value: ''
      };

      // this.transUserForm.controls[toControl].patchValue('dummyValue');

      this.dataStorageService.getTransliteration(request).subscribe(
        response => {
          console.log('TRANSLITERATION', response);

          if (!response[appConstants.NESTED_ERROR])
            this.transUserForm.controls[toControl].patchValue(response[appConstants.RESPONSE].to_field_value);
          else this.transUserForm.controls[toControl].patchValue('can not be transliterated');
        },
        error => {
          // this.transUserForm.controls[toControl].patchValue('can not be transliterated');
          this.onError();
          console.log(error);
        }
      );
    } else {
      this.transUserForm.controls[toControl].patchValue('');
    }
  }

  /**
   * @description This is a custom validator, which check for the white spaces.
   *
   * @private
   * @param {FormControl} control
   * @returns
   * @memberof DemographicComponent
   */
  private noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { whitespace: true };
  }

  /**
   * @description This is called to submit the user form in case od modify or create.
   *
   * @memberof DemographicComponent
   */
  onSubmit() {
    this.markFormGroupTouched(this.userForm);
    this.markFormGroupTouched(this.transUserForm);
    if (this.userForm.valid && this.transUserForm.valid) {
      const identity = this.createIdentityJSONDynamic();
      const request = this.createRequestJSON(identity);
      const responseJSON = this.createResponseJSON(identity);
      this.dataUploadComplete = false;
      if (this.dataModification) {
        let preRegistrationId = this.user.preRegId;
        this.dataStorageService.updateUser(request, preRegistrationId).subscribe(
          response => {
            console.log(response);
            if (
              (response[appConstants.NESTED_ERROR] === null && response[appConstants.RESPONSE] === null) ||
              response[appConstants.NESTED_ERROR] !== null
            ) {
              this.onError();
              return;
            } else {
              this.onModification(responseJSON);
            }
            this.onSubmission();
          },
          error => {
            console.log(error);
            this.onError();
          }
        );
      } else {
        this.dataStorageService.addUser(request).subscribe(
          response => {
            console.log(response);
            if (
              (response[appConstants.NESTED_ERROR] === null && response[appConstants.RESPONSE] === null) ||
              response[appConstants.NESTED_ERROR] !== null
            ) {
              this.onError();
              return;
            } else {
              this.onAddition(response, responseJSON);
            }
            this.onSubmission();
          },
          error => {
            console.log(error);
            // this.router.navigate(['error']);
            this.onError();
          }
        );
      }
    }
  }

  /**
   * @description This is called when user chooses to modify the data.
   *
   * @private
   * @param {ResponseModel} request
   * @memberof DemographicComponent
   */
  private onModification(request: ResponseModel) {
    this.regService.updateUser(
      this.step,
      new UserModel(this.preRegId, request, this.regService.getUserFiles(this.step), this.codeValue)
    );
    this.sharedService.updateNameList(this.step, {
      fullName: this.userForm.controls[this.formControlNames.fullName].value,
      fullNameSecondaryLang: this.transUserForm.controls[this.formControlNames.fullNameSecondary].value,
      preRegId: this.preRegId,
      postalCode: this.userForm.controls[this.formControlNames.postalCode].value,
      regDto: this.sharedService.getNameList()[0].regDto
    });

    console.log('GET NAME LIST on Modification', this.sharedService.getNameList());
  }

  /**
   * @description This is called when user creates a new application.
   *
   * @private
   * @param {*} response
   * @param {ResponseModel} request
   * @memberof DemographicComponent
   */
  private onAddition(response: any, request: ResponseModel) {
    this.preRegId = response[appConstants.RESPONSE][0][appConstants.DEMOGRAPHIC_RESPONSE_KEYS.preRegistrationId];
    this.regService.addUser(new UserModel(this.preRegId, request, [], this.codeValue));
    this.sharedService.addNameList({
      fullName: this.userForm.controls[this.formControlNames.fullName].value,
      fullNameSecondaryLang: this.transUserForm.controls[this.formControlNames.fullNameSecondary].value,
      preRegId: this.preRegId,
      postalCode: this.userForm.controls[this.formControlNames.postalCode].value
    });
    console.log('GET NAME LIST On ADDITON', this.sharedService.getNameList());
    console.log('GET User Array On ADDITON', this.regService.getUsers());
  }

  /**
   * @description After sumission of the form, the user is route to file-upload or preview page.
   *
   * @memberof DemographicComponent
   */
  onSubmission() {
    this.checked = true;
    this.dataUploadComplete = true;
    let url = '';
    if (this.message['modifyUserFromPreview'] === 'true') {
      url = Utils.getURL(this.router.url, 'summary/preview');
    } else {
      url = Utils.getURL(this.router.url, 'file-upload');
    }
    console.log('OUT DEMOGRAPHIC IN FILE-UPLOAD OR PREVIEW');
    this.router.navigate([url]);
  }

  /**
   * @description THis is to create the attribute array for the Identity modal.
   *
   * @private
   * @param {string} element
   * @param {IdentityModel} identity
   * @memberof DemographicComponent
   */
  private createAttributeArray(element: string, identity: IdentityModel) {
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
    } else if (typeof identity[element] === 'string' && this.userForm.controls[this.formControlNames[element]].value) {
      attr = this.userForm.controls[this.formControlNames[element]].value;
    }
    identity[element] = attr;
  }

  /**
   * @description This method mark all the form control as touched
   *
   * @private
   * @param {FormGroup} formGroup
   * @memberof DemographicComponent
   */
  private markFormGroupTouched(formGroup: FormGroup) {
    (<any>Object).values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control.controls) {
        this.markFormGroupTouched(control);
      }
    });
  }

  /**
   * @description This is to create the identity modal
   *
   * @private
   * @returns
   * @memberof DemographicComponent
   */
  private createIdentityJSONDynamic() {
    const identity = new IdentityModel(1, [], '', [], [], [], [], [], [], [], [], [], '', '', '', '');
    let keyArr: any[] = Object.keys(this.formControlNames);
    for (let index = 0; index < keyArr.length - 8; index++) {
      const element = keyArr[index];
      this.createAttributeArray(element, identity);
    }
    return identity;
  }

  /**
   * @description This is to create the request modal.
   *
   * @private
   * @param {IdentityModel} identity
   * @returns
   * @memberof DemographicComponent
   */
  private createRequestJSON(identity: IdentityModel) {
    let langCode = this.primaryLang;
    if (this.user) {
      langCode = this.user.request.langCode;
    }
    const req: RequestModel = {
      langCode: langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }

  /**
   * @description This is the response modal.
   *
   * @private
   * @param {IdentityModel} identity
   * @returns
   * @memberof DemographicComponent
   */
  private createResponseJSON(identity: IdentityModel) {
    let preRegistrationId = '';
    let createdBy = this.loginId;
    let createdDateTime = Utils.getCurrentDate();
    let updatedDateTime = '';
    let langCode = this.primaryLang;
    if (this.user) {
      preRegistrationId = this.user.preRegId;
      createdBy = this.user.request.createdBy;
      createdDateTime = this.user.request.createdDateTime;
      updatedDateTime = Utils.getCurrentDate();
      langCode = this.user.request.langCode;
    }
    const req: ResponseModel = {
      preRegistrationId: preRegistrationId,
      createdBy: createdBy,
      createdDateTime: createdDateTime,
      updatedDateTime: updatedDateTime,
      langCode: langCode,
      demographicDetails: new DemoIdentityModel(identity)
    };
    return req;
  }

  ngOnDestroy() {
    // this.message$
  }
  /**
   * @description This is a dialoug box whenever an erroe comes from the server, it will appear.
   *
   * @private
   * @memberof DemographicComponent
   */
  private onError() {
    console.log(this.primaryLanguagelabels);
    console.log(this.dialog.openDialogs);

    this.dataUploadComplete = true;
    const body = {
      case: 'ERROR',
      title: 'ERROR',
      message: this.primaryLanguagelabels.error.error,
      yesButtonText: this.primaryLanguagelabels.error.button_ok
    };
    this.dialog.open(DialougComponent, {
      width: '250px',
      data: body
    });
  }
}
