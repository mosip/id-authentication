import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { TranslateService, TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { SharedService } from '../../booking/booking.service';
import { FileUploadComponent } from './file-upload.component';
import * as appConstants from './../../../app.constants';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient } from 'selenium-webdriver/http';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialModule } from 'src/app/material.module';

describe('FileUploadComponent', () => {
  let component: FileUploadComponent;
  let fixture: ComponentFixture<FileUploadComponent>;
  beforeEach(() => {
    const activatedRouteStub = {};
    const routerStub = {
      url: {
        split: () => ({ pop: () => ({}), push: () => ({}), join: () => ({}) })
      },
      navigateByUrl: () => ({})
    };
    const domSanitizerStub = { bypassSecurityTrustResourceUrl: () => ({}) };
    const registrationServiceStub = {
      getLoginId: () => ({}),
      getSameAs: () => ({}),
      getUsers: () => ({ length: {} }),
      getUser: () => ({}),
      setDocumentCategories: () => ({}),
      updateUser: () => ({}),
      setSameAs: () => ({}),
      changeMessage: () => ({})
    };
    const dataStorageServiceStub = {
      getApplicantType: () => ({ subscribe: () => ({}) }),
      getDocumentCategories: () => ({ subscribe: () => ({}) }),
      getUsers: () => ({ subscribe: () => ({}) }),
      deleteFile: () => ({ subscribe: () => ({}) }),
      sendFile: () => ({ subscribe: () => ({}) }),
      copyDocument: () => ({ subscribe: () => ({}) })
    };
    const translateServiceStub = { use: () => ({}) };
    const sharedServiceStub = {
      getAllApplicants: () => ({}),
      addApplicants: () => ({})
    };
    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [FileUploadComponent],
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
        MaterialModule
      ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: Router, useValue: routerStub },
        { provide: DomSanitizer, useValue: domSanitizerStub },
        { provide: RegistrationService, useValue: registrationServiceStub },
        { provide: DataStorageService, useValue: dataStorageServiceStub },
        { provide: TranslateService, useValue: translateServiceStub },
        { provide: SharedService, useValue: sharedServiceStub }
      ]
    });
    fixture = TestBed.createComponent(FileUploadComponent);
    component = fixture.componentInstance;
  });
  it('can load instance', () => {
    expect(component).toBeTruthy();
  });
  it('sameAsselected defaults to: false', () => {
    expect(component.sameAsselected).toEqual(false);
  });
  it('users defaults to: []', () => {
    expect(component.users).toEqual([]);
  });
  // it('JsonString defaults to: appConstants.DOCUMENT_UPLOAD_REQUEST_DTO', () => {
  //   expect(component.JsonString).toEqual(appConstants.DOCUMENT_UPLOAD_REQUEST_DTO);
  // });
  it('browseDisabled defaults to: true', () => {
    expect(component.browseDisabled).toEqual(true);
  });
  it('step defaults to: 0', () => {
    expect(component.step).toEqual(0);
  });
  it('multipleApplicants defaults to: false', () => {
    expect(component.multipleApplicants).toEqual(false);
  });
  it('allApplicants defaults to: []', () => {
    expect(component.allApplicants).toEqual([]);
  });
  // describe('ngOnInit', () => {
  //   it('makes expected calls', () => {
  //     const registrationServiceStub: RegistrationService = fixture.debugElement.injector.get(RegistrationService);
  //     const sharedServiceStub: SharedService = fixture.debugElement.injector.get(SharedService);
  //     spyOn(component, 'getAllApplicants');
  //     spyOn(component, 'getApplicantsName');
  //     spyOn(component, 'viewFirstFile');
  //     spyOn(registrationServiceStub, 'getLoginId');
  //     spyOn(registrationServiceStub, 'getSameAs');
  //     spyOn(registrationServiceStub, 'getUsers');
  //     spyOn(registrationServiceStub, 'getUser');
  //     spyOn(sharedServiceStub, 'getAllApplicants');
  //     component.ngOnInit();
  //     expect(component.getAllApplicants).toHaveBeenCalled();
  //     expect(component.getApplicantsName).toHaveBeenCalled();
  //     expect(component.viewFirstFile).toHaveBeenCalled();
  //     expect(registrationServiceStub.getLoginId).toHaveBeenCalled();
  //     expect(registrationServiceStub.getSameAs).toHaveBeenCalled();
  //     expect(registrationServiceStub.getUsers).toHaveBeenCalled();
  //     expect(registrationServiceStub.getUser).toHaveBeenCalled();
  //     expect(sharedServiceStub.getAllApplicants).toHaveBeenCalled();
  //   });
  // });
  // describe('getAllApplicants', () => {
  //   it('makes expected calls', () => {
  //     const dataStorageServiceStub: DataStorageService = fixture.debugElement.injector.get(DataStorageService);
  //     const sharedServiceStub: SharedService = fixture.debugElement.injector.get(SharedService);
  //     spyOn(dataStorageServiceStub, 'getUsers');
  //     spyOn(sharedServiceStub, 'addApplicants');
  //     component.getAllApplicants();
  //     expect(dataStorageServiceStub.getUsers).toHaveBeenCalled();
  //     expect(sharedServiceStub.addApplicants).toHaveBeenCalled();
  //   });
  // });
  // describe('viewFirstFile', () => {
  //   it('makes expected calls', () => {
  //     spyOn(component, 'viewFile');
  //     component.viewFirstFile();
  //     expect(component.viewFile).toHaveBeenCalled();
  //   });
  // });
  // describe('viewLastFile', () => {
  //   it('makes expected calls', () => {
  //     spyOn(component, 'viewFile');
  //     component.viewLastFile();
  //     expect(component.viewFile).toHaveBeenCalled();
  //   });
  // });
  describe('removeFilePreview', () => {
    it('makes expected calls', () => {
      const domSanitizerStub: DomSanitizer = fixture.debugElement.injector.get(DomSanitizer);
      spyOn(domSanitizerStub, 'bypassSecurityTrustResourceUrl');
      component.removeFilePreview();
      expect(domSanitizerStub.bypassSecurityTrustResourceUrl).toHaveBeenCalled();
    });
  });
  describe('onBack', () => {
    it('makes expected calls', () => {
      const routerStub: Router = fixture.debugElement.injector.get(Router);
      const registrationServiceStub: RegistrationService = fixture.debugElement.injector.get(RegistrationService);
      spyOn(routerStub, 'navigateByUrl');
      spyOn(registrationServiceStub, 'changeMessage');
      component.onBack();
      expect(routerStub.navigateByUrl).toHaveBeenCalled();
      expect(registrationServiceStub.changeMessage).toHaveBeenCalled();
    });
  });
  describe('onNext', () => {
    it('makes expected calls', () => {
      const routerStub: Router = fixture.debugElement.injector.get(Router);
      spyOn(routerStub, 'navigateByUrl');
      component.onNext();
      expect(routerStub.navigateByUrl).toHaveBeenCalled();
    });
  });
  describe('nextFile', () => {
    it('makes expected calls', () => {
      spyOn(component, 'viewFileByIndex');
      component.nextFile(0);
      expect(component.viewFileByIndex).toHaveBeenCalled();
    });
  });
  describe('previousFile', () => {
    it('makes expected calls', () => {
      spyOn(component, 'viewFileByIndex');
      component.previousFile(0);
      expect(component.viewFileByIndex).toHaveBeenCalled();
    });
  });
});
