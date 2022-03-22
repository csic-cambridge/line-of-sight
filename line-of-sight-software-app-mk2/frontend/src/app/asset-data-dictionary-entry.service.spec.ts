import { TestBed } from '@angular/core/testing';

import { AssetDataDictionaryEntryService } from './asset-data-dictionary-entry.service';

describe('AssetDataDictionaryEntryService', () => {
  let service: AssetDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AssetDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
