import { TestBed } from '@angular/core/testing';

import { OfflineAssetDataDictionaryEntryService } from './offline-asset-data-dictionary-entry.service';

describe('OfflineAssetDataDictionaryEntryService', () => {
  let service: OfflineAssetDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineAssetDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
