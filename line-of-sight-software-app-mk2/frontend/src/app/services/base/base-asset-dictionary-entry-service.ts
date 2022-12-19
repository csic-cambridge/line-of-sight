import {BehaviorSubject, Observable} from 'rxjs';
import {DataDictionaryEntry} from '../../types/data-dictionary-entry';

export abstract class BaseAssetDictionaryEntryService {
    public entries$: BehaviorSubject<DataDictionaryEntry[]> = new BehaviorSubject<DataDictionaryEntry[]>([]);
    constructor() {
    }

    abstract getDataDictionaryEntries(projectId: string): Observable<Array<DataDictionaryEntry>>;
    abstract load(projectId: string): void;
}
