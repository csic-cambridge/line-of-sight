import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FirsService {

    private serviceUrl: string;

    constructor(
        private http: HttpClient
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/firs';
    }

    get(): Observable<Array<string>> {
        return this.http.get<Array<string>>(this.serviceUrl);
    }
}
