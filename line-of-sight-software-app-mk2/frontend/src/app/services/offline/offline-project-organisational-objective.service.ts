import { Injectable } from '@angular/core';
import {BaseProjectOrganisationalObjectiveService} from '../base/base-project-organisational-objective-service';
import {Observable, of} from 'rxjs';
import {ProjectOrganisationalObjective, ProjectOrganisationalObjectiveUpdate} from '../../types/project-organisational-objective';
import {GuidHelper} from '../../helpers/guid-helper';

@Injectable({
  providedIn: 'root'
})
export class OfflineProjectOrganisationalObjectiveService extends BaseProjectOrganisationalObjectiveService {


    delete(pooId: string, projectId: string): Observable<any> {
        const fr = this.projectOrganisationalObjectives.value.find(x => x.id  === pooId);
        this.projectOrganisationalObjectives.next(this.projectOrganisationalObjectives.value.filter(x => x.id !== pooId));
        const jsonData = JSON.stringify(this.projectOrganisationalObjectives.value);
        localStorage.setItem('projectOrganisationalObjectiveData', jsonData);
        return of(fr);
    }

    getProjectOrganisationalObjectives(projectId: string): Observable<Array<ProjectOrganisationalObjective>> {
        const projectOrganisationalObjectiveData = localStorage.getItem('projectOrganisationalObjectiveData');
        if (projectOrganisationalObjectiveData) {
            return of(JSON.parse(projectOrganisationalObjectiveData) as ProjectOrganisationalObjective[]);
        }
        return of([] as ProjectOrganisationalObjective[]);
    }

    loadProjectOrganisationalObjectives(projectId: string): void {
        this.getProjectOrganisationalObjectives(projectId).subscribe(x => {
            this.projectOrganisationalObjectives.next(x);
        });
    }

    save(poo: ProjectOrganisationalObjective, projectId: string): Observable<ProjectOrganisationalObjectiveUpdate> {
        console.log(poo)
        if (poo.id === '') {
            poo.id = GuidHelper.getGuid();
            poo.oirs[0].id = GuidHelper.getGuid()
            this.projectOrganisationalObjectives.next([...this.projectOrganisationalObjectives.value, ...[poo]]);
        } else {
            const update = this.projectOrganisationalObjectives.value;
            poo.oirs.map(oir => {
                if (oir.id === '') {
                    oir.id = GuidHelper.getGuid()
                }
            })
            update[update.findIndex(x => x.id === poo.id)] = poo;
            this.projectOrganisationalObjectives.next(update);
        }
        const jsonData = JSON.stringify(this.projectOrganisationalObjectives.value);

        console.log(jsonData)
        localStorage.setItem('projectOrganisationalObjectiveData', jsonData);
        const oir_ids: string[] = [];
        poo.oirs.forEach((oir) => oir_ids.push(oir.id));
        console.log(poo.oirs)
        const pooUpdate = {
            id: poo.id,
            oo_version_id: poo.oo_version_id,
            oo_is_deleted: poo.oo_is_deleted,
            oo_versions: poo.oo_versions,
            oirs: poo.oirs,
            frs: poo.frs
        };
        return of(pooUpdate);
    }

}
