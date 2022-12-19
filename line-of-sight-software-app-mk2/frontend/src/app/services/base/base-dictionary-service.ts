import {BehaviorSubject, Observable} from 'rxjs';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';

export abstract class BaseDictionaryService<T> {
    public dictionaries: BehaviorSubject<T[]> = new BehaviorSubject<T[]>([]);
    public dictionaryItems: BehaviorSubject<DataDictionaryEntry[]> = new BehaviorSubject<DataDictionaryEntry[]>([]);
    constructor() {
    }

    abstract getDictionaries(): Observable<Array<T>>;
    abstract addDictionary(data: string): boolean;

}
