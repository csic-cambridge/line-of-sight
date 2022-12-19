import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export abstract class BaseAuthenticationService {
    constructor() {
    }

    abstract login(): void;
    abstract getProvider(): Observable<any>;
    abstract logout(): Observable<any>;
}
