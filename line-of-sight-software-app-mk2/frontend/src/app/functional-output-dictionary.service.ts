import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {shareReplay} from "rxjs/operators";
import {FunctionalOutputDictionary} from "./functional-output-dictionary";

@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputDictionaryService {
    private serviceUrl;
    private cache$: Observable<Array<FunctionalOutputDictionary>> | undefined;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/functional-output-data-dictionary';
    }

    getFunctionalOutputDictionaries(): Observable<Array<FunctionalOutputDictionary>> {
        if (!this.cache$) {
            this.cache$ = this.http.get<Array<FunctionalOutputDictionary>>(this.serviceUrl).pipe(shareReplay());
        }
        return this.cache$;
    }

    getFODDEntries(): Observable<Array<FunctionalOutputDictionary>> {
        return this.http.get<Array<FunctionalOutputDictionary>>(this.serviceUrl);
    }
}
