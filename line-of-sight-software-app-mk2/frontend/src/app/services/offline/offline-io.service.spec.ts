import { TestBed } from '@angular/core/testing';

import { OfflineIoService } from './offline-io.service';

describe('OfflineIoService', () => {
  let service: OfflineIoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineIoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
