import { TestBed } from '@angular/core/testing';

import { BookingDeactivateGuardService } from './booking-deactivate-guard.service';

describe('BookingDeactivateGuardService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BookingDeactivateGuardService = TestBed.get(BookingDeactivateGuardService);
    expect(service).toBeTruthy();
  });
});
