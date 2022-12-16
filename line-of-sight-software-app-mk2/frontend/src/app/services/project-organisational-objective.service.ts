import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {ProjectOrganisationalObjective} from '../types/project-organisational-objective';
import {ProjectOrganisationalObjectiveUpdate} from '../types/project-organisational-objective';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {BaseProjectService} from './base/base-project-service';
import {BaseOrganisationalObjectiveService} from './base/base-organisational-objective-service';
import {OrganisationalObjective} from '../types/organisational-objective';

@Injectable({
    providedIn: 'root'
})
export class ProjectOrganisationalObjectiveService {
    private serviceUrl;

    public projectOrganisationalObjectives: BehaviorSubject<ProjectOrganisationalObjective[]> =
        new BehaviorSubject<ProjectOrganisationalObjective[]>([]);

    constructor(private http: HttpClient,
                private ps: BaseProjectService) {
        this.serviceUrl = environment.apiBaseUrl + '/api/project-organisational-objectives';
    }
    getProjectOrganisationalObjectives(projectId: string): Observable<Array<ProjectOrganisationalObjective>> {
        return this.http.get<Array<ProjectOrganisationalObjective>>(this.serviceUrl + '/' +
            this.ps.getProjectIdUrlPath(projectId));
    }
    loadProjectOrganisationalObjectives(projectId: string): void {
        this.http.get<Array<ProjectOrganisationalObjective>>(this.serviceUrl + '/' +
            this.ps.getProjectIdUrlPath(projectId)).subscribe(data => {
            this.projectOrganisationalObjectives.next(data);
        });
    }

    save(poo: ProjectOrganisationalObjective, projectId: string): Observable<ProjectOrganisationalObjectiveUpdate> {
        const oir_ids: string[] = [];
        poo.oirs.forEach((oir) => oir_ids.push(oir.id));
        const pooUpdate = {
            id: poo.id,
            oo_version_id: poo.oo_version_id,
            oo_is_deleted: poo.oo_is_deleted,
            oo_versions: poo.oo_versions,
            oir_ids: oir_ids.flat(),
            frs: poo.frs
        };
        return this.http.put<ProjectOrganisationalObjectiveUpdate>
                    (this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId) + '/' + poo.id, pooUpdate);
    }

    delete(pooId: string, projectId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + this.ps.getProjectIdUrlPath(projectId) + '/' + pooId);
    }

}
