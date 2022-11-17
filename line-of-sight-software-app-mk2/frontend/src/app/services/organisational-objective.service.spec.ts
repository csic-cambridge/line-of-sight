import { TestBed } from '@angular/core/testing';

import { OrganisationalObjectiveService } from './organisational-objective.service';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';

describe('OrganisationalObjectiveService', () => {
  let service: OrganisationalObjectiveService;

  beforeEach(() => {
    TestBed.configureTestingModule({
        imports: [HttpClientModule, RouterTestingModule]
    });
    service = TestBed.inject(OrganisationalObjectiveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
