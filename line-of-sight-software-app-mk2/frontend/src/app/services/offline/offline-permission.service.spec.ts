import { TestBed } from '@angular/core/testing';

import { OfflinePermissionService } from './offline-permission.service';

describe('OfflinePermissionService', () => {
  let service: OfflinePermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflinePermissionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
