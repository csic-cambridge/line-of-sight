import { TestBed } from '@angular/core/testing';

import { OfflineAssetDictionaryService } from './offline-asset-dictionary.service';

describe('OfflineAssetDictionaryService', () => {
  let service: OfflineAssetDictionaryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineAssetDictionaryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
