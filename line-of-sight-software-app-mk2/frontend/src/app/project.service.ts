import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Project } from './project';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})

export class ProjectService {
    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/project';
    }

    getProjects(): Observable<Project[]> {
        return this.http.get<Project[]>(this.serviceUrl);
    }

    save(project: Project): Observable<Project> {
        return this.http.post<Project>(this.serviceUrl, project);
    }

    rename(project: Project): Observable<Project> {
        return this.http.put<Project>(this.serviceUrl + '/' + this.getProjectIdUrlPath(project.id), project);
    }

    copy(projectIdToCopy: string, copyName: string): Observable<Project> {
        const map = {name : copyName};
        return this.http.post<Project>(this.serviceUrl + '/' + this.getProjectIdUrlPath(projectIdToCopy), map);
    }

    delete(projectId: string): Observable<any> {
        return this.http.delete(this.serviceUrl + '/' + this.getProjectIdUrlPath(projectId));
    }

    getProjectIdUrlPath(projectId: string): string {
        return 'pid/' + projectId;
    }
}