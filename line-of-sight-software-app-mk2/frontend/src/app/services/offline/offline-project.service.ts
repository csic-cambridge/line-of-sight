import { Injectable } from '@angular/core';
import {BaseProjectService} from '../base/base-project-service';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {Project} from '../../types/project';
import {NgxIndexedDBService} from 'ngx-indexed-db';
import {GuidHelper} from '../../helpers/guid-helper';

@Injectable({
  providedIn: 'root'
})
export class OfflineProjectService extends BaseProjectService {

    constructor(private http: HttpClient, private dbService: NgxIndexedDBService) {
        super(http);
    }

    getProjects(): Observable<Project[]> {
        this.dbService.getAll('project').subscribe((items) => {
            this.projects.next(items as Project[]);
        }, (err) => this.projects);
        return this.projects;
    }

    save(project: Project): Observable<Project> {

        if (project.id === '') {
            project.id = GuidHelper.getGuid();
            this.dbService
                .add('project', project)
                .subscribe((key) => {});
            this.projects.next([...this.projects.value, ...[project]]);
        }
        return of(project);
    }

    rename(project: Project): Observable<Project> {
        return of(project);
    }

    copy(projectIdToCopy: string, copyName: string): Observable<Project> {
        const project = {id: projectIdToCopy, name: copyName} as Project;
        return of(project);
    }

    delete(projectId: string): Observable<any> {
        return of(projectId);
    }

    getProjectIdUrlPath(projectId: string): string {
        return 'pid/' + projectId;
    }
}
