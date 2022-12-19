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
        private router: Router) {
        super();
    }

    public login(): void {
        this.router.navigate(['/login']);
    }

    public getProvider(): Observable<any> {
        const loginrovider = {url: 'project', name: 'Showcase login'} as LoginProvider;
        return of([loginrovider]);
    }

    logout(): Observable<any> {
        return of('good bye');
    }
}
