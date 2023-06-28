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
            this.airs = JSON.parse(airsData);
        }
        return of(this.airs);
    }
    import(data: string): boolean {
      const array = data.split(/\r\n|\r|\n/).filter(x => !this.airs.includes(x));
      console.log(array)
      this.airs = [...this.airs, ...array];
      const jsonData = JSON.stringify(this.airs);
      localStorage.setItem('airsData', jsonData);
      return true;
    }
}
