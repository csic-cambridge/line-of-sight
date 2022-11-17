import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';

export abstract class BaseDictionaryService<T> {
    public dictionaries: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    public dictionaryItems: BehaviorSubject<DataDictionaryEntry[]> = new BehaviorSubject<DataDictionaryEntry[]>([]);
    public serviceUrl: string;
    constructor(protected http: HttpClient, protected actionUrl: string) {
        this.serviceUrl = `${environment.apiBaseUrl}/api/${actionUrl}`;
    }

    abstract getDictionaries(): Observable<Array<T>>;
    abstract addDictionary(data: string): boolean;

}
