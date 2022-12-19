import { TestBed } from '@angular/core/testing';

import { OfflineAssetService } from './offline-asset.service';

describe('OfflineAssetService', () => {
  let service: OfflineAssetService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineAssetService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
