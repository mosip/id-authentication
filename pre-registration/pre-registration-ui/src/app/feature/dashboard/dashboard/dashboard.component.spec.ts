import { TestBed, ComponentFixture, async, fakeAsync, inject } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { DashBoardComponent } from './dashboard.component';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { of } from 'rxjs';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { MaterialModule } from 'src/app/material.module';
import { Applicant } from 'src/app/shared/models/dashboard-model/dashboard.modal';
import { MatCheckboxChange } from '@angular/material';
import { SharedService } from '../../booking/booking.service';
import { Routes, Router } from '@angular/router';
import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import Utils from 'src/app/app.util';
import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';

@Component({
  template: ''
})
class DummyComponent {}
const routes: Routes = [{ path: 'pre-registration/summary/acknowledgement', component: DummyComponent }];

// class RouterStub {
//   url = 'pre-registration/summary/acknowledgement';

//   navigateByUrl(url: string) {
//     return url;
//   }
// }

class SharedServiceStub {
  addNameList(applicant: NameList) {
    return applicant.fullName;
  }

  flushNameList() {
    return 0;
  }

  addApplicants(applicant) {
    return applicant;
  }
}

let router = {
  navigate: jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
  // navigateByUrl: 'abc'
};

// const router = {
//   navigateByUrl: 'gkjhk'
// };

// let sharedService: SharedService,
//     mockUsers = {
//       getNameList: jasmine.createSpy('getNameList').and.returnValue(of([{ fullName: 'Agn', preId: '1234' }])),
//       addNameList: jasmine.createSpy('addtoNameList').and.callThrough()
//     };
class UtilStub implements Utils {
  static getURL(currentURL: string, nextRoute: string, numberofPop = 1) {
    return currentURL;
  }
}

class MockService {
  use() {}
  url = 'some/url/here';
  getUsers(preId: string) {
    let applicant = {
      err: null,
      status: true,
      resTime: '2019-03-12T13:17:28.276Z',
      response: [
        {
          preRegistrationId: '29564951460821',
          fullname: [
            {
              language: 'fra',
              value: 'test1'
            },
            {
              language: 'ara',
              value: 'تِست١'
            }
          ],
          statusCode: 'Booked',
          bookingRegistrationDTO: {
            registrationCenterId: '123',
            appointment_date: '12/12/1993',
            time_slot_from: '09:30',
            time_slot_to: '09:45'
          },
          postalCode: '212332'
        }
      ]
    };

    return of([applicant]);
  }

  getSecondaryLanguageLabels() {
    return of({});
  }

  get() {
    return of({});
  }
}

describe('Dashboard Component', () => {
  let component: DashBoardComponent;
  let fixture: ComponentFixture<DashBoardComponent>;
  let utilsqwe;
  // let location: Location;
  // let router: Router;
  // let util: Utils;

  // let dashboard: DashBoardComponent,
  //   mockUDashboard = {
  //     addNameList: jasmine.createSpy('addtoNameList').and.callThrough()
  //   };

  // spyOn<any>(component, 'createAppointmentDateTime').and.returnValue('12/12/1993');

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DashBoardComponent, DummyComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule,
        RouterTestingModule.withRoutes(routes),
        MaterialModule
      ],
      providers: [
        { provide: DataStorageService, useClass: MockService },
        // { provide: Router, useClass: RouterStub },
        { provide: Utils, useClass: UtilStub },
        { provide: SharedService, useClass: SharedServiceStub },
        { provide: Router, useValue: router }
        // { provide: DashBoardComponent, useValue: mockUDashboard }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    // router = TestBed.get(Router);
    // location = TestBed.get(Location);
    // router.initialNavigation();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should check onselect user', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    component.onSelectUser(applicant, new MatCheckboxChange());
    expect(component.disableModifyAppointmentButton).toBeTruthy();
  });

  it('should add user', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    let event = new MatCheckboxChange();
    event.checked = true;
    component.onSelectUser(applicant, event);
    fixture.detectChanges();
    console.log('selectedUsers' + component);
    expect(component.disableModifyAppointmentButton).toBeFalsy();
  });

  it('user length', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    let event = new MatCheckboxChange();
    event.checked = true;
    component.onSelectUser(applicant, event);
    fixture.detectChanges();
    expect(component.selectedUsers.length).toBeGreaterThan(0);
  });

  it('event should be true', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    let event = new MatCheckboxChange();
    event.checked = true;
    component.onSelectUser(applicant, event);
    expect(component.selectedUsers).toContain(applicant);
  });

  it('modify appointment', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    component.selectedUsers = [applicant, applicant];
    // UtilStub.getURL('qbc', 'gh');
    expect(router.navigateByUrl).toBeDefined();
    expect(component.selectedUsers.length).toBe(2);
  });

  it('add applicant', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    component.addtoNameList(applicant);
    console.log('component.selectedUsers.length', component.selectedUsers.length);

    expect(component.selectedUsers.length).toBe(0);
  });

  it('should create applicant', () => {
    localStorage.setItem('langCode', 'ara');
    let applicants = {
      err: null,
      status: true,
      resTime: '2019-03-12T13:17:28.276Z',
      response: [
        {
          preRegistrationId: '29564951460821',
          fullname: [
            {
              language: 'fra',
              value: 'test1'
            },
            {
              language: 'ara',
              value: 'تِست١'
            }
          ],
          statusCode: 'Pending_Appointment',
          bookingRegistrationDTO: null,
          postalCode: '212332'
        }
      ]
    };

    // component.createApplicant([applicant], 0);
    expect(component.createApplicant(applicants, 0).applicationID).toBe('29564951460821');
  });

  it('should create applicant with booked status', () => {
    let applicant = {
      preRegistrationId: '29564951460821',
      fullname: [
        {
          language: 'fra',
          value: 'test1'
        },
        {
          language: 'ara',
          value: 'تِست١'
        }
      ],
      statusCode: 'Booked',
      bookingRegistrationDTO: {
        registrationCenterId: '123',
        appointment_date: '12/12/1993',
        time_slot_from: '09:30',
        time_slot_to: '09:45'
      },
      postalCode: '212332'
    };
    localStorage.setItem('langCode', 'ara');
    let applicants = {
      err: null,
      status: true,
      resTime: '2019-03-12T13:17:28.276Z',
      response: [applicant, applicant]
    };

    component.loginId = '1243';

    // component.createApplicant([applicant], 0);
    expect(router.navigate).toHaveBeenCalledWith(['/']);
    expect(component.createApplicant(applicants, 0).applicationID).toBe('29564951460821');
  });

  it('ON Select User', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    let event = {} as MatCheckboxChange;
    event.checked = true;
    component.onSelectUser(applicant, event);
    expect(component.disableModifyAppointmentButton).toBe(false);
  });

  it('ON Acknowledgment view', () => {
    const applicant: Applicant = {
      applicationID: '123',
      appointmentDateTime: '1234',
      name: 'shahsnak',
      nameInSecondaryLanguage: 'asdf',
      postalCode: '123',
      regDto: 'regDTO',
      status: 'true'
    };
    component.onAcknowledgementView(applicant);
    expect(router.navigateByUrl).toHaveBeenCalled();
  });

  // it('get user', () => {
  //   component.getUsers();
  //   component.loginId = null;
  //   expect(component.users.length).toBe(0);
  // });

  // it('add to Name List', () => {
  //   const applicant: NameList = {
  //     bookingData: '12/12/2017',
  //     fullName: 'shashank',
  //     fullNameSecondaryLang: 'fre',
  //     postalCode: '123',
  //     preRegId: '123',
  //     regDto: '',
  //     registrationCenter: 'blr',
  //     status: 'true'
  //   };
  //   // component.selectedUsers = [applicant, applicant];

  //   let service = new SharedService();
  //   // let name = service.addNameList(applicant);
  //   // SharedService.
  //   // console.log('asdf', component.selectedUsers);

  //   expect(service.addNameList(applicant)).toBeUndefined();
  //   // expect(component.selectedUsers.length).toBe(2);
  // });

  // it('should go to url', async(
  //   inject([Router, Location], (router: Router, location: Location) => {
  //     fixture.detectChanges();
  //     const applicant: Applicant = {
  //       applicationID: '123',
  //       appointmentDateTime: '1234',
  //       name: 'shahsnak',
  //       nameInSecondaryLanguage: 'asdf',
  //       postalCode: '123',
  //       regDto: 'regDTO',
  //       status: 'true'
  //     };
  //     component.onAcknowledgementView(applicant);

  //     // fixture.debugElement.query(By.css('a')).nativeElement.click();
  //     fixture.whenStable().then(() => {
  //       // expect(location.pathname()).toEqual('/settings/testing/edit/1');
  //       expect(location.path()).toBe('/pre-registration/summary/acknowledgement');
  //       console.log('after expect');
  //     });
  //   })
  // ));

  // it('navigate to "" redirects you to /home', fakeAsync(() => {
  //   // const spy = spyOn(router, 'navigateByUrl');
  //   // const spyUtil = spyOn(util, 'getURL');

  //   const applicant: Applicant = {
  //     applicationID: '123',
  //     appointmentDateTime: '1234',
  //     name: 'shahsnak',
  //     nameInSecondaryLanguage: 'asdf',
  //     postalCode: '123',
  //     regDto: 'regDTO',
  //     status: 'true'
  //   };

  // component.onAcknowledgementView(applicant);

  // const url = spy.calls.first().args[0];
  // router.navigateByUrl('pre-registration/summary/acknowledgement');
  // .then(() => {
  //   expect(location.path).toBe('/pre-registration/summary/acknowledgement');
  // });
  // expect(url).toBe('/pre-registration/summary/acknowledgement');
  // }));
  // ));

  // it('view acknowledgement', inject([Router], (router: Router) => {
  //   const spy = spyOn(router, 'navigateByUrl');
  //   const applicant: Applicant = {
  //     applicationID: '123',
  //     appointmentDateTime: '1234',
  //     name: 'shahsnak',
  //     nameInSecondaryLanguage: 'asdf',
  //     postalCode: '123',
  //     regDto: 'regDTO',
  //     status: 'true'
  //   };
  //   component.onAcknowledgementView(applicant);
  //   const url = spy.calls.first().args[0];
  //   expect(url).toBe('/pre-registration/summary/acknowledgement');
  //   // expect(component.selectedUsers.length).toBe(0);
  // }));
});
