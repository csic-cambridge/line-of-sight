import { Injectable } from '@angular/core';
import {BaseProjectService} from '../base/base-project-service';
import {Observable, of} from 'rxjs';
import {GuidHelper} from '../../helpers/guid-helper';
import {BaseFunctionalOutputService} from '../base/base-functional-output-service';
import {FunctionalOutput} from '../../types/functional-output';
import {Airs} from "../../types/airs";
import {Firs} from "../../types/firs";

@Injectable({
  providedIn: 'root'
})
export class OfflineFunctionalOutputService extends BaseFunctionalOutputService {

    constructor(private ps: BaseProjectService) {
        super(ps);
    }

    getFunctionalOutputs(projectId: string): Observable<Array<FunctionalOutput>> {
        const functionalOutputData = localStorage.getItem('functionalOutputData');
        if (functionalOutputData) {
            return of(JSON.parse(functionalOutputData) as FunctionalOutput[]);
        }
        return of([] as FunctionalOutput[]);
    }

    loadFunctionalOutputs(projectId: string): void {
        this.getFunctionalOutputs(projectId).subscribe(x => {
            this.functionalOutputs.next(x);
        });
    }

    save(functionalOutput: FunctionalOutput, projectId: string): Observable<FunctionalOutput> {
        if (functionalOutput.id === '') {
            functionalOutput.id = GuidHelper.getGuid();
            functionalOutput.firs = functionalOutput.firs.map((f: any) => {
               if (typeof f.firs === 'object' && f.firs !== null){
                   let newF = {
                       id: f.id,
                       firs: f.firs.firs
                   }
                   return newF
               } else {
                   let fDot = {
                       id:  GuidHelper.getGuid(),
                       firs: f.firs
                   }
                   return fDot
               }
            })
            this.functionalOutputs.next([...this.functionalOutputs.value, ...[functionalOutput]]);
        } else {
            const update = this.functionalOutputs.value;
            functionalOutput.firs = functionalOutput.firs.map((f: any) => {
                if (typeof f.firs === 'object' && f.firs !== null){
                    let newF = {
                        id: f.id,
                        firs: f.firs.firs
                    }
                    return newF
                } else {
                    let fDot = {
                        id:  GuidHelper.getGuid(),
                        firs: f.firs
                    }
                    return fDot
                }
            })
            update[update.findIndex(x => x.id === functionalOutput.id)] = functionalOutput;
            this.functionalOutputs.next(update);
        }

        let firs = this.functionalOutputs.value.map((fo) => fo.firs)
        firs.map((f: any) => {
            if (f.firs) f = f.firs
            return f
        })
        const firsJson = JSON.stringify(firs.flat())
        const jsonData = JSON.stringify(this.functionalOutputs.value);
        localStorage.setItem('firsData', firsJson)
        localStorage.setItem('functionalOutputData', jsonData);
        return  of(functionalOutput);
    }

    delete(foId: string, projectId: string): Observable<any> {
        const asset = this.functionalOutputs.value.find(x => x.id  === foId);
        this.functionalOutputs.next(this.functionalOutputs.value.filter(x => x.id !== foId));
        const jsonData = JSON.stringify(this.functionalOutputs.value);
        localStorage.setItem('functionalOutputData', jsonData);
        return of(asset);
    }
}
