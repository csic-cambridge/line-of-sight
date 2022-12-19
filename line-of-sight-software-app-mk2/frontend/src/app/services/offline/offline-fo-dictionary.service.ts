import { Injectable } from '@angular/core';
import {FunctionalOutputDictionary} from '../../types/functional-output-dictionary';
import {Observable, of} from 'rxjs';
import {BaseFunctionalOutputDictionaryService} from '../base/base-functional-output-dictionary-service';

@Injectable({
  providedIn: 'root'
})
export class OfflineFoDictionaryService  extends BaseFunctionalOutputDictionaryService {
    constructor() {
        super();
    }

    addDictionary(data: string): boolean {
        return false;
    }

    getDictionaries(): Observable<Array<FunctionalOutputDictionary>> {
        return of([] as FunctionalOutputDictionary[]);
    }
}
