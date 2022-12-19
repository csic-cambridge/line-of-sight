import { TestBed } from '@angular/core/testing';

import { OfflineFunctionalOutputService } from './offline-functional-output.service';

describe('OfflineFunctionalOutputService', () => {
  let service: OfflineFunctionalOutputService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineFunctionalOutputService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
