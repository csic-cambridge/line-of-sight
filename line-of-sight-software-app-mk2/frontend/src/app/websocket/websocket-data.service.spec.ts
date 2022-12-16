import { TestBed } from '@angular/core/testing';

import { WebsocketDataService } from './websocket-data.service';

describe('WebsocketDataServiceService', () => {
  let service: WebsocketDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WebsocketDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
