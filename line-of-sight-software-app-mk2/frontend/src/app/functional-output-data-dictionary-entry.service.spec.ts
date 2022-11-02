import { TestBed } from '@angular/core/testing';

import { FunctionalOutputDataDictionaryEntryService } from './functional-output-data-dictionary-entry.service';

describe('FunctionalOutputDataDictionaryEntryService', () => {
  let service: FunctionalOutputDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FunctionalOutputDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
