import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FunctionalOutputDictionary} from '../types/functional-output-dictionary';
import {Observable} from 'rxjs';
import {BaseFunctionalOutputDictionaryService} from './base/base-functional-output-dictionary-service';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputDictionaryService extends BaseFunctionalOutputDictionaryService {
    private serviceUrl: string;
    constructor(private http: HttpClient) {
        super();
        this.serviceUrl = `${environment.apiBaseUrl}/api/functional-output-data-dictionary`;
    }
    getDictionaries(): Observable<FunctionalOutputDictionary[]> {
        this.http.get<FunctionalOutputDictionary[]>(this.serviceUrl)
            .subscribe(x => this.dictionaries.next(x));
        return this.dictionaries;
    }

    addDictionary(data: string): boolean {
        return false;
    }
}
