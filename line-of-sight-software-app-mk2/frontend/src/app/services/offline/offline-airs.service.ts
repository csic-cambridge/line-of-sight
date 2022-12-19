import { Injectable } from '@angular/core';
import {BaseAirsService} from '../base/base-airs-service';
import {Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OfflineAirsService extends BaseAirsService {
    private airs: string[] = [];
  constructor() {super(); }

    get(): Observable<Array<string>> {
        const airsData = localStorage.getItem('airsData');
        if (airsData) {
            this.airs = JSON.parse(airsData) as string[];
        }
        return of(this.airs.map(x => x.split(',')[1].trim()).filter((v, i, a) => a.indexOf(v) === i));
    }
    import(data: string): boolean {
      const array = data.split(/\r\n|\r|\n/).filter(x => !this.airs.includes(x));
      this.airs = [...this.airs, ...array];
      const jsonData = JSON.stringify(this.airs);
      localStorage.setItem('airsData', jsonData);
      return true;
    }
}
