import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';

import { DialougComponent } from './dialoug.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { AuthService } from 'src/app/auth/auth.service';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DataStorageService } from 'src/app/core/services/data-storage.service';

class MockService {
  use() {}
  url = 'some/url/here';
}

describe('DialougComponent', () => {
  let component: DialougComponent;
  let fixture: ComponentFixture<DialougComponent>;
  let data = { case: 'MESSAGE' };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DialougComponent],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule,
        RouterTestingModule,
        FormsModule,
        MaterialModule,
        BrowserAnimationsModule
      ],
      providers: [
        {
          provide: Router,
          useValue: {
            url: '/path'
          }
        },
        { provide: DataStorageService, useClass: MockService },
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: data },
        AuthService,
        RegistrationService
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialougComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate mobile number', () => {
    component.applicantNumber = '9748123455';
    component.validateMobile();
    fixture.detectChanges();
    expect(component.invalidApplicantNumber).toBeFalsy();

    component.applicantNumber = '9748125';
    component.validateMobile();
    fixture.detectChanges();
    expect(component.invalidApplicantNumber).toBeTruthy();
  });

  it('should validate email address', () => {
    component.applicantEmail = 'agneetra10@gmail.com';
    component.validateEmail();
    fixture.detectChanges();
    expect(component.invalidApplicantEmail).toBeFalsy();

    component.applicantEmail = 'agneetra10@.com';
    component.validateEmail();
    fixture.detectChanges();
    expect(component.invalidApplicantEmail).toBeTruthy();
  });

  it('checks the ischecked parameter', () => {
    const x = component.isChecked;
    component.onSelectCheckbox();
    fixture.detectChanges();
    expect(component.isChecked).toBe(!x);
  });

  it('triggers ngOnInit', () => {
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.input.case).toBe(data.case);
  });

  it('checks user redirection', inject(
    [AuthService, RegistrationService],
    (service: AuthService, regService: RegistrationService) => {
      localStorage.setItem('newApplicant', 'true');
      component.userRedirection();
      fixture.detectChanges();
      expect(service.token).toBe(null);

      localStorage.setItem('newApplicant', 'false');
      regService.changeMessage({ modifyUserFromPreview: 'false' });
      component.userRedirection();
      fixture.detectChanges();
      expect(component.checkCondition).toBeDefined();
    }
  ));
});
