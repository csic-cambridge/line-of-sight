import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AirsService {

    private serviceUrl: string;

    constructor(
        private http: HttpClient
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/airs';
    }

    get(): Observable<Array<string>> {
        return this.http.get<Array<string>>(this.serviceUrl);
    }
}
