import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {AssetDictionary} from "./asset-dictionary";

@Injectable({
    providedIn: 'root'
})
export class AssetDictionaryService {

    private serviceUrl;
    private cache$: Observable<Array<AssetDictionary>> | undefined;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/asset-data-dictionary';
    }

    // getAssetDictionaryEntries(): Observable<Array<AssetDictionary>> {
    //     if(!this.cache$) {
    //         console.log("Caching asset data dictionary entries");
    //         this.cache$ = this.http.get<Array<AssetDictionary>>(this.serviceUrl).pipe(shareReplay());
    //     }
    //     return this.cache$;
    // }

    getAssetDictionaries() : Observable<Array<AssetDictionary>> {
        return this.http.get<Array<AssetDictionary>>(this.serviceUrl);
    }

}