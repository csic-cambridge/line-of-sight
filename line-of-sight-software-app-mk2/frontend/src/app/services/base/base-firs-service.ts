import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export abstract class BaseFirsService {

    constructor() {
    }

    abstract get(): Observable<Array<string>>;
}
