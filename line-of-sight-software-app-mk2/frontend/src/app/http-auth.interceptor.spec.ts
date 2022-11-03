import { TestBed } from '@angular/core/testing';
import { HttpAuthInterceptor } from './http-auth.interceptor';
import {HttpClientModule} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';

describe('HttpAuthInterceptorInterceptor', () => {
    beforeEach(() => TestBed.configureTestingModule({
        imports: [RouterTestingModule, HttpClientModule],
        providers: [HttpAuthInterceptor]
    }));

    it('should be created', () => {
        const interceptor: HttpAuthInterceptor = TestBed.inject(HttpAuthInterceptor);
        expect(interceptor).toBeTruthy();
    });
});
