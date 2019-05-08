import { TestBed, inject } from '@angular/core/testing';

import { RegistrationService } from './registration.service';

describe('RegistrationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RegistrationService]
    });
  });

  it('should be created', inject([RegistrationService], (service: RegistrationService) => {
    expect(service).toBeTruthy();
  }));

  it('flushes the users array', inject([RegistrationService], (service: RegistrationService) => {
    service.flushUsers();
    expect(service.getUsers().length).toBe(0);
  }));

  it('changes the message', inject([RegistrationService], (service: RegistrationService) => {
    const x = {
      name: 'Agnitra'
    };
    service.changeMessage(x);
    service.currentMessage.subscribe(message => {
      expect(message).toBe(x);
    });
  }));

  it('Sets and gets the login ID', inject([RegistrationService], (service: RegistrationService) => {
    const x = '12345';
    service.setLoginId(x);
    expect(service.getLoginId()).toBe(x);
  }));

  it('adds an user', inject([RegistrationService], (service: RegistrationService) => {
    const x = {};
    service.addUser(x);
    expect(service.getUser(0)).toBe(x);
  }));

  it('adds a list of users', inject([RegistrationService], (service: RegistrationService) => {
    const x = [{}, {}];
    service.addUsers(x);
    expect(service.getUsers()[0]).toBe(x[0]);
  }));

  it('Updates an user', inject([RegistrationService], (service: RegistrationService) => {
    const x = {};
    service.addUser(x);
    const updatedX = {
      preRegId: '12345'
    };
    service.updateUser(0, updatedX);
    expect(service.getUser(0)).toBe(updatedX);
  }));

  it('deletes an user', inject([RegistrationService], (service: RegistrationService) => {
    const x = {};
    service.addUser(x);
    service.deleteUser(0);
    expect(service.getUsers().length).toBe(0);
  }));

  it('gets the list of files of the user', inject([RegistrationService], (service: RegistrationService) => {
    const x = {
      files: ['hello.txt']
    };
    service.addUser(x);
    const files = service.getUserFiles(0);
    expect(files.length).toBe(x.files.length);
  }));

  it('sets and gets registration center ID', inject([RegistrationService], (service: RegistrationService) => {
    const x = '12345';
    service.setRegCenterId(x);
    expect(service.getRegCenterId()).toBe(x);
  }));

  it('sets sameAs', inject([RegistrationService], (service: RegistrationService) => {
    const x = 'aaaa';
    service.setSameAs(x);
    expect(service.sameAs).toBe(x);
  }));

  it('get sameAs', inject([RegistrationService], (service: RegistrationService) => {
    const x = 'aaaa';
    service.setSameAs(x);
    expect(service.getSameAs()).toBe(x);
  }));
});
