import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { Project } from "./project";
import { HttpClient } from "@angular/common/http";
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

    getProjects(): Observable<Array<Project>> {
        return this.http.get<Array<Project>>(this.serviceUrl);
    }

    save(project: Project): Observable<Project> {
        console.log("Project Id to store: ", project.id);
        return this.http.post<Project>(this.serviceUrl, project);
    }

    rename (project: Project): Observable<Project> {
        console.log("Project Id to rename: ", project.id);
        return this.http.put<Project>(this.serviceUrl + '/' + project.id, project);
    }

    copy(projectIdToCopy: string, copyName: string): Observable<Project> {
        console.log("Copying Project with ID: " + projectIdToCopy + " as: " + copyName);
        let map = {"name" : copyName}
        return this.http.post<Project>(this.serviceUrl+ '/' + projectIdToCopy, map);
    }

    delete(projectId: string): Observable<any> {
        console.log("Project Id to delete: ", projectId);
        return this.http.delete(this.serviceUrl + '/' + projectId);
    }
}