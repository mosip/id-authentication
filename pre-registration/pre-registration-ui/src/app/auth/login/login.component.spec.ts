import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../auth.service';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { of } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SharedModule } from 'src/app/shared/shared.module';
import { ConfigService } from 'src/app/core/services/config.service';

describe('LoginComponent', () => {
  let configs = {
    'mosip.default.dob.day': '01',
    'mosip.default.dob.month': '01',
    'mosip.kernel.otp.default-length': '6',
    'mosip.kernel.otp.expiry-time': '120',
    'mosip.kernel.otp.validation-attempt-threshold': '3',
    'mosip.kernel.pin.length': '6',
    'mosip.kernel.sms.number.length': '10',
    'mosip.login.mode': 'email,mobile',
    'mosip.primary-language': 'ara',
    'mosip.regex.CNIE': '^([0-9]{10,30})$',
    'mosip.regex.DOB': '^d{4}/([0]d|1[0-2])/([0-2]d|3[01])$',
    'mosip.regex.email': '^[w-+]+(.[w]+)*@[w-]+(.[w]+)*(.[a-z]{2,})$',
    'mosip.regex.phone': '^([6-9]{1})([0-9]{9})$',
    'mosip.regex.postalCode': '^[(?i)A-Z0-9]{6}$',
    'mosip.regex.textField': '',
    'mosip.secondary-language': 'fra',
    'mosip.supported-languages': 'eng,ara,fra',
    'preregistration.auto.logout': '10',
    'preregistration.availability.noOfDays': '7',
    'preregistration.availability.sync': '9',
    'preregistration.max.file.size': '1',
    'preregistration.nearby.centers': '2000',
    'preregistration.recommended.centers.locCode': '4',
    'preregistration.timespan.cancel': '24',
    'preregistration.timespan.rebook': '24',
    'preregistration.workflow.booking': 'true/false ',
    'preregistration.workflow.demographic': 'true/false ',
    'preregistration.workflow.documentupload': 'true/false '
  };

  const labels = {
    login: {
      logout_msg: 'hello'
    },
    message: {
      login: {
        msg3: 'hello'
      }
    }
  };

  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  let service: DataStorageService,
    mockService = {
      getConfig: jasmine.createSpy('getConfig').and.returnValue(of(configs)),
      getSecondaryLanguageLabels: jasmine.createSpy('getSecondaryLanguageLabels').and.returnValue(of(labels)),
      sendOtp: jasmine.createSpy('sendOtp').and.returnValue(of({})),
      verifyOtp: jasmine.createSpy('verifyOtp').and.returnValue(of({ errors: null }))
    };

  let configService: ConfigService,
    mockConfigs = {
      setConfig: jasmine.createSpy('setConfig').and.returnValue(of(configs)),
      getConfigByKey: jasmine.createSpy('getConfigByKey').and.returnValue('fra')
    };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule,
        MaterialModule,
        RouterTestingModule,
        FormsModule,
        BrowserAnimationsModule,
        SharedModule
      ],
      providers: [
        AuthService,
        {
          provide: DataStorageService,
          useValue: mockService
        },
        {
          provide: ConfigService,
          useValue: mockConfigs
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate login Id', () => {
    component.inputContactDetails = '9748107386';
    component.loginIdValidator();
    fixture.detectChanges();
    expect(component.errorMessage).toBe(undefined);
  });

  it('should set timer', () => {
    component.setTimer();
    fixture.detectChanges();
    expect(component.seconds).toBe('00');

    component.setTimer();
    fixture.detectChanges();
    expect(component.minutes).toBe('02');
  });

  it('should change language to english', () => {
    component.selectedLanguage = 'English';
    component.changeLanguage();
    fixture.detectChanges();
    expect(localStorage.getItem('langCode')).toBe('fra');
  });

  it('should change the language to arabic', () => {
    component.selectedLanguage = 'Arabic';
    component.changeLanguage();
    fixture.detectChanges();
    expect(localStorage.getItem('langCode')).toBe('fra');
  });

  it('should change the language to french', () => {
    component.selectedLanguage = 'French';
    component.changeLanguage();
    fixture.detectChanges();
    expect(localStorage.getItem('langCode')).toBe('fra');
  });

  it('should test show verify', () => {
    component.inputOTP = '123456';
    component.showVerifyBtn();
    fixture.detectChanges();
    expect(component.showResend).toBeTruthy();
    expect(component.showVerify).toBeFalsy();
  });

  it('should load languages', () => {
    component.loadLanguagesWithConfig();
    fixture.detectChanges();
    expect(component.showSpinner).toBeFalsy();
  });

  it('should test disply otp message', () => {
    localStorage.setItem('langCode', 'ara');
    component.showOtpMessage();
    expect(localStorage.getItem('langCode')).toBe('ara');
  });

  it('Should test set language direction', () => {
    component.setLanguageDirection('ara', 'fra');
    fixture.detectChanges();
    expect(component.dir).toBe('rtl');
  });
});
