import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AssetDictionary} from '../types/asset-dictionary';
import {BaseDictionaryService} from './base/base-dictionary-service';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AssetDictionaryService extends BaseDictionaryService<AssetDictionary>{
    constructor(http: HttpClient) {
        super(http, 'asset-data-dictionary');
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
