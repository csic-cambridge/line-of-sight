import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AssetDictionary} from '../types/asset-dictionary';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {BaseAssetDictionaryService} from './base/base-asset-dictionary-service';

@Injectable({
    providedIn: 'root'
})
export class AssetDictionaryService extends BaseAssetDictionaryService {
    private serviceUrl: string;
    constructor(private http: HttpClient) {
        super();
        this.serviceUrl = `${environment.apiBaseUrl}/api/asset-data-dictionary`;
    }
    getDictionaries(): Observable<AssetDictionary[]> {
        this.http.get<AssetDictionary[]>(this.serviceUrl)
            .subscribe(x => this.dictionaries.next(x));
        return this.dictionaries;
    }
    addDictionary(data: string): boolean {
        return false;
    }
}
