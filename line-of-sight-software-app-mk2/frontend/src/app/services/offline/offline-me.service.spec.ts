import { TestBed } from '@angular/core/testing';

import { OfflineMeService } from './offline-me.service';

describe('OfflineMeService', () => {
  let service: OfflineMeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineMeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
