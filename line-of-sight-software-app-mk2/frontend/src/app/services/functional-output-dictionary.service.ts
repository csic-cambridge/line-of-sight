import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FunctionalOutputDictionary} from '../types/functional-output-dictionary';
import {BaseDictionaryService} from './base/base-dictionary-service';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FunctionalOutputDictionaryService extends BaseDictionaryService<FunctionalOutputDictionary>{
    constructor(http: HttpClient) {
        super(http, 'functional-output-data-dictionary');
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
