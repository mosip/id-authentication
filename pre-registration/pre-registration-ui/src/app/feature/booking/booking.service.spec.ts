import { TestBed, inject } from '@angular/core/testing';

import { SharedService } from './booking.service';
import { NameList } from 'src/app/shared/models/demographic-model/name-list.modal';

describe('SharedService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SharedService]
    });
  });

  it('should be created', inject([SharedService], (service: SharedService) => {
    expect(service).toBeTruthy();
  }));

  it('should be created', inject([SharedService], (service: SharedService) => {
    service.flushNameList();
    expect(service.getNameList().length).toBe(0);
  }));

  it('sets current coordinates', inject([SharedService], (service: SharedService) => {
    const x = {lat: 72.11, lon: 12.11};
    service.changeCoordinates(x);
    service.currentCoordinates.subscribe(value => {
      expect(value).toBe(x);
    });
  }));

  it('sets list of coordinates for registration centers', inject([SharedService], (service: SharedService) => {
    const x = {lat: 72.11, lon: 12.11};
    service.listOfCenters(x);
    service.coordinatesList.subscribe(value => {
      expect(value).toBe(x);
    });
  }));

  it('adds and gets name to and from the name list', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    service.addNameList(x);
    expect(service.getNameList()[0]).toBe(x);
  }));

  it('adds and gets applicants', inject([SharedService], (service: SharedService) => {
    const user: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    const x = {
      response: [{
        ...user
      }]
    };
    service.addApplicants(x);
    const c = service.getAllApplicants();
    expect(c[0]).toBe(x.response[0]);
  }));

  it('should reset the namelist', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    service.addNameList(x);
    service.resetNameList();
    expect(service.getNameList().length).toBe(0);
  }));

  it('should update the namelist', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    const updatedX: NameList = {
      fullName: 'Agnitra Banerjee',
      preRegId: '1234'
    };
    service.addNameList(x);
    service.updateNameList(0, updatedX)
    expect(service.getNameList()[0]).toBe(updatedX);
  }));

  it('should return index by PreID from the namelist', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    service.addNameList(x);
    const index = service.getIndexByPreId(x.preRegId)
    expect(service.getNameList()[index]).toBe(x);
  }));

  it('should update registration center data', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    service.addNameList(x);
    const list = service.updateRegistrationCenterData(x.preRegId, {name: 'Bangalore'});
    const index = service.getIndexByPreId(x.preRegId); 
    expect(list[index].registrationCenter.name).toBe('Bangalore');
  }));

  it('should update booking details data', inject([SharedService], (service: SharedService) => {
    const x: NameList = {
      fullName: 'Agnitra',
      preRegId: '1234'
    };
    service.addNameList(x);
    const list = service.updateBookingDetails(x.preRegId, {booking_date: '18-03-2019'});
    const index = service.getIndexByPreId(x.preRegId); 
    expect(list[index].bookingData.booking_date).toBe('18-03-2019');
  }));

});
