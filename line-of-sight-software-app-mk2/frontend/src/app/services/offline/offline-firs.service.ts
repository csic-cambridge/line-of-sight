import { Injectable } from '@angular/core';
import {BaseAirsService} from '../base/base-airs-service';
import {Observable, of} from 'rxjs';
import {Firs} from "../../types/firs";

@Injectable({
  providedIn: 'root'
})
export class OfflineFirsService extends BaseAirsService {
    private firs: string[] = [];
    constructor() {super(); }

    get(): Observable<Array<string>> {
        const firsData = localStorage.getItem('firsData');
        if (firsData) {
            this.firs =JSON.parse(firsData)
        }
        return of(this.firs);
    }
    import(data: string): boolean {
        const array = data.split(/\r\n|\r|\n/).filter(x => !this.firs.includes(x));
        this.firs = [...this.firs, ...array];
        const jsonData = JSON.stringify(this.firs);
        localStorage.setItem('firsData', jsonData);
        return true;
    }
}
