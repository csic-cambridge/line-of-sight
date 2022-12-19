import { Injectable } from '@angular/core';
import {BaseProjectService} from '../base/base-project-service';
import {Observable, of} from 'rxjs';
import {GuidHelper} from '../../helpers/guid-helper';
import {BaseFunctionalOutputService} from '../base/base-functional-output-service';
import {FunctionalOutput} from '../../types/functional-output';

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
            this.functionalOutputs.next([...this.functionalOutputs.value, ...[functionalOutput]]);
        } else {
            const update = this.functionalOutputs.value;
            update[update.findIndex(x => x.id === functionalOutput.id)] = functionalOutput;
            this.functionalOutputs.next(update);
        }
        const jsonData = JSON.stringify(this.functionalOutputs.value);
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
