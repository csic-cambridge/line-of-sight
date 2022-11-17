import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../types/user';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})

export class UserService {
    private serviceUrl;

    constructor(
        private http: HttpClient,
    ) {
        this.serviceUrl = environment.apiBaseUrl + '/api/user';
    }

    getUsers(): Observable<Array<User>> {
        return this.http.get<Array<User>>(this.serviceUrl);
    }

    getUser(userId: string): Observable<User> {
        return this.http.get<User>(this.serviceUrl + '/' + userId);
    }

    getLoggedInUser(): Observable<User> {
        return this.http.get<User>(this.serviceUrl);
    }

    save(user: User): Observable<User> {
        return this.http.put<User>(this.serviceUrl + '/' + user.user_id, user);
    }

}