import { TestBed } from '@angular/core/testing';
import { AssetDataDictionaryEntryService } from './asset-data-dictionary-entry.service';
import {HttpClientModule} from '@angular/common/http';

describe('AssetDataDictionaryEntryService', () => {
  let service: AssetDataDictionaryEntryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule]
    });
    service = TestBed.inject(AssetDataDictionaryEntryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
