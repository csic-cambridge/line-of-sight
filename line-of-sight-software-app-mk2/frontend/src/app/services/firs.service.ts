import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {BaseFirsService} from './base/base-firs-service';

@Injectable({
  providedIn: 'root'
})
export class FirsService extends BaseFirsService {

    private serviceUrl: string;

    constructor(private http: HttpClient) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/firs';
    }

    get(): Observable<Array<string>> {
        return this.http.get<Array<string>>(this.serviceUrl);
    }
}
