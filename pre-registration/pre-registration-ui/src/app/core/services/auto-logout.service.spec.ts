import { TestBed } from '@angular/core/testing';

import { AutoLogoutService } from './auto-logout.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

describe('AutoLogoutService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HttpClient,
        {
          provide: Router,
          useValue: '/path'
        }
      ]
    })
  });

  it('should be created', () => {
    const service: AutoLogoutService = TestBed.get(AutoLogoutService);
    expect(service).toBeTruthy();
  });
});
