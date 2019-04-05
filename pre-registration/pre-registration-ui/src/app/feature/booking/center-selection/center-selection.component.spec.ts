import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CenterSelectionComponent } from './center-selection.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { FormsModule } from '@angular/forms';
import { BookingModule } from '../booking.module';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { SharedService } from '../booking.service';

describe('CenterSelectionComponent', () => {
  let component: CenterSelectionComponent;
  let locationData = {
    response: [{
      "locationHierarchylevel": 2,
      "locationHierarchyName": "city",
      "isActive": true
    }]
  };
  let center = {
    response: [{
      "id": "10002",
      "name": "Rural Municipal Mnasra",
      "centerTypeCode": "REG",
      "addressLine1": "Route De Moulay Bousselham",
      "addressLine2": "Douar Sbih Menacera",
      "addressLine3": "Morroco",
      "latitude": "34.360207",
      "longitude": "-6.550075",
      "locationCode": "14053",
      "holidayLocationCode": "KTA",
      "contactPhone": "753476995",
      "workingHours": "8:00:00",
      "langCode": "eng",
      "numberOfKiosks": 1,
      "perKioskProcessTime": "00:15:00",
      "centerStartTime": "09:00:00",
      "centerEndTime": "17:00:00",
      "timeZone": "(GTM+01:00) CENTRAL EUROPEAN TIME",
      "contactPerson": "John Smith",
      "lunchStartTime": "13:00:00",
      "lunchEndTime": "14:00:00",
      "isActive": true
    }]
  };

  let centers = {
    registrationCenters: [center]
  }


  let service: DataStorageService, mockService = {
    getLocationTypeData: jasmine.createSpy('getLocationTypeData').and.returnValue(of(locationData)),
    recommendedCenters: jasmine.createSpy('recommendedCenters').and.returnValue(of(center)),
    getRegistrationCentersByName: jasmine.createSpy('getRegistrationCentersByName').and.returnValue(of(centers))
  }

  let userService: SharedService, mockUsers = {
    getNameList: jasmine.createSpy('getNameList').and.returnValue(of([{fullName: 'Agn', preId: '1234'}])),
    changeCoordinates: jasmine.createSpy('changeCoordinates').and.returnValue(of([11.111, 11.11])),
    listOfCenters: jasmine.createSpy('listOfCenters').and.returnValue(of([{id: '1001', latitude: 11.111, longitude: 11.11}]))
  }
  let fixture: ComponentFixture<CenterSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ],
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
      FormsModule,
      BookingModule,
      RouterTestingModule
      ], providers: [
        {
          provide: DataStorageService, useValue: mockService
        },
        {
          provide: SharedService, useValue: mockUsers
        }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CenterSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set search flag', () => {
    component.setSearchClick(true);
    fixture.detectChanges();
    expect(component.searchClick).toBeTruthy();
  });

  it('should test onSubmit', () => {
    component.searchText = 'hello';
    component.onSubmit();
    fixture.detectChanges();
    expect(component.searchTextFlag).toBeTruthy();
  });

  it('should set the step', () => {
    component.setStep(1);
    fixture.detectChanges();
    expect(component.step).toBe(1);
  });

  it('should increase step', () => {
    component.setStep(2);
    component.nextStep();
    fixture.detectChanges();
    expect(component.step).toBe(3);
  });

  it('should decrease step', () => {
    component.setStep(2);
    component.prevStep();
    fixture.detectChanges();
    expect(component.step).toBe(1);
  });

  it('should change time format', () => {
    let x = component.changeTimeFormat('09:00');
    console.log(x);
    fixture.detectChanges();
    expect(x).toBe('09:00 am');
  });

  it('should change time format for afternoon', () => {
    let x = component.changeTimeFormat('17:00');
    console.log(x);
    fixture.detectChanges();
    expect(x).toBe('5:00 pm');
  });

  it('should test display results', () => {
    const response = {}
    component.displayResults(response);
    fixture.detectChanges();
    expect(component.showTable).toBeTruthy();
  });

  it('should dispatch coordinates list', () => {
    component.REGISTRATION_CENTRES = [];
    component.dispatchCenterCoordinatesList();
    fixture.detectChanges();
    mockUsers.listOfCenters().subscribe(value => {
      expect(value[0].id).toBe('1001');
    });
  });

  it('should test selected row', () => {
    const row = {};
    component.selectedRow(row);
    fixture.detectChanges();
    expect(component.enableNextButton).toBeTruthy();
  })
});
