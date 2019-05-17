import { TestBed, inject } from '@angular/core/testing';

import { GetContactService } from './get-contact.service';

describe('GetContactService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GetContactService]
    });
  });

  it('should be created', inject([GetContactService], (service: GetContactService) => {
    expect(service).toBeTruthy();
  }));
});
