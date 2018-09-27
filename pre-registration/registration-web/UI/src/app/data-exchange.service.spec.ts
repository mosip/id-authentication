import { TestBed, inject } from '@angular/core/testing';

import { DataExchangeService } from './data-exchange.service';

describe('DataExchangeService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataExchangeService]
    });
  });

  it('should be created', inject([DataExchangeService], (service: DataExchangeService) => {
    expect(service).toBeTruthy();
  }));
});
