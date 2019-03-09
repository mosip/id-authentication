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

describe('CenterSelectionComponent', () => {
  let component: CenterSelectionComponent;
  let locationData = [
    {
      "locationHierarchylevel": 2,
      "locationHierarchyName": "city",
      "isActive": true
    }
  ];
  let center = [
    {
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
  ];
  let service: DataStorageService, mockService = {
    getLocationTypeData: jasmine.createSpy('getLocationTypeData').and.returnValue(of(locationData)),
    recommendedCenters: jasmine.createSpy('recommendedCenters').and.returnValue(of(center))
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
});
