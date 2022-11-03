import { TestBed } from '@angular/core/testing';

import { FunctionalOutputDataDictionaryEntryService } from './functional-output-data-dictionary-entry.service';
import {HttpClientModule} from '@angular/common/http';

describe('FunctionalOutputDataDictionaryEntryService', () => {
  let service: FunctionalOutputDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule]
    });
    service = TestBed.inject(FunctionalOutputDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
