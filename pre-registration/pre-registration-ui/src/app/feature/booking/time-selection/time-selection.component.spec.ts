import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeSelectionComponent } from './time-selection.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { RouterTestingModule } from '@angular/router/testing';
import { SharedService } from '../booking.service';
import { of } from 'rxjs';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { ConfigService } from 'src/app/core/services/config.service';

let center = {
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
} 

const response = {
  response: {
    centerDetails: [{
      "TotalAvailable": 1,
      "date": "2019-03-16",
      "displayDate": "16 Mar, 2019",
      "displayDay": "Saturday",
      "holiday": false,
      "inActive": false,
      "showAfternoon": true,
      "timeSlots": [{
        "availability": 1,
        "displayTime": "4:30 - 4:45",
        "fromTime": "16:30:00",
        "tag": "afternoon",
        "toTime": "16:45:00",
        "names": [{fullName: 'Agnitra', preRegId: '1234', registrationCenter: center}]
      }]
    }]
  }
}

const name = [{fullName: 'Agnitra', preRegId: '1234', registrationCenter: center}];

let service: SharedService, mockUsers = {
  getNameList: jasmine.createSpy('getNameList').and.returnValue(name),
  resetNameList: jasmine.createSpy('resetNameList').and.returnValue([])
};

let service1: RegistrationService, mockCenters = {
  getRegCenterId: jasmine.createSpy('getRegCenterId').and.returnValue('1234')
};

let service2: DataStorageService, mockService = {
  getAvailabilityData: jasmine.createSpy('getAvailabilityData').and.returnValue(of(center)),
  getSecondaryLanguageLabels: jasmine.createSpy('getSecondaryLanguageLabels').and.returnValue(of({timeSelection: {booking: {label1: 'hello'}}}))
}

let service3: ConfigService, mockConfig = {
  getConfigByKey: jasmine.createSpy('getConfigByKey').and.returnValue(7)
}

describe('TimeSelectionComponent', () => {
  let component: TimeSelectionComponent;
  let fixture: ComponentFixture<TimeSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeSelectionComponent ],
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
      RouterTestingModule
      ],
      providers: [
        {
          provide: DataStorageService, useValue: mockService
        },
        {
          provide: RegistrationService, useValue: mockCenters
        },
        {
          provide: SharedService, useValue: mockUsers
        },
        {
          provide: ConfigService, useValue: mockConfig
        }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set selected date', () => {
    component.availabilityData = response.response.centerDetails;
    component.names = name;
    component.dateSelected(0);
    fixture.detectChanges();
    expect(component.enableBookButton).toBeTruthy();
  });

  it('should check enable bucket tabs', () => {
    component.availabilityData = response.response.centerDetails;
    component.selectedTile = 0;
    component.enableBucketTabs();
    fixture.detectChanges();
    expect(component.activeTab).toBe('afternoon');
  });

  it('should get selected tab', () => {
    component.availabilityData = response.response.centerDetails;
    component.selectedTile = 0;
    component.tabSelected('afternoon');
    fixture.detectChanges();
    expect(component.activeTab).toBe('afternoon');
  });

  it('should get card selected', () => {
    component.cardSelected(1);
    fixture.detectChanges();
    expect(component.selectedCard).toBe(1);
  });

  it('should test item deleted', () => {
    component.availabilityData = response.response.centerDetails;
    component.selectedTile = 0;
    component.selectedCard = 0;
    component.itemDelete(0);
    fixture.detectChanges();
    expect(component.deletedNames[0].preRegId).toBe(name[0].preRegId);
  });

  it('should test add item', () => {
    component.availabilityData = response.response.centerDetails;
    component.selectedTile = 0;
    component.selectedCard = 0;
    component.deletedNames = name;
    component.addItem(0);
    fixture.detectChanges();
    expect(component.deletedNames.length).toBe(0);
  });

  it('should test format json', () => {
    component.registrationCenterLunchTime = ['14'];
    component.registrationCenter = center.id;
    component.formatJson(response.response.centerDetails);
    fixture.detectChanges();
    expect(component.availabilityData.length).toBe(1);
  });

  it('should test place names in slots', () => {
    component.availabilityData = response.response.centerDetails;
    component.selectedTile = 0;
    component.names = name;
    fixture.detectChanges();
    expect(component.names.length).toBe(0);
  })
});
