import { Injectable } from '@angular/core';
import {BaseFunctionalRequirementService} from '../base/base-functional-requirement-service';
import {BaseProjectService} from '../base/base-project-service';
import {Observable, of} from 'rxjs';
import {FunctionalRequirement} from '../../types/functional-requirement';
import {GuidHelper} from '../../helpers/guid-helper';

@Injectable({
  providedIn: 'root'
})
export class OfflineFunctionalRequirementService extends BaseFunctionalRequirementService {

  constructor(private ps: BaseProjectService) {
      super(ps);
  }

    delete(frId: string, projectId: string): Observable<any> {
        const fr = this.functionalRequirements.value.find(x => x.id  === frId);
        this.functionalRequirements.next(this.functionalRequirements.value.filter(x => x.id !== frId));
        const jsonData = JSON.stringify(this.functionalRequirements.value);
        localStorage.setItem('functionalRequirementData', jsonData);
        return of(fr);
    }

    getFunctionalRequirements(projectId: string): Observable<Array<FunctionalRequirement>> {
        const functionalRequirementData = localStorage.getItem('functionalRequirementData');
        if (functionalRequirementData) {
            return of(JSON.parse(functionalRequirementData) as FunctionalRequirement[]);
        }
        return of([] as FunctionalRequirement[]);
    }

    loadFunctionalRequirements(projectId: string): void {
        this.getFunctionalRequirements(projectId).subscribe(x => {
            this.functionalRequirements.next(x);
        });
    }

    save(functionalRequirement: FunctionalRequirement, projectId: string): Observable<FunctionalRequirement> {
        if (functionalRequirement.id === '') {
            functionalRequirement.id = GuidHelper.getGuid();
            this.functionalRequirements.next([...this.functionalRequirements.value, ...[functionalRequirement]]);
        } else {
            const update = this.functionalRequirements.value;
            update[update.findIndex(x => x.id === functionalRequirement.id)] = functionalRequirement;
            this.functionalRequirements.next(update);
        }
        const jsonData = JSON.stringify(this.functionalRequirements.value);
        localStorage.setItem('functionalRequirementData', jsonData);
        return  of(functionalRequirement);
    }
}
