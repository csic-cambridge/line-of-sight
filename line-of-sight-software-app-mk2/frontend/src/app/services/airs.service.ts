import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BaseAirsService} from './base/base-airs-service';

@Injectable({
  providedIn: 'root'
})
export class AirsService extends BaseAirsService {

    private serviceUrl: string;

    constructor(
        private http: HttpClient
    ) {
        super();
        this.serviceUrl = environment.apiBaseUrl + '/api/airs';
    }

    get(): Observable<Array<string>> {
        return this.http.get<Array<string>>(this.serviceUrl);
    }
}
