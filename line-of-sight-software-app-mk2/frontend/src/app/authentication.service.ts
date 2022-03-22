import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
    private serviceUrl;
    private logoutServiceurl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/login';
        this.logoutServiceurl = environment.apiBaseUrl + '/logout';

    }

    login(values: any): Observable<any> {
        console.log('Authentication service login for user', values.username);
        const params = new HttpParams().append('username', values.username).append('password', values.password);

        return this.http.post<any>(this.serviceUrl, params);
    }

    logout(): Observable<any> {
        console.log('Authentication service logout');
        return this.http.post<any>(this.logoutServiceurl, null);
    }
}
