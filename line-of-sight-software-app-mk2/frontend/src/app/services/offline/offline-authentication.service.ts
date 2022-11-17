import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {BaseAuthenticationService} from '../base/base-authentication.service';
import {LoginProvider} from '../../types/login-provider';

@Injectable({
  providedIn: 'root'
})
export class OfflineAuthenticationService extends BaseAuthenticationService {

    constructor(
        private router: Router,
        private http: HttpClient) {
        super(router, http);
    }

    public login(): void {
        this.router.navigate(['/login']);
    }

    public getProvider(): Observable<any> {
        const loginrovider = {url: '/dashboard', name: 'Local login'} as LoginProvider;
        return of([loginrovider]);
    }

    logout(): Observable<any> {
        return of('good bye');
    }
}
