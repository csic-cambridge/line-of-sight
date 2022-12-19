import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export abstract class BaseAirsService {

    constructor() {
    }

    abstract get(): Observable<Array<string>>;
}
