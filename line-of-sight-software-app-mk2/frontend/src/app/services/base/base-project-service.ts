import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Project} from '../../types/project';
import {Oir, OrganisationalObjective} from '../../types/organisational-objective';
import {ProjectOrganisationalObjective} from '../../types/project-organisational-objective';

export abstract class BaseProjectService {

    public projects: BehaviorSubject<Project[]> = new BehaviorSubject<Project[]>([]);
    public objectives: BehaviorSubject<ProjectOrganisationalObjective[]> = new BehaviorSubject<ProjectOrganisationalObjective[]>([]);
    constructor(http: HttpClient) {
    }

    abstract getProjects(): Observable<Project[]>;

    abstract save(project: Project): Observable<Project>;

    abstract rename(project: Project): Observable<Project> ;

    abstract copy(projectIdToCopy: string, copyName: string): Observable<Project>;

    abstract delete(projectId: string): Observable<any>;

    abstract getProjectIdUrlPath(projectId: string): string;

    importAirs(data: string, projectId: string): boolean {
        const project = this.projects.value.find(x => x.id === projectId);
        if (project) {
            const all = data.split('\r\n');

            // const dictItems = all.map(x => {
            //     const split = x.split(',');
            //     return {id: split[0], text: split[1]} as ;
            // });
            return true;
        }
        else{
            return false;
        }
    }
    importFirs(data: string, projectId: string): boolean {
        const project = this.projects.value.find(x => x.id === projectId);
        if (project) {
            const all = data.split('\r\n');

            const oirs = all.map(x => {
                const split = x.split(',');
                return {id: split[0], oir: split[1]} as Oir;
            });
            return true;
        }
        else{
            return false;
        }
    }
}
