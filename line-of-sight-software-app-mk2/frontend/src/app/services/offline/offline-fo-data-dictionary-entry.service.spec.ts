import { TestBed } from '@angular/core/testing';

import { OfflineFoDataDictionaryEntryService } from './offline-fo-data-dictionary-entry.service';

describe('OfflineFoDataDictionaryEntryService', () => {
  let service: OfflineFoDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OfflineFoDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
