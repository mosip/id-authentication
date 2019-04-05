import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { DemographicComponent } from './demographic.component';
import { DataStorageService } from 'src/app/core/services/data-storage.service';
import { of } from 'rxjs';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { MaterialModule } from 'src/app/material.module';
import { MatDialog, MatButtonToggleChange } from '@angular/material';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegistrationService } from 'src/app/core/services/registration.service';
import { SharedService } from '../../booking/booking.service';
import { Router } from '@angular/router';
import { MatKeyboardModule } from 'ngx7-material-keyboard';

class MockService {
  use() {}
  url = 'some/url/here';
  getUsers() {
    return of({});
  }

  get() {
    return of({});
  }
}

let router = {
  navigate: jasmine.createSpy('navigate'),
  navigateByUrl: jasmine.createSpy('navigateByUrl')
  // navigateByUrl: 'abc'
};

describe('Demographic Component', () => {
  let component: DemographicComponent;
  let fixture: ComponentFixture<DemographicComponent>;
  let locationData = {
    response: {
      acceptButton: 'Acceptez',
      alertMessageFirst: "Vous serez déconnecté car vous n'acceptez pas les conditions générales",
      alertMessageSecond:
        "Vous serez redirigé vers votre page d'application parce que vous n'acceptez pas les conditions générales",
      alertMessageThird:
        "Vous serez redirigé vers la page de prévisualisation car vous n'acceptez pas les conditions générales.",
      case: 'CONSENTPOPUP',
      checkCondition: 'Cliquez pour accepter les termes et conditions',
      message:
        "Donner son ement pour le stockage et l'utilisation des informations personnelles telles qu'elles sont données",
      subtitle: 'Votre accord',
      title: 'Termes et conditions'
    }
  };
  let locationMessage = 'modifyUser';

  let mockUser = {
    getPrimaryLabels: jasmine.createSpy('getPrimaryLabels').and.returnValue(of(locationData)),
    getSecondaryLanguageLabels: jasmine.createSpy('getSecondaryLanguageLabels').and.returnValue(of(locationData)),
    getLocationMetadataHirearchy: jasmine.createSpy('getLocationMetadataHirearchy').and.returnValue(of(locationData)),
    getGenderDetails: jasmine.createSpy('getGenderDetails').and.returnValue(of(locationData))
  };

  const regServiceStub = {
    getMessage() {
      return of({});
    },
    getUsers: jasmine.createSpy('getUsers').and.returnValue(of(locationMessage)),
    getLoginId: jasmine.createSpy('getLoginId').and.returnValue(of(locationMessage))
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DemographicComponent],
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
        MaterialModule,
        FormsModule,
        ReactiveFormsModule,
        MatKeyboardModule
      ],
      providers: [
        { provide: DataStorageService, useValue: mockUser },
        { provide: RegistrationService, useValue: regServiceStub },
        { provide: Router, useValue: router },
        { provide: SharedService, useClass: MockService },
        { provide: TranslateService, useClass: MockService },
        { provide: MatDialog, useClass: MockService },
        { provide: FormsModule, useClass: MockService },
        { provide: MatKeyboardModule, useClass: MockService }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DemographicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be truthy', () => {
    expect(component).toBeTruthy();
  });

  it('component should be truthy on ngdestroy', () => {
    component.ngOnDestroy();
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('will change a boolean value on the result of preference change method..', () => {
    component.isReadOnly = true;
    fixture.detectChanges();
    // component.message = 'dsa';
    // spyOn(regservice, 'currentMessage').and.returnValue(of('response'"21"));
    expect(component.isReadOnly).toEqual(true);
  });

  it('it will calculate the age based on a date provided', () => {
    let inputDate = new Date('December 17, 1995 03:24:00');
    const x = component.calculateAge(inputDate);
    fixture.detectChanges();
    expect(x).toEqual(23);
  });

  it('should add code value to codevalue array', () => {
    const x = {
      valueCode: 'deepak',
      valueName: 'deepak1',
      languageCode: 'english'
    };

    component.codeValue;
    component.addCodeValue(x);
    expect(component.codeValue.length).toEqual(1);
  });

  it('should change checked value', () => {
    component.checked = false;
    component.onSubmission();
    fixture.detectChanges();
    expect(component.checked).toEqual(true);
  });

  it('on Entity change', () => {
    const entity = {
      code: 'MLE',
      genderName: 'Mâle',
      isActive: true,
      langCode: 'fra'
    };
    let entityArray = [entity, entity];
    let entityArray1 = [entity, entity];
    let event = new MatButtonToggleChange(null, 'MLE');
    // component.checked = false;
    component.onEntityChange([entityArray, entityArray1], event);
    fixture.detectChanges();
    expect(component.codeValue.length).toEqual(4);
  });
});
