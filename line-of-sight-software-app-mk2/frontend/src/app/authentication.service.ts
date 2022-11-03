import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
    private getProvidersUrl;
    private logoutUrl;

    constructor(
        private router: Router,
        private http: HttpClient, private cookieService: CookieService
    ) {
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
        console.log('Authentication service: logout');
        return this.http.post<any>(this.logoutUrl, null);
    }
}
