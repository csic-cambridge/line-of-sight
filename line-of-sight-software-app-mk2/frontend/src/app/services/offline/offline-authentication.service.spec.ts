import { TestBed } from '@angular/core/testing';

import { OfflineAuthenticationService } from './offline-authentication.service';

describe('OfflineAuthenticationService', () => {
  let service: OfflineAuthenticationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineAuthenticationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
