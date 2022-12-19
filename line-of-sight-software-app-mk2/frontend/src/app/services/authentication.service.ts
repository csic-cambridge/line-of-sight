import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import { Router } from '@angular/router';
import {BaseAuthenticationService} from './base/base-authentication.service';
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService extends BaseAuthenticationService {
    private getProvidersUrl;
    private logoutUrl;

    constructor(
        private router: Router,
        private http: HttpClient
    ) {
        super();
        this.getProvidersUrl = environment.apiBaseUrl + '/api/oauth-providers';
        this.logoutUrl = environment.apiBaseUrl + '/logout';
    }

    public login(): void {
        this.router.navigate(['/login']);
    }

    public getProvider(): Observable<any> {
        return this.http.post<any>(this.getProvidersUrl, null);
    }

    logout(): Observable<any> {
        return this.http.post<any>(this.logoutUrl, null);
    }
}
