import { Injectable } from '@angular/core';
import { Observable } from "rxjs";
import { UserPermissions } from "./user-permissions";
import { HttpClient } from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root'
})

export class UserPermissionService {
    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/user-permissions';
    }

    getPermissions(userId: string): Observable<UserPermissions> {
        return this.http.get<UserPermissions>(this.serviceUrl + '/' + userId);
    }

    save(userPermissions: UserPermissions): Observable<UserPermissions> {
        console.log("Storing user permissions for user: " + userPermissions.user_id);
        return this.http.put<UserPermissions>(this.serviceUrl + '/' + userPermissions.user_id, userPermissions);
    }

}