import { TestBed } from '@angular/core/testing';

import { OfflineProjectDataService } from './offline-project-data.service';

describe('OfflineProjectDataService', () => {
  let service: OfflineProjectDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineProjectDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
