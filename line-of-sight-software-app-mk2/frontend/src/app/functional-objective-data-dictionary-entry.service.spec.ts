import { TestBed } from '@angular/core/testing';

import { FunctionalObjectiveDataDictionaryEntryService } from './functional-objective-data-dictionary-entry.service';

describe('FunctionalObjectiveDataDictionaryEntryService', () => {
  let service: FunctionalObjectiveDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FunctionalObjectiveDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
