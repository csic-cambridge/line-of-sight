import { TestBed } from '@angular/core/testing';

import { IoService } from './io.service';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';

describe('IoService', () => {
  let service: IoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule]
    });
    service = TestBed.inject(IoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
