import { TestBed, inject } from '@angular/core/testing';

import { DataStorageService } from './data-storage.service';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';
import { BookingModel } from 'src/app/feature/booking/center-selection/booking.model';
import { RequestModel } from 'src/app/shared/models/request-model/RequestModel';

describe('DataStorageService', () => {
  let service: DataStorageService = null;

  let dataStorageService: DataStorageService,
    mockService = {
      getConfig: jasmine.createSpy('getConfig').and.returnValue(of('hello')),
      getGuidelineTemplate: jasmine.createSpy('getGuidelineTemplate').and.returnValue(of('guideline 1')),
      getRegistrationCenterByIdAndLangCode: jasmine
        .createSpy('getRegistrationCenterByIdAndLangCode')
        .and.returnValue(of({ regCenterID: '12345' })),
      recommendedCenters: jasmine.createSpy('recommendedCenters').and.returnValue(of({ regCenterID: '12345' })),
      sendNotification: jasmine.createSpy('sendNotification').and.returnValue(of({ message: 'success' })),
      getSecondaryLanguageLabels: jasmine.createSpy('getSecondaryLanguageLabels').and.returnValue(of('hello')),
      generateQRCode: jasmine.createSpy('generateQRCode').and.returnValue(of(new Blob())),
      copyDocument: jasmine.createSpy('copyDocument').and.returnValue(of({ message: 'success' })),
      deleteFile: jasmine.createSpy('deleteFile').and.returnValue(of({ message: 'success' })),
      getUsers: jasmine.createSpy('getUsers').and.returnValue(of({ users: [{ userId: '12345' }] })),
      getUser: jasmine.createSpy('getUser').and.returnValue(of({ user: { userId: '12345' } })),
      getGenderDetails: jasmine.createSpy('getGenderDetails').and.returnValue(of('male')),
      getTransliteration: jasmine.createSpy('getTransliteration').and.returnValue(of('bon voyage')),
      getUserDocuments: jasmine.createSpy('getUserDocuments').and.returnValue(of({ documents: ['hello.txt'] })),
      getLocationImmediateHierearchy: jasmine
        .createSpy('getLocationImmediateHierearchy')
        .and.returnValue(of({ country: 'morroco' })),
      getLocationMetadataHirearchy: jasmine
        .createSpy('getLocationMetadataHirearchy')
        .and.returnValue(of({ country: 'morroco' })),
      makeBooking: jasmine.createSpy('makeBooking').and.returnValue(of('success')),
      getAvailabilityData: jasmine.createSpy('getAvailabilityData').and.returnValue(of({ available: 4 })),
      getLocationTypeData: jasmine.createSpy('getLocationTypeData').and.returnValue(of({ country: 'morroco' })),
      getRegistrationCentersByName: jasmine
        .createSpy('getRegistrationCentersByName')
        .and.returnValue(of({ registrationCenter: { centerId: '1001' } })),
      getNearbyRegistrationCenters: jasmine
        .createSpy('getNearbyRegistrationCenters')
        .and.throwError('No registration centers found'),
      cancelAppointment: jasmine.createSpy('cancelAppointment').and.returnValue(of('success')),
      addUser: jasmine.createSpy('addUser').and.returnValue(of('successfully inserted')),
      deleteRegistration: jasmine.createSpy('deleteRegistration').and.returnValue(of('success')),
      sendFile: jasmine.createSpy('sendFile').and.throwError('HDFS not working')
    };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        {
          provide: DataStorageService,
          useValue: mockService
        }
      ]
    });
  });

  beforeEach(inject([DataStorageService], dataStorageService => {
    service = dataStorageService;
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('gets configs', () => {
    let response = null;
    service.getConfig().subscribe(value => {
      response = value;
    });

    expect(response).toBeDefined();
    expect(response).toBe('hello');
  });

  it('gets guideline template', () => {
    let response = null;
    service.getGuidelineTemplate().subscribe(value => {
      response = value;
    });

    expect(response).toBeDefined();
    expect(response).toBe('guideline 1');
  });

  it('gets registrationCenter by Id and language', () => {
    let response = null;
    service.getRegistrationCenterByIdAndLangCode('12345', 'ara').subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
    expect(response.regCenterID).toBe('12345');
  });

  it('gets recommended registrationCenter', () => {
    let response = null;
    service.recommendedCenters('ara', 4, ['abcd']).subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
    expect(response.regCenterID).toBe('12345');
  });

  it('sendNotification', () => {
    let response = null;
    const data = new FormData();
    service.sendNotification(data).subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
    expect(response.message).toBe('success');
  });

  it('getSecondaryLanguageLabels', () => {
    let response = null;
    service.getSecondaryLanguageLabels('ara').subscribe(value => {
      response = value;
    });
    expect(response).toBe('hello');
  });

  it('generateQRCode', () => {
    let response = null;
    service.generateQRCode('hello').subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
  });

  it('copyDocument', () => {
    let response = null;
    service.copyDocument('12345', '54321').subscribe(value => {
      response = value;
    });
    expect(response).toBeDefined();
    expect(response.message).toBe('success');
  });

  it('deleteFile', () => {
    let response = null;
    service.deleteFile('12345').subscribe(value => {
      response = value;
    });
    expect(response.message).toBe('success');
  });

  it('getUsers', () => {
    let response = null;
    service.getUsers('12345').subscribe(value => {
      response = value;
    });
    expect(response.users[0].userId).toBe('12345');
  });

  it('getUser', () => {
    let response = null;
    service.getUser('12345').subscribe(value => {
      response = value;
    });
    expect(response.user.userId).toBe('12345');
  });

  it('getGenderDetails', () => {
    let response = null;
    service.getGenderDetails().subscribe(value => {
      response = value;
    });
    expect(response).toBe('male');
  });

  it('getTransliteration', () => {
    let response = null;
    const request = {};
    service.getTransliteration(request).subscribe(value => {
      response = value;
    });
    expect(response).toBe('bon voyage');
  });

  it('getUserDocuments', () => {
    let response = null;
    service.getUserDocuments('12345').subscribe(value => {
      response = value;
    });
    expect(response.documents.length).toBe(1);
  });

  it('getLocationImmediateHierearchy', () => {
    let response = null;
    service.getLocationImmediateHierearchy('eng', 'country').subscribe(value => {
      response = value;
    });
    expect(response.country).toBe('morroco');
  });

  it('getLocationMetadataHirearchy', () => {
    let response = null;
    service.getLocationMetadataHirearchy('country').subscribe(value => {
      response = value;
    });
    expect(response.country).toBe('morroco');
  });

  it('makeBooking', () => {
    let response = null;
    const req = new BookingModel('1234', '1001', '2019-03-08', '09:00:00', '09:15:00');
    const request = new RequestModel('aaa', req);
    service.makeBooking(request).subscribe(value => {
      response = value;
    });
    expect(response).toBe('success');
  });

  it('getAvailabilityData', () => {
    let response = null;
    service.getAvailabilityData('1001').subscribe(value => {
      response = value;
    });
    expect(response.available).toBe(4);
  });

  it('getLocationTypeData', () => {
    let response = null;
    service.getLocationTypeData().subscribe(value => {
      response = value;
    });
    expect(response.country).toBe('morroco');
  });

  it('getRegistrationCentersByName', () => {
    let response = null;
    service.getRegistrationCentersByName('country', 'morroco').subscribe(value => {
      response = value;
    });
    expect(response.registrationCenter.centerId).toBe('1001');
  });

  it('getNearbyRegistrationCenters throws error', () => {
    expect(() => {
      service.getNearbyRegistrationCenters({});
    }).toThrow(new Error('No registration centers found'));
  });

  it('cancelAppointment', () => {
    let response = null;
    const req = new BookingModel('1234', '1001', '2019-03-08', '09:00:00', '09:15:00');
    const request = new RequestModel('aaa', req);
    service.cancelAppointment(request, '111').subscribe(value => {
      response = value;
    });
    expect(response).toBe('success');
  });

  it('addUser', () => {
    let response = null;
    service.addUser({}).subscribe(value => {
      response = value;
    });
    expect(response).toBe('successfully inserted');
  });

  it('deleteRegistration', () => {
    let response = null;
    service.deleteRegistration('12345').subscribe(value => {
      response = value;
    });
    expect(response).toBe('success');
  });

  it('sendFile', () => {
    const formData = new FormData();
    expect(() => {
      service.sendFile(formData, '111');
    }).toThrowError('HDFS not working');
  });
});
