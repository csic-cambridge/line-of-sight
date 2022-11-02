import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {DataDictionaryEntry} from "./data-dictionary-entry";
import {shareReplay} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class AssetDataDictionaryEntryService {

    private serviceUrl;
    private cache$: Observable<Array<DataDictionaryEntry>> | undefined;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/asset-data-dictionary';
    }

    getAssetDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>> {
        if(!this.cache$) {
            console.log("Caching asset data dictionary entries");
            this.cache$ = this.http.get<Array<DataDictionaryEntry>>(this.serviceUrl + '/' + projectId).pipe(shareReplay());
        }
        return this.cache$;
    }

}
