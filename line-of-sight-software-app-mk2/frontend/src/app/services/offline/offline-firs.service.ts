import { Injectable } from '@angular/core';
import {BaseAirsService} from '../base/base-airs-service';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OfflineFirsService extends BaseAirsService {
    private firs: string[] = [];
    constructor() {super(); }

    get(): Observable<Array<string>> {
        const airsData = localStorage.getItem('firsData');
        if (airsData) {
            this.firs = JSON.parse(airsData) as string[];
        }
        return of(this.firs.map(x => x.split(',')[1].trim()).filter((v, i, a) => a.indexOf(v) === i));
    }
    import(data: string): boolean {
        const array = data.split(/\r\n|\r|\n/).filter(x => !this.firs.includes(x));
        this.firs = [...this.firs, ...array];
        const jsonData = JSON.stringify(this.firs);
        localStorage.setItem('firsData', jsonData);
        return true;
    }
}
